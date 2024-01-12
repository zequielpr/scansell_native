package com.kunano.scansell_native.controllers.home;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;

import com.google.android.material.snackbar.Snackbar;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.controllers.ValidateData;
import com.kunano.scansell_native.model.Home.BusinessModel;
import com.kunano.scansell_native.model.db.Business;
import com.kunano.scansell_native.threads.CustomThread;
import com.kunano.scansell_native.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BusinessController {
    private View title;
    private BusinessModel businessesModel;
    private HomeViewModel businessesView;
    private boolean isDeleteModeActive;
    private Drawable checkedCircle;
    private Drawable uncheckedCircle;
    private boolean isAllSelected;
    private HashSet<Business> businessListToDelete;
    private HashSet<Business> deletedBusinessList;
    private List<Business> businessesListData;
    private boolean cancelDeletingProcess;
    private Snackbar snackbar;
    Thread deleteBusinessThread;

   public BusinessController(){
       super();

    }


    public BusinessController(BusinessModel businessModel, HomeViewModel businessesView ){
       super();
        this.businessesModel = businessModel;
        this.businessesView = businessesView;
        checkedCircle = businessesView.getLayoutInflater().getContext().getResources().getDrawable(R.drawable.checked_circle);
        uncheckedCircle = businessesView.getLayoutInflater().getContext().getResources().getDrawable(R.drawable.unchked_circle);
    }

    //Add business
    public CompletableFuture<Boolean> addBusiness(){
        //Verify data
        return businessesModel.addBusiness();
    }


    public void deleteBusinesses(){
        cancelDeletingProcess = false;
        deletedBusinessList = new HashSet<>();
        deleteBusinessThread = new CustomThread(deleteBusinesses);
        deleteBusinessThread.start();
        desactivateDeletMode();
    }

    private void unoDeletedBusinessesOption(){
       snackbar = Snackbar.make(businessesView.getHomeFragment().getView(), businessesView.getHomeFragment().getString(R.string.businesses_deleted_succ),
                Snackbar.LENGTH_LONG);

       snackbar.setAction(businessesView.getHomeFragment().getString(R.string.undo), new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               businessesModel.addBusinessList(new ArrayList<>(deletedBusinessList));
           }
       });
        snackbar.show();
    }



    //Controll view
    public void showData(){
        businessesModel.getBusinessEsList().observe(businessesView.getHomeLifecycleOwner(),
                this::setBusinessesList);

    }

    private void setBusinessesList(List<Business> businesses){
        businessesListData = businesses;
        businessesView.setBusinessList(businesses, this);
    }



    public void wrapTitleContent(){
        ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        businessesView.setTitleWidth(layoutParams);
    }

    public void reduceTitleSpace(int size){
        ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
        layoutParams.width = size;
        businessesView.setTitleWidth(layoutParams);
    }



    public void verifyIfAllCardsAreSelected(){
        if(businessesListData.size() == businessListToDelete.size()){
            isAllSelected = true;
            businessesView.setImageForSeletAllButton(checkedCircle);
            return;
        }
        isAllSelected = false;
        businessesView.setImageForSeletAllButton(uncheckedCircle);
    }

    public  boolean isBusinessTodelete(String businessID){
        if (businessListToDelete == null) return false;
        return businessListToDelete.containsAll(businessesListData.stream().filter(business ->
                String.valueOf(business.businessId).equals(businessID)).collect(Collectors.toList()));
    }

    public void longPressBusinessCard(View businessCard){
        String businessId = businessCard.getTag().toString();
        activateDeletMode();
        checkTouchedCard(businessId);
        verifyIfAllCardsAreSelected();
    }

    public void shortPressBusinessCard(View businessCard){
        String businessId = businessCard.getTag().toString();

        if(isDeleteModeActive){
            if(isBusinessTodelete(businessId)){
                uncheckTouchedCard(businessId);
                verifyIfAllCardsAreSelected();
                if(businessListToDelete.isEmpty())hideDeleteButton();
                return;
            }
            showDeleteButton();
            checkTouchedCard(businessId);
            verifyIfAllCardsAreSelected();
        }

    }



    public void checkAllCards(){
        businessListToDelete.addAll(businessesListData);
        isAllSelected = true;
        businessesView.setImageForSeletAllButton(checkedCircle);
        businessesView.setCircleForAllCards(checkedCircle);
        showDeleteButton();
        updateSelectedBusinessNumb();
    }
    public void uncheckAllCards(){
        isAllSelected = false;
        businessesView.setImageForSeletAllButton(uncheckedCircle);
        businessesView.setCircleForAllCards(uncheckedCircle);
        businessListToDelete.clear();
        hideDeleteButton();
        updateSelectedBusinessNumb();

    }



    private void checkTouchedCard(String businessCardId){
        Map<String, Object> imageAndBusinessId = new HashMap<>();
        imageAndBusinessId.put("businessId", businessCardId);
        imageAndBusinessId.put("checkedCircle", checkedCircle );

        businessListToDelete.addAll(businessesListData.stream().filter(business ->
                String.valueOf(business.businessId).equals(businessCardId)).collect(Collectors.toList()));

        businessesView.setCircleForTouchedCard(imageAndBusinessId);
        updateSelectedBusinessNumb();

    }

    public void uncheckTouchedCard(String businessCardId){
        Map<String, Object> imageAndBusinessId = new HashMap<>();
        imageAndBusinessId.put("businessId", businessCardId);
        imageAndBusinessId.put("checkedCircle", uncheckedCircle );


        businessListToDelete.removeAll(businessesListData.stream().filter(business ->
                String.valueOf(business.businessId).equals(businessCardId)).collect(Collectors.toList()));

        businessesView.setCircleForTouchedCard(imageAndBusinessId);
        updateSelectedBusinessNumb();

    }

    public   MutableLiveData<Map<String, Object>> getImageForTouchedCard(){
        return businessesView.getImageAndBusinessId();
    }

    public void activateDeletMode(){
        businessListToDelete = new HashSet<>();
        setUncheckedCircleVisibility(View.VISIBLE);
        isDeleteModeActive = true;
        reduceTitleSpace(200);
        hideAddBusinessBotton();
        showDeleteButton();
        showCancelDeletModeButton();
        showSelectAllButton();

    }

    public void desactivateDeletMode(){
        updateSelectedBusinessNumb();
        isDeleteModeActive = false;
        setUncheckedCircleVisibility(View.GONE);
        showAddbusinessBotton();
        hideDeleteButton();
        hideCancelDeletModeButton();
        hideSelectAllButton();
        wrapTitleContent();

    }

    private void updateSelectedBusinessNumb(){
       String selectedBusinesses = String.valueOf(businessListToDelete.size()).
               concat(" ").concat(businessesView.getHomeFragment().getString(R.string.selected));
       businessesView.setSelectedBusinesses(selectedBusinesses);
    }



    public Drawable getCheckedCircle() {
        return checkedCircle;
    }

    public void setCheckedCircle(Drawable checkedCircle) {
        this.checkedCircle = checkedCircle;
    }

    public Drawable getUncheckedCircle() {
        return uncheckedCircle;
    }

    public void setUncheckedCircle(Drawable uncheckedCircle) {
        this.uncheckedCircle = uncheckedCircle;
    }
    public HashSet<Business> getBusinessListToDelete() {
        return businessListToDelete;
    }

    public void setBusinessListToDelete(HashSet<Business> businessListToDelete) {
        this.businessListToDelete = businessListToDelete;
    }

    public boolean isCancelDeletingProcess() {
        return cancelDeletingProcess;
    }

    public void setCancelDeletingProcess(boolean cancelDeletingProcess) {
        this.cancelDeletingProcess = cancelDeletingProcess;
    }
    public HomeViewModel getBusinessesView() {
        return businessesView;
    }

    public void setBusinessesView(HomeViewModel businessesView) {
        this.businessesView = businessesView;
    }
    public boolean isAllSelected() {
        return isAllSelected;
    }

    public void setAllSelected(boolean allSelected) {
        isAllSelected = allSelected;
    }
    public boolean isDeleteModeActive() {
        return isDeleteModeActive;
    }

    public void setDeleteModeActive(boolean deleteModeActive) {
        isDeleteModeActive = deleteModeActive;
    }
    public BusinessModel getBusinessesModel() {
        return businessesModel;
    }

    public void setBusinessesModel(BusinessModel businessesModel) {
        this.businessesModel = businessesModel;
    }
    public void setName(String name){
        businessesModel.setName(name);
    }

    public void setAddress(String address){
        businessesModel.setAddress(address);
    }

    public String getName(){
        return businessesModel.getName();
    }

    public String getAddress(){
        return businessesModel.getAddress();
    }


    public boolean verifyName(){
        return ValidateData.validateName(businessesModel.getName());
    }

    public boolean verifyAddres(){
        return ValidateData.validateAddress(businessesModel.getAddress());
    }
    public void hideAddBusinessBotton(){
        businessesView.setAddButtonVisibility(View.GONE);
    }

    public void showAddbusinessBotton(){
        businessesView.setAddButtonVisibility(View.VISIBLE);
    }

    public void hideCancelDeletModeButton(){
        businessesView.setCancelDeleteModeButtonVisibility(View.GONE);

    }
    public void showCancelDeletModeButton(){
        businessesView.setCancelDeleteModeButtonVisibility(View.VISIBLE);
    }
    public void hideSelectAllButton(){
        businessesView.setSelectAllButtonVisibility(View.GONE);
    }

    public void showSelectAllButton(){
        businessesView.setSelectAllButtonVisibility(View.VISIBLE);
    }


    public void hideDeleteButton(){
        businessesView.setDeletButtonVisibility(View.GONE);
    }

    public void showDeleteButton(){
        businessesView.setDeletButtonVisibility(View.VISIBLE);
    }
    public View getTitle() {
        return title;
    }

    public void setTitle(View title) {
        this.title = title;
    }
    public MutableLiveData<Integer> getUncheckedCircleVisibility() {
        return businessesView.getUncheckedCircleVisibility();
    }

    public void setUncheckedCircleVisibility(int uncheckedCircleVisibility) {
        businessesView.setUncheckedCircleVisibility(uncheckedCircleVisibility);
    }

    public MutableLiveData<Drawable> getCircleForAllCards() {
        return businessesView.getCircleForAllCards();
    }




    //Create a thread to deletes the business and show the progress in real time. This way the main
    // thread does not get locked
    Runnable deleteBusinesses = ()->{
        int progress = 0;
        businessesView.setProgress(progress);
        //businessesView.setDeletingCustomViewVisibility(View.VISIBLE);
        System.out.println("Deleted businesses: continuar?" +cancelDeletingProcess +" " + deletedBusinessList.size());
        for (Business business : businessListToDelete) {

            if(cancelDeletingProcess){
                try {
                    break;
                }catch (Exception e){
                    System.out.println("Error" + e.getMessage());
                }
            }
            //System.out.println("Deleted businesses: continuar?" +cancelDeletingProcess +" " + deletedBusinessList.size());

            try {

                if(businessesModel.deleteBusinessOffline(business).get()){
                    deletedBusinessList.add(business);
                    businessesView.setItemsToDelete(String.valueOf(deletedBusinessList.size()).concat("/").concat(String.valueOf(businessListToDelete.size())));
                    if(progress != (deletedBusinessList.size() * 100 / businessListToDelete.size())){
                        progress = (deletedBusinessList.size() * 100 / businessListToDelete.size());
                        businessesView.setProgress(progress);
                    }
                }
                Thread.sleep(Math.round(1000/businessListToDelete.size()));

            } catch (ExecutionException e) {
                System.out.println("Error" + e.getMessage());
                throw new RuntimeException(e);

            } catch (InterruptedException e) {
                System.out.println("Error" + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        //businessesView.setDeletingCustomViewVisibility(View.GONE);
        businessesView.getProgressBarDialog().dismiss();
        uncheckAllCards();
        unoDeletedBusinessesOption();

    };

}
