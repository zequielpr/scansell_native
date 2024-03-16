package com.kunano.scansell_native.ui.profile.admin.account.email;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangeEmailviewModel extends ViewModel {

    private MutableLiveData<String> emailWarnMessage;
    private MutableLiveData<String> emailToConfirmWarnMessage;

    public ChangeEmailviewModel() {
        emailWarnMessage = new MutableLiveData<>();
        emailToConfirmWarnMessage = new MutableLiveData<>();
    }


    public MutableLiveData<String> getEmailWarnMessage() {
        return emailWarnMessage;
    }

    public void setEmailWarnMessage(String emailWarnMessage) {
        this.emailWarnMessage.postValue(emailWarnMessage);
    }

    public MutableLiveData<String> getEmailToConfirmWarnMessage() {
        return emailToConfirmWarnMessage;
    }

    public void setEmailToConfirmWarnMessage(String emailToConfirmWarnMessage) {
        this.emailToConfirmWarnMessage.postValue(emailToConfirmWarnMessage);
    }
}
