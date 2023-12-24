package com.kunano.scansell_native.ui.home.bottom_sheet;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BottomSheetViewModel extends ViewModel {
    MutableLiveData<String> advertIncorrectName;
    MutableLiveData<String> advertIncorrectAddress;

    public BottomSheetViewModel(){
        advertIncorrectName = new MutableLiveData<>();
        advertIncorrectAddress = new MutableLiveData<>();
    }


    public MutableLiveData<String> getAdvertIncorrectName() {
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
    }



}
