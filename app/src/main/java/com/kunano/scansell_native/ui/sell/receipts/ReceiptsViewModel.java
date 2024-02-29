package com.kunano.scansell_native.ui.sell.receipts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReceiptsViewModel extends ViewModel {
    private MutableLiveData<Boolean> isSearchModeActive;
    public ReceiptsViewModel(){
        isSearchModeActive = new MutableLiveData<>(false);
    }

    public MutableLiveData<Boolean> getIsSearchModeActive() {
        return isSearchModeActive;
    }

    public void setIsSearchModeActive(boolean isSearchModeActive) {
        this.isSearchModeActive.postValue(isSearchModeActive);
    }
}