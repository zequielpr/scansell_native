package com.kunano.scansell_native.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kunano.scansell_native.controllers.ValidateData;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.repository.Repository;

import java.util.List;


/***This view model is scooped in the host activity. Home fragment and BottomSheetFragment share it ***/

public class HomeViewModel extends AndroidViewModel {
    private ListenHomeViewModel listenHomeViewModel;
    private Repository repository;


    private LiveData<List<Business>> businessListLiveData;


    public HomeViewModel(@NonNull Application application){
        super(application);
        repository = new Repository(application);
        businessListLiveData = repository.getAllBusinesses();

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








    //Getter an setter
    public LiveData<List<Business>> getAllBusinesses(){
        return businessListLiveData;
    }

    public ListenHomeViewModel getListenHomeViewModel() {
        return listenHomeViewModel;
    }

    public void setListenHomeViewModel(ListenHomeViewModel listenHomeViewModel) {
        this.listenHomeViewModel = listenHomeViewModel;
    }


    //BusinessCard
    public void shortTap(Business business){
        listenHomeViewModel.navigateToProducts(String.valueOf(business.getBusinessId()));
    }

}