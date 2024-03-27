package com.kunano.scansell_native.ui.profile.admin.account.delete_account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeleteAccountViewModel extends ViewModel {
    private MutableLiveData<Integer> emailWarnVisibilityMutableLiveData;


    public DeleteAccountViewModel() {
        emailWarnVisibilityMutableLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<Integer> getEmailWarnVisibilityMutableLiveData() {
        return emailWarnVisibilityMutableLiveData;
    }

    public void setEmailWarnVisibilityMutableLiveData(Integer emailWarnVisibilityMutableLiveData) {
        this.emailWarnVisibilityMutableLiveData.postValue(emailWarnVisibilityMutableLiveData);
    }
}
