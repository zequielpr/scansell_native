package com.kunano.scansell_native.ui.profile.admin.account.email;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.ui.profile.auth.UserData;

public class EmailViewModel extends AndroidViewModel {

    private MutableLiveData<String> emailWarnMessage;
    private MutableLiveData<String> emailToConfirmWarnMessage;

    public EmailViewModel(@NonNull Application application) {
        super(application);
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


    public boolean isEmailValid(UserData.EmailWarns emailWarns){

        setEmailWarnMessage(null);
        setEmailToConfirmWarnMessage(null);

        String message;
        switch (emailWarns){
            case EMAIL_EMPTY:
                message = getApplication().getString(R.string.introduce_an_email);
                setEmailWarnMessage(message);
                return false;
            case EMAIL_NO_VALID:
                message = getApplication().getString(R.string.email_not_valid);
                setEmailWarnMessage(message);
                return false;
            case EMAIL_TO_CONFIRM_EMPTY:
                message = getApplication().getString(R.string.introduce_an_email_to_confirm);
                setEmailToConfirmWarnMessage(message);
                return false;
            case EMAILS_NOT_MATCHES:
                message = getApplication().getString(R.string.emails_not_matches);
                setEmailToConfirmWarnMessage(message);
                return false;
            default:
                return true;

        }
    }
}
