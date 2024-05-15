package com.kunano.scansell_native.ui.home;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.repository.home.BusinessRepository;
import com.kunano.scansell_native.components.ProcessItemsComponent;

import java.util.List;


/***This view model is scooped in the host activity. Home fragment and BottomSheetFragment share it ***/

public class HomeViewModel extends AndroidViewModel {
    private ListenHomeViewModel listenHomeViewModel;
    private LiveData<List<Business>> businessListLiveData;
    private Long currentBusinessId;
    private MutableLiveData<String> selectedItems;

    private MutableLiveData<Integer> createNewBusinessVisibilityMD;
    private MutableLiveData<Integer> cardBackgroundColor;
    private BusinessRepository businessRepository;
    private MutableLiveData<Drawable> checkedOrUncheckedCircleLivedata;




    public HomeViewModel(@NonNull Application application) {
        super(application);
        businessRepository = new BusinessRepository(application);
        this.businessListLiveData = businessRepository.getAllBusinesses();
        currentBusinessId = null;

        createNewBusinessVisibilityMD = new MutableLiveData<>();
        selectedItems = new MutableLiveData<>();

        cardBackgroundColor = new MutableLiveData<>();
        checkedOrUncheckedCircleLivedata = new MutableLiveData<>();
    }



    public LiveData<List<Business>> getAllBusinesses() {
        return businessListLiveData;
    }

    public void notifyInsertNewBusinessResult(boolean result) {
        listenHomeViewModel.desactivateWaitingMode();
    }
    private void notifyUpdateBusinessResult(boolean result) {
        //listenHomeViewModel.desactivateWaitingMode();
    }



    //BusinessCard------------------------------------------------------------------------




    public void  addOrRemoveItemToProcess(ProcessItemsComponent<Business> businessProcessor, Business business) {
        boolean isAdded = businessProcessor.isItemToBeProcessed(business);
        System.out.println("Is added: " + isAdded);
        if(isAdded){
            businessProcessor.removeItemToProcess(business);
        }else {
            businessProcessor.addItemToProcess(business);
        }

        selectedItems.postValue(String.valueOf(businessProcessor.getItemsToProcess().size()));
    }




    public LiveData<Integer> getQuantityOfProductsInBusiness(Long businessId){
        return businessRepository.getQuantityOfProductsInBusiness(businessId);
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

    public MutableLiveData<Integer> getCreateNewBusinessVisibilityMD() {
        return createNewBusinessVisibilityMD;
    }

    public void setCreateNewBusinessVisibilityMD(Integer createNewBusinessVisibilityMD) {
        this.createNewBusinessVisibilityMD.postValue(createNewBusinessVisibilityMD);
    }

    public MutableLiveData<String> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(String selectedItems) {
        this.selectedItems.postValue(selectedItems);
    }

    public MutableLiveData<Integer> getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    public void setCardBackgroundColor(Integer cardBackgroundColor) {
        this.cardBackgroundColor.postValue(cardBackgroundColor);
    }

    public MutableLiveData<Drawable> getCheckedOrUncheckedCircleLivedata() {
        return checkedOrUncheckedCircleLivedata;
    }

    public void setCheckedOrUncheckedCircleLivedata(Drawable checkedOrUncheckedCircleLivedata) {
        this.checkedOrUncheckedCircleLivedata.postValue(checkedOrUncheckedCircleLivedata);
    }
}