package com.kunano.scansell_native.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.controllers.ValidateData;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.repository.Repository;

import java.util.HashSet;
import java.util.List;


/***This view model is scooped in the host activity. Home fragment and BottomSheetFragment share it ***/

public class HomeViewModel extends AndroidViewModel {
    private ListenHomeViewModel listenHomeViewModel;
    private Repository repository;
    private boolean isDeleteModeActive;

    private HashSet<Business> businessesToDelete;
    private HashSet<Business> deletedBusinesses;


    private LiveData<List<Business>> businessListLiveData;
    private MutableLiveData<Integer> deleteProgress;
    private MutableLiveData<Integer> selectedBusinesses;



    public HomeViewModel(@NonNull Application application){
        super(application);
        this.repository = new Repository(application);
        this.businessListLiveData = repository.getAllBusinesses();
        this.deleteProgress = new MutableLiveData<>();
        this.selectedBusinesses = new MutableLiveData<>();
        this.isDeleteModeActive = false;

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


    //BusinessCard------------------------------------------------------------------------
    public void shortTap(Business business){
        if (isDeleteModeActive){
            if(businessesToDelete.contains(business)){
                //Unselete to delete

            }else{
                //Select to delete
            }


            return;
        }

        listenHomeViewModel.navigateToProducts(String.valueOf(business.getBusinessId()));
    }

    public void longTap(Business business){
        listenHomeViewModel.navigateToProducts(String.valueOf(business.getBusinessId()));
    }





}