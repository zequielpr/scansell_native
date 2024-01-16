package com.kunano.scansell_native.ui.home;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.controllers.ValidateData;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.repository.Repository;

import java.util.HashSet;
import java.util.List;


/***This view model is scooped in the host activity. Home fragment and BottomSheetFragment share it ***/

public class HomeViewModel extends AndroidViewModel {
    private ListenHomeViewModel listenHomeViewModel;
    private Repository repository;

    public boolean isDeleteModeActive() {
        return isDeleteModeActive;
    }

    public void setDeleteModeActive(boolean deleteModeActive) {
        isDeleteModeActive = deleteModeActive;
    }

    private boolean isDeleteModeActive;
    private boolean isAllSelected;

    private HashSet<Business> businessesToDelete;
    private HashSet<Business> deletedBusinesses;




    private LiveData<List<Business>> businessListLiveData;
    private MutableLiveData<Integer> deleteProgress;
    private MutableLiveData<String> selectedBusinessesNumb;

    private MutableLiveData<Drawable> checkedOrUncheckedCircle;



    public HomeViewModel(@NonNull Application application){
        super(application);
        this.repository = new Repository(application);
        this.businessListLiveData = repository.getAllBusinesses();

        this.deleteProgress = new MutableLiveData<>();
        this.selectedBusinessesNumb = new MutableLiveData<>();
        this.checkedOrUncheckedCircle = new MutableLiveData<>();
        checkedOrUncheckedCircle = new MutableLiveData<>();

        this.businessesToDelete = new HashSet<>();
        this.deletedBusinesses = new HashSet<>();
        this.isDeleteModeActive = false;
        this.isAllSelected = false;

    }
    public void reciveDataBusiness(String name, String address){
        insertNewBusiness(new Business(name, address,""));
    }


    public void insertNewBusiness(Business business){

        repository.insertBusiness(business, this::notifyResult);
        listenHomeViewModel.activateWaitingMode();
    }


    private void notifyResult(boolean result){
        listenHomeViewModel.desactivateWaitingMode();
    }

    //Validate data
    public boolean validateName(String name){
        return ValidateData.validateName(name);
    }
    public boolean validateAddress(String address){
        return ValidateData.validateAddress(address);
    }



    //BusinessCard------------------------------------------------------------------------
    public void shortTap(Business business){
        if (isDeleteModeActive){

           if(businessesToDelete.contains(business)){
                businessesToDelete.remove(business);

            }else{
               businessesToDelete.add(business);
                //Select to delete
            }


           selectedBusinessesNumb.postValue(Integer.toString(businessesToDelete.size()));
           isAllSelected = businessListLiveData.getValue().size() == businessesToDelete.size();
            return;
        }

        listenHomeViewModel.navigateToProducts(String.valueOf(business.getBusinessId()));
    }

    public void longTap(Business business){
        if(businessesToDelete.contains(business)){
            businessesToDelete.remove(business);
        }else {
            businessesToDelete.add(business);
        }
        selectedBusinessesNumb.postValue(Integer.toString(businessesToDelete.size()));
        isAllSelected = businessListLiveData.getValue().size() == businessesToDelete.size();
    }

    public void selectAll(){
        isAllSelected = true;
        businessesToDelete.addAll(businessListLiveData.getValue());
        selectedBusinessesNumb.postValue(Integer.toString(businessesToDelete.size()));

    }

    public void unSelectAll(){
        isAllSelected = false;
        businessesToDelete.clear();
        selectedBusinessesNumb.postValue(Integer.toString(businessesToDelete.size()));
    }

    public void  desactivateDeleteMod(){
        selectedBusinessesNumb.postValue(getApplication().getString(R.string.businesses_title));
        businessesToDelete.clear();
        isAllSelected = false;
        isDeleteModeActive = false;
    }





    //Getter an setter----------------------------------------------------------------------------
    public LiveData<List<Business>> getAllBusinesses(){
        return businessListLiveData;
    }

    public ListenHomeViewModel getListenHomeViewModel() {
        return listenHomeViewModel;
    }

    public void setListenHomeViewModel(ListenHomeViewModel listenHomeViewModel) {
        this.listenHomeViewModel = listenHomeViewModel;
    }

    public MutableLiveData<Integer> getDeleteProgress() {
        return deleteProgress;
    }

    public void setDeleteProgress(MutableLiveData<Integer> deleteProgress) {
        this.deleteProgress = deleteProgress;
    }

    public MutableLiveData<String> getSelectedBusinessesNumb() {
        return selectedBusinessesNumb;
    }

    public void setSelectedBusinessesNumb(String titleAppbar) {
        this.selectedBusinessesNumb.postValue(titleAppbar);
    }

    public HashSet<Business> getBusinessesToDelete() {
        return businessesToDelete;
    }

    public void setBusinessesToDelete(HashSet<Business> businessesToDelete) {
        this.businessesToDelete = businessesToDelete;
    }

    public HashSet<Business> getDeletedBusinesses() {
        return deletedBusinesses;
    }

    public void setDeletedBusinesses(HashSet<Business> deletedBusinesses) {
        this.deletedBusinesses = deletedBusinesses;
    }

    public MutableLiveData<Drawable> getCheckedOrUncheckedCircle() {
        return checkedOrUncheckedCircle;
    }

    public void setCheckedOrUncheckedCircle(Drawable checkedOrUncheckedCircle) {
        this.checkedOrUncheckedCircle.postValue(checkedOrUncheckedCircle);
    }


    public void setIsAllSelected(boolean isAllSelected){
        this.isAllSelected = isAllSelected;
    }

    public boolean  getIsAllSelected(){
        return this.isAllSelected;
    }


}