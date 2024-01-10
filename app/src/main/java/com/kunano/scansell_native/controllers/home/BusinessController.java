package com.kunano.scansell_native.controllers.home;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.controllers.ValidateData;
import com.kunano.scansell_native.db.Business;
import com.kunano.scansell_native.model.Home.BusinessModel;
import com.kunano.scansell_native.ui.home.HomeViewModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

    int deletedBusinesses;


    public BusinessController(BusinessModel businessModel, HomeViewModel businessesView ){
        this.businessesModel = businessModel;
        this.businessesView = businessesView;
        checkedCircle = businessesView.getLayoutInflater().getContext().getResources().getDrawable(R.drawable.checked_circle);
        uncheckedCircle = businessesView.getLayoutInflater().getContext().getResources().getDrawable(R.drawable.unchked_circle);
    }

    //Add business
    public CompletableFuture<Boolean> addBusiness(){

        //Verify data
        return businessesModel.addBusinessOffline();
    }


    public void deleteBusinesses(){
        int bumBusinessToDeleted = businessListToDelete.size();
        deletedBusinessList = new HashSet<>();
        for (Business business : businessListToDelete) {
            //Delete business offline
            businessesModel.deleteBusinessOffline(business).thenAccept(result -> {
                deletedBusinessList.add(business);
                businessesView.setItemsToDelete(String.valueOf(deletedBusinessList.size()).concat("/").concat(String.valueOf(bumBusinessToDeleted)));
                businessesView.setProgress((deletedBusinessList.size() * 100 / bumBusinessToDeleted));

            });
        }
        businessListToDelete.removeAll(deletedBusinessList);

        desactivateDeletMode();



    }

    //Controll view
    public void showData(){
        businessesModel.getBusinessListDataAsync().observe(businessesView.getHomeLifecycleOwner(), (businesses) -> setBusinessesList(businesses));


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


    private void setBusinessesList(List<Business> businesses){
        businessesListData = businesses;
        businessesView.setBusinessList(businesses, this);
    }


    public HashSet<Business> getBusinessListToDelete() {
        return businessListToDelete;
    }

    public void setBusinessListToDelete(HashSet<Business> businessListToDelete) {
        this.businessListToDelete = businessListToDelete;
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


    //Delete business




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


    //title

    public View getTitle() {
        return title;
    }

    public void setTitle(View title) {
        this.title = title;
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
        }

        //verifyIfAllCardsAreSelected();


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

    public void checkAllCards(){
        businessListToDelete.addAll(businessesListData);

        isAllSelected = true;
        businessesView.setImageForSeletAllButton(checkedCircle);
        businessesView.setCircleForAllCards(checkedCircle);
        showDeleteButton();
    }
    public void uncheckAllCards(){
        isAllSelected = false;
        businessesView.setImageForSeletAllButton(uncheckedCircle);
        businessesView.setCircleForAllCards(uncheckedCircle);
        businessListToDelete.clear();
        hideDeleteButton();
    }



    public void checkTouchedCard(String businessCardId){
        Map<String, Object> imageAndBusinessId = new HashMap<>();
        imageAndBusinessId.put("businessId", businessCardId);
        imageAndBusinessId.put("checkedCircle", checkedCircle );

        businessListToDelete.addAll(businessesListData.stream().filter(business ->
                String.valueOf(business.businessId).equals(businessCardId)).collect(Collectors.toList()));

        businessesView.setCircleForTouchedCard(imageAndBusinessId);

    }

    public void uncheckTouchedCard(String businessCardId){
        Map<String, Object> imageAndBusinessId = new HashMap<>();
        imageAndBusinessId.put("businessId", businessCardId);
        imageAndBusinessId.put("checkedCircle", uncheckedCircle );


        businessListToDelete.removeAll(businessesListData.stream().filter(business ->
                String.valueOf(business.businessId).equals(businessCardId)).collect(Collectors.toList()));

        businessesView.setCircleForTouchedCard(imageAndBusinessId);


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
        uncheckAllCards();
        businessListToDelete.clear();
        isDeleteModeActive = false;
        setUncheckedCircleVisibility(View.GONE);
        showAddbusinessBotton();
        hideDeleteButton();
        hideCancelDeletModeButton();
        hideSelectAllButton();
        wrapTitleContent();

    }




}
