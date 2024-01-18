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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/***This view model is scooped in the host activity. Home fragment and BottomSheetFragment share it ***/

public class HomeViewModel extends AndroidViewModel {
    private ListenHomeViewModel listenHomeViewModel;
    private Repository repository;
    protected boolean isDeleteModeActive;
    private boolean isAllSelected;
    private boolean continueDeleting;
    private Business currentBusiness;

    private LinkedHashSet<Business> businessesToDelete;
    private HashSet<Business> deletedBusinesses;
    protected String deletedBusinessN;
    private int percentageDeleted;
    private LiveData<List<Business>> businessListLiveData;
    private MutableLiveData<Integer> deleteProgressLiveData;
    private MutableLiveData<String> selectedBusinessesNumbLiveData;

    private MutableLiveData<Drawable> checkedOrUncheckedCirclLivedata;
    private MutableLiveData<String> deletedBusnLiveData;



    public HomeViewModel(@NonNull Application application){
        super(application);
        this.repository = new Repository(application);
        this.businessListLiveData = repository.getAllBusinesses();

        this.deleteProgressLiveData = new MutableLiveData<>();
        this.selectedBusinessesNumbLiveData = new MutableLiveData<>();
        this.checkedOrUncheckedCirclLivedata = new MutableLiveData<>();
        checkedOrUncheckedCirclLivedata = new MutableLiveData<>();
        this.deletedBusnLiveData = new MutableLiveData<>();

        this.businessesToDelete = new LinkedHashSet<>();
        this.deletedBusinesses = new HashSet<>();
        this.isDeleteModeActive = false;
        this.isAllSelected = false;
    }


    public void insertNewBusiness(String name, String address){

        Business newBusiness = new Business(name, address,"");

        repository.insertBusiness(newBusiness, this::notifyInsertNewBusinessResult);
        listenHomeViewModel.activateWaitingMode();
    }
    public LiveData<List<Business>> getAllBusinesses(){
        return businessListLiveData;
    }

    public void deletetBusiness(){
        selectedBusinessesNumbLiveData.postValue(getApplication().getString(R.string.businesses_title));
        checkedOrUncheckedCirclLivedata.postValue(null);
        isAllSelected = false;
        isDeleteModeActive = false;
        deletedBusinesses.clear();
        listenHomeViewModel.showProgressBar();
        continueDeleting = true;
        percentageDeleted = 0;



        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            for (Business business : businessesToDelete) {
                try {
                    if (!continueDeleting) {
                        break;
                    }
                    updateProgressBar(business);
                    Thread.sleep(Math.round(1000 / businessesToDelete.size()));
                    deletedBusinesses.add(business);
                    repository.deleteBusiness(business);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            listenHomeViewModel.hideProgressBar();
            desactivateDeleteMod();

            // Update the LiveData with the result

        });
    }



    private void updateProgressBar(Business business){
        deletedBusinessN = deletedBusinesses.size() + "/" + businessesToDelete.size();
        if(percentageDeleted != (deletedBusinesses.size() * 100 /businessesToDelete.size())){
            percentageDeleted = (deletedBusinesses.size() * 100 /businessesToDelete.size());
        }

        deleteProgressLiveData.postValue(percentageDeleted);
        deletedBusnLiveData.postValue(deletedBusinessN);

    }


    public void cancelDeleteProcess(){
        continueDeleting = false;
    }




    private void notifyInsertNewBusinessResult(boolean result){
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


           selectedBusinessesNumbLiveData.postValue(Integer.toString(businessesToDelete.size()));
           isAllSelected = businessListLiveData.getValue().size() == businessesToDelete.size();
            return;
        }
        currentBusiness = business;

        listenHomeViewModel.navigateToProducts(String.valueOf(business.getBusinessId()));
    }

    public void longTap(Business business){
        if(businessesToDelete.contains(business)){
            businessesToDelete.remove(business);
        }else {
            businessesToDelete.add(business);
        }
        selectedBusinessesNumbLiveData.postValue(Integer.toString(businessesToDelete.size()));
        isAllSelected = businessListLiveData.getValue().size() == businessesToDelete.size();
    }

    public void selectAll(){
        isAllSelected = true;
        businessesToDelete.addAll(businessListLiveData.getValue());
        selectedBusinessesNumbLiveData.postValue(Integer.toString(businessesToDelete.size()));

    }

    public void unSelectAll(){
        isAllSelected = false;
        businessesToDelete.clear();
        selectedBusinessesNumbLiveData.postValue(Integer.toString(businessesToDelete.size()));
    }

    public void  desactivateDeleteMod(){
        selectedBusinessesNumbLiveData.postValue(getApplication().getString(R.string.businesses_title));
        businessesToDelete.clear();
        isAllSelected = false;
        isDeleteModeActive = false;
    }






    //Getter an setter----------------------------------------------------------------------------

    public ListenHomeViewModel getListenHomeViewModel() {
        return listenHomeViewModel;
    }

    public void setListenHomeViewModel(ListenHomeViewModel listenHomeViewModel) {
        this.listenHomeViewModel = listenHomeViewModel;
    }

    public MutableLiveData<Integer> getDeleteProgressLiveData() {
        return deleteProgressLiveData;
    }

    public void setDeleteProgressLiveData(MutableLiveData<Integer> deleteProgressLiveData) {
        this.deleteProgressLiveData = deleteProgressLiveData;
    }

    public MutableLiveData<String> getSelectedBusinessesNumbLiveData() {
        return selectedBusinessesNumbLiveData;
    }

    public void setSelectedBusinessesNumbLiveData(String titleAppbar) {
        this.selectedBusinessesNumbLiveData.postValue(titleAppbar);
    }

    public HashSet<Business> getBusinessesToDelete() {
        return businessesToDelete;
    }

    public void setBusinessesToDelete(LinkedHashSet<Business> businessesToDelete) {
        this.businessesToDelete = businessesToDelete;
    }

    public HashSet<Business> getDeletedBusinesses() {
        return deletedBusinesses;
    }

    public void setDeletedBusinesses(HashSet<Business> deletedBusinesses) {
        this.deletedBusinesses = deletedBusinesses;
    }

    public MutableLiveData<Drawable> getCheckedOrUncheckedCirclLivedata() {
        return checkedOrUncheckedCirclLivedata;
    }

    public void setCheckedOrUncheckedCirclLivedata(Drawable checkedOrUncheckedCirclLivedata) {
        this.checkedOrUncheckedCirclLivedata.postValue(checkedOrUncheckedCirclLivedata);
    }


    public void setIsAllSelected(boolean isAllSelected){
        this.isAllSelected = isAllSelected;
    }

    public boolean  getIsAllSelected(){
        return this.isAllSelected;
    }

    public MutableLiveData<String> getDeletedBusnLiveData() {
        return deletedBusnLiveData;
    }

    public void setDeletedBusnLiveData(MutableLiveData<String> deletedBusnLiveData) {
        this.deletedBusnLiveData = deletedBusnLiveData;
    }

    public Business getCurrentBusiness() {
        return currentBusiness;
    }

    public void setCurrentBusiness(Business currentBusiness) {
        this.currentBusiness = currentBusiness;
    }

    public boolean isDeleteModeActive() {
        return isDeleteModeActive;
    }

    public void setDeleteModeActive(boolean deleteModeActive) {
        isDeleteModeActive = deleteModeActive;
    }

    //This method will be called from producHomeViewodel

}