package com.kunano.scansell_native.ui.profile.admin.account.name;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.ValidateData;

public class NameViewModel extends AndroidViewModel {
    private MutableLiveData<String> newNameWarnMutableData;

    public NameViewModel(@NonNull Application application) {
        super(application);
        newNameWarnMutableData = new MutableLiveData<>();
    }


    public MutableLiveData<String> getNewNameWarnMutableData() {
        return newNameWarnMutableData;
    }

    public void setNewNameWarnMutableData(String newNameWarnMutableData) {
        this.newNameWarnMutableData.postValue(newNameWarnMutableData);
    }


    public boolean validateName(String  newName){

        if (newName.isEmpty()){
            setNewNameWarnMutableData(getApplication().getString(R.string.name));
            return false;
        }else if(!ValidateData.validateName(newName)) {
            setNewNameWarnMutableData(getApplication().getString(R.string.advert_invalid_name));
            return false;
        }else {
            setNewNameWarnMutableData(null);
            return true;
        }
    }

}
