package com.kunano.scansell_native.controllers.home;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.controllers.ValidateData;
import com.kunano.scansell_native.model.Home.Business;
import com.kunano.scansell_native.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BusinessController {
    private View title;
    private Business businessesModel;
    private HomeViewModel businessesView;
    private boolean isDeleteModeActive;
    private Drawable checkedCircle;
    private Drawable uncheckedCircle;
    private boolean isAllSelected;
    private HashSet<String> businessListToDelete;
    private HashSet<String> deletedBusinessList;
    private List<Map<String, Object>> businessesListData;

    int deletedBusinesses;


    public BusinessController(Business businessModel, HomeViewModel businessesView ){
        this.businessesModel = businessModel;
        this.businessesView = businessesView;
        checkedCircle = businessesView.getLayoutInflater().getContext().getResources().getDrawable(R.drawable.checked_circle);
        uncheckedCircle = businessesView.getLayoutInflater().getContext().getResources().getDrawable(R.drawable.unchked_circle);
    }

    //Add business
    public CompletableFuture<Boolean> addBusiness(){
        //Verify data
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", businessesModel.getName());
        data.put("address", businessesModel.getAddress());



        //Option te create businesses while the divice is offline
        boolean isSuccessfully = businessesModel.addBusinessOffline(data);
        future.complete(isSuccessfully);



        return future;
    }


    public void deleteBusinesses(){

        deletedBusinesses = 0;
        boolean isSuccessful;
        deletedBusinessList = new HashSet<>();
        //Delete businesses while the device is offline
        for (String businessId : businessListToDelete) {
            //Delete business offline
            isSuccessful = deleteBusinessOffline(businessId);
            if (!isSuccessful)break;
            deletedBusinessList.add(businessId);
            businessesListData.removeIf((businessData) -> businessData.get("name").equals(businessId));
            deletedBusinesses++;
            businessesView.setItemsToDelete(String.valueOf(deletedBusinesses).concat("/").concat(String.valueOf(businessListToDelete.size())));
            businessesView.setProgress((deletedBusinesses * 100 / businessListToDelete.size()));
            System.out.println("list business" + businessesListData.size());
            businessesView.setBusinessList(businessesListData, this);
        }

        businessListToDelete.removeAll(deletedBusinessList);


    }

    //Controll view
    public void showData(){
        CompletableFuture<Void> future = new CompletableFuture<>();
        businessesModel.getBusinessListDataAsync().thenAccept(data ->{
            if(!data.isEmpty()){
                businessesView.setBusinessList(data, this);
                businessesListData = new ArrayList<>(data);
            }else{

                System.out.println("No data");
                //Show button to create a new business.
            }

        });
    }


    public HashSet<String> getBusinessListToDelete() {
        return businessListToDelete;
    }

    public void setBusinessListToDelete(HashSet<String> businessListToDelete) {
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
    public Business getBusinessesModel() {
        return businessesModel;
    }

    public void setBusinessesModel(Business businessesModel) {
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


    public boolean deleteBusinessOffline(String businessId){
       return businessesModel.deleteBusinessOffline(businessId);
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

    public void longPressBusinessCard(View businessCard){
        String businessId = businessCard.getTag().toString();
        activateDeletMode();
        checkTouchedCard(businessId);
        verifyIfAllCardsAreSelected();
    }

    public void shortPressBusinessCard(View businessCard){
        String businessId = businessCard.getTag().toString();
        if(isDeleteModeActive){
            if(businessListToDelete.contains(businessId)){
                uncheckTouchedCard(businessId);
                verifyIfAllCardsAreSelected();
                if(businessListToDelete.isEmpty())hideDeleteButton();
                return;
            }
            showDeleteButton();
            checkTouchedCard(businessId);
        }

        verifyIfAllCardsAreSelected();


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
        List<String> businessesId = businessesListData.stream()
                .map(map -> map.get("business_id"))  // Assuming the key is "businesId"
                .map(value -> (String) value)
                .collect(Collectors.toList());

        businessListToDelete.addAll(businessesId);

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
        businessListToDelete.add(businessCardId);

        businessesView.setCircleForTouchedCard(imageAndBusinessId);


    }

    public void uncheckTouchedCard(String businessCardId){
        Map<String, Object> imageAndBusinessId = new HashMap<>();
        imageAndBusinessId.put("businessId", businessCardId);
        imageAndBusinessId.put("checkedCircle", uncheckedCircle );
        businessListToDelete.remove(businessCardId);

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
