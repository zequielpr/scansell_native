package com.kunano.scansell_native.ui.login.sing_in;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.ValidateData;

public class SignInViewModel extends AndroidViewModel {
    private MutableLiveData<String> emailWarn;
    private MutableLiveData<String> passwdWarn;

    public SignInViewModel(@NonNull Application application) {
        super(application);
        emailWarn = new MutableLiveData<>();
        passwdWarn = new MutableLiveData<>();
    }

    public boolean validateEmailAndPasswd(String email, String passwd){
        boolean result = true;

        result = validateEmail(email);

        if (passwd.isEmpty()){
            passwdWarn.postValue(getApplication().getString(R.string.introduce_account_passwd));
            result = false;
        }else {
            passwdWarn.postValue(null);
        }
        return result;
    }

    public boolean validateEmail(String email){

        if(email.isEmpty()){
            emailWarn.postValue(getApplication().getString(R.string.intro_account_email));
            return false;
        } else if (!ValidateData.validateEmailAddress(email)) {
            emailWarn.postValue(getApplication().getString(R.string.email_not_valid));
            return false;
        }else {
            emailWarn.postValue(null);
            return true;
        }
    }




    public MutableLiveData<String> getEmailWarn() {
        return emailWarn;
    }

    public void setEmailWarn(String emailWarn) {
        this.emailWarn.postValue(emailWarn);
    }

    public MutableLiveData<String> getPasswdWarn() {
        return passwdWarn;
    }

    public void setPasswdWarn(String passwdWarn) {
        this.passwdWarn.postValue(passwdWarn);
    }
}
