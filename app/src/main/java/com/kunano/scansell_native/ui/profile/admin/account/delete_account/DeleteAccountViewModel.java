package com.kunano.scansell_native.ui.profile.admin.account.delete_account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeleteAccountViewModel extends ViewModel {
    private MutableLiveData<String> emailWarnContentMutableLiveData;
    private MutableLiveData<Integer> emailWarnVisibilityMutableLiveData;


    public DeleteAccountViewModel() {
        emailWarnVisibilityMutableLiveData = new MutableLiveData<>();
        emailWarnContentMutableLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<Integer> getEmailWarnVisibilityMutableLiveData() {
        return emailWarnVisibilityMutableLiveData;
    }

    public void setEmailWarnVisibilityMutableLiveData(Integer emailWarnVisibilityMutableLiveData) {
        this.emailWarnVisibilityMutableLiveData.postValue(emailWarnVisibilityMutableLiveData);
    }

    public MutableLiveData<String> getEmailWarnContentMutableLiveData() {
        return emailWarnContentMutableLiveData;
    }

    public void setEmailWarnContentMutableLiveData(String emailWarnContentMutableLiveData) {
        this.emailWarnContentMutableLiveData.postValue(emailWarnContentMutableLiveData);
    }
}
