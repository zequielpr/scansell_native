package com.kunano.scansell_native.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.controllers.ValidateData;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


/***This view model is scooped in the host activity. Home fragment and BottomSheetFragment share it ***/

public class HomeViewModel extends DeleteItemsViewModel {
    private ListenHomeViewModel listenHomeViewModel;
    private LiveData<List<Business>> businessListLiveData;

    private LiveData<List<Business>> currentBusiness;


    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.businessListLiveData = repository.getAllBusinesses();
        this.deleteProgressLiveData = new MutableLiveData<>();
        this.selectedItemsNumbLiveData = new MutableLiveData<>();
        checkedOrUncheckedCirclLivedata = new MutableLiveData<>();
        this.deletedItemsLiveData = new MutableLiveData<>();
        this.itemsToDelete = new LinkedHashSet<>();
        this.deletedItems = new HashSet<>();
        this.isDeleteModeActive = false;
        this.isAllSelected = false;
    }




    public void insertNewBusiness(String name, String address) {

        Business newBusiness = new Business(name, address, "");

        repository.insertBusiness(newBusiness, this::notifyInsertNewBusinessResult);
        listenHomeViewModel.activateWaitingMode();
    }

    public LiveData<List<Business>> getAllBusinesses() {
        return businessListLiveData;
    }

    private void notifyInsertNewBusinessResult(boolean result) {
        listenHomeViewModel.desactivateWaitingMode();
    }

    //Validate data
    public boolean validateName(String name) {
        return ValidateData.validateName(name);
    }

    public boolean validateAddress(String address) {
        return ValidateData.validateAddress(address);
    }


    //BusinessCard------------------------------------------------------------------------
    public void shortTap(Business business) {
        if (isDeleteModeActive) {

            if (itemsToDelete.contains(business)) {
                itemsToDelete.remove(business);

            } else {
                itemsToDelete.add((Object) business);
                //Select to delete
            }


            selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
            isAllSelected = businessListLiveData.getValue().size() == itemsToDelete.size();
            return;
        }

        currentBusiness = repository.getBusinesById(business.getBusinessId());

        listenHomeViewModel.navigateToProducts(String.valueOf(business.getBusinessId()));
    }


    public void longTap(Business business) {
        if (itemsToDelete.contains(business)) {
            itemsToDelete.remove(business);
        } else {
            itemsToDelete.add(business);
        }
        selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
        isAllSelected = businessListLiveData.getValue().size() == itemsToDelete.size();
    }



    public List<Object> parseBusinessListToGeneric() {
        return businessListLiveData.getValue().stream()
                .map(business -> (Object) business)
                .collect(Collectors.toList());
    }





    public void deletetBusiness() {

        listenHomeViewModel.showProgressBar();

        super.deletetItems(new ListenResponse() {
            @Override
            public void isSuccessfull(boolean resultado) {
                if(resultado){
                    listenHomeViewModel.hideProgressBar();
                }
            }
        });


    }








    //Getter an setter----------------------------------------------------------------------------

    public ListenHomeViewModel getListenHomeViewModel() {
        return listenHomeViewModel;
    }

    public void setListenHomeViewModel(ListenHomeViewModel listenHomeViewModel) {
        this.listenHomeViewModel = listenHomeViewModel;
    }


    public LiveData<List<Business>> getCurrentBusiness() {
        return currentBusiness;
    }

    public void setCurrentBusiness(LiveData<List<Business>> currentBusiness) {
        this.currentBusiness = currentBusiness;
    }

}