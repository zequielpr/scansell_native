package com.kunano.scansell_native.ui.profile.admin.account.name;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangeNameViewModel extends ViewModel {
    private MutableLiveData<String> newNameWarnMutableData;

    private ChangeNameViewModel(){
        newNameWarnMutableData = new MutableLiveData<>();
    }

    public MutableLiveData<String> getNewNameWarnMutableData() {
        return newNameWarnMutableData;
    }

    public void setNewNameWarnMutableData(String newNameWarnMutableData) {
        this.newNameWarnMutableData.postValue(newNameWarnMutableData);
    }
}
