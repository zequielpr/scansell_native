package com.kunano.scansell_native.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.ValidateData;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;
import com.kunano.scansell_native.ui.components.ListenResponse;

import java.util.List;
import java.util.stream.Collectors;


/***This view model is scooped in the host activity. Home fragment and BottomSheetFragment share it ***/

public class HomeViewModel extends DeleteItemsViewModel {
    private ListenHomeViewModel listenHomeViewModel;
    private LiveData<List<Business>> businessListLiveData;
    private Long currentBusinessId;




    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.businessListLiveData = businessRepository.getAllBusinesses();
        currentBusinessId = null;
    }




    public void insertNewBusiness(String name, String address) {

        Business newBusiness = new Business(name, address, "");

        businessRepository.insertBusiness(newBusiness, this::notifyInsertNewBusinessResult);
        listenHomeViewModel.activateWaitingMode();
    }


    public LiveData<List<Business>> getAllBusinesses() {
        return businessListLiveData;
    }

    private void notifyInsertNewBusinessResult(boolean result) {
        listenHomeViewModel.desactivateWaitingMode();
    }
    private void notifyUpdateBusinessResult(boolean result) {
        //listenHomeViewModel.desactivateWaitingMode();
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
                itemsToDelete.add(business);
                //Select to delete
            }


            selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
            isAllSelected = businessListLiveData.getValue().size() == itemsToDelete.size();
            return;
        }

        //currentBusiness = repository.getBusinesById(business.getBusinessId());

        listenHomeViewModel.navigateToProducts(business.getBusinessId());
        currentBusinessId = business.getBusinessId();
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





    public void passBusinessToBin() {
        this.itemTypeToDelete = ItemTypeToDelete.BUSINESS;

        listenHomeViewModel.showProgressBar();

        super.passItemsToBin(new ListenResponse() {
            @Override
            public void isSuccessfull(boolean resultado) {
                if(resultado){
                    listenHomeViewModel.hideProgressBar();
                }
            }
        },getApplication().getString(R.string.businesses_title));


    }








    //Getter an setter----------------------------------------------------------------------------

    public ListenHomeViewModel getListenHomeViewModel() {
        return listenHomeViewModel;
    }

    public void setListenHomeViewModel(ListenHomeViewModel listenHomeViewModel) {
        this.listenHomeViewModel = listenHomeViewModel;
    }

    public Long getCurrentBusinessId() {
        System.out.println("Current business: " +currentBusinessId);
        return currentBusinessId;
    }

    public void setCurrentBusinessId(Long currentBusinessId) {
        this.currentBusinessId = currentBusinessId;
    }
}