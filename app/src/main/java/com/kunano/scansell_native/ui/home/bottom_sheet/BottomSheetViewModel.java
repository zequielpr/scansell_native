package com.kunano.scansell_native.ui.home.bottom_sheet;

import androidx.lifecycle.ViewModel;

import com.kunano.scansell_native.controllers.ValidateData;

public class BottomSheetViewModel extends ViewModel {

    public boolean validateName(String name){
        return ValidateData.validateName(name);
    }
    public boolean validateAddress(String address){
        return ValidateData.validateAddress(address);
    }




/*    public MutableLiveData<String> getAdvertIncorrectName() {
        return advertIncorrectName;
    }

    public void setAdvertIncorrectName(String advertIncorrectName) {
        this.advertIncorrectName.postValue(advertIncorrectName);
    }

    public MutableLiveData<String> getAdvertIncorrectAddress() {
        return advertIncorrectAddress;
    }

    public void setAdvertIncorrectAddress(String advertIncorrectAddress) {
        this.advertIncorrectAddress.postValue(advertIncorrectAddress);
    }*/



}
