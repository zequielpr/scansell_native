package com.kunano.scansell_native.ui.profile.admin.account.password;

import android.app.Application;
import android.content.Context;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;

public class PasswordViewModel extends AndroidViewModel {
    private MutableLiveData<Integer> hideOrShowPasswordMutableData;
    private MutableLiveData<Spanned> upperAndLowerCaseMutableData;
    private MutableLiveData<Spanned> atLeastOneDigit;
    private MutableLiveData<Spanned> atLeastEightCharacters;
    private MutableLiveData<Boolean> isPasswordValid;
    private MutableLiveData<Integer> passwdNotMatchVisibility;
    private ValidatePasswd validatePasswd;

    public PasswordViewModel(@NonNull Application application){
        super(application);
        hideOrShowPasswordMutableData = new MutableLiveData<>();
        isPasswordValid = new MutableLiveData<>(false);
        passwdNotMatchVisibility = new MutableLiveData<>(View.GONE);

        upperAndLowerCaseMutableData = new MutableLiveData<>(HtmlCompat.fromHtml(
                "<strike>"+getApplication().getString(R.string.upper_and_lower_case)+"</strike>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        atLeastOneDigit = new MutableLiveData<>(HtmlCompat.fromHtml(
                "<strike>"+getApplication().getString(R.string.at_least_one_digit)+"</strike>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        atLeastEightCharacters = new MutableLiveData<>(HtmlCompat.fromHtml(
                "<strike>"+getApplication().getString(R.string.at_least_eight_characters)+"</strike>",
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        validatePasswd = new ValidatePasswd(this);
    }


    public TextWatcher getPasswordEditTextWatcher(Context context){
        return validatePasswd.getPasswordEditTextWatcher(context);
    }

    public TextWatcher getConfirmPasswordEditTextWatcher(EditText passwordEditText){
       return validatePasswd.getConfirmPasswordEditTextWatcher(passwordEditText);
    }

    public boolean checkIfPasswdMatch(String passwd, String passToConfirm){
        return  validatePasswd.checkIfPasswdMatch(passwd.trim(), passToConfirm.trim());
    }




    public MutableLiveData<Integer> getHideOrShowPasswordMutableData() {
        return hideOrShowPasswordMutableData;
    }

    public MutableLiveData<Spanned> getUpperAndLowerCaseMutableData() {
        return upperAndLowerCaseMutableData;
    }

    public void setUpperAndLowerCaseMutableData(Spanned upperAndLowerCaseMutableData) {
        this.upperAndLowerCaseMutableData.postValue(upperAndLowerCaseMutableData);
    }

    public void setHideOrShowPasswordMutableData(Integer hideOrShowPasswordMutableData) {
        this.hideOrShowPasswordMutableData.postValue(hideOrShowPasswordMutableData);
    }

    public MutableLiveData<Spanned> getAtLeastOneDigit() {
        return atLeastOneDigit;
    }

    public void setAtLeastOneDigit(Spanned atLeastOneDigit) {
        this.atLeastOneDigit.postValue(atLeastOneDigit);
    }

    public MutableLiveData<Spanned> getAtLeastEightCharacters() {
        return atLeastEightCharacters;
    }

    public void setAtLeastEightCharacters(Spanned atLeastEightCharacters) {
        this.atLeastEightCharacters.postValue(atLeastEightCharacters);
    }

    public MutableLiveData<Boolean> getIsPasswordValid() {
        return isPasswordValid;
    }

    public void setIsPasswordValid(Boolean isPasswordValid) {
        this.isPasswordValid.postValue(isPasswordValid);
    }

    public MutableLiveData<Integer> getPasswdNotMatchVisibility() {
        return passwdNotMatchVisibility;
    }

    public void setPasswdNotMatchVisibility(Integer passwdNotMatchVisibility) {
        this.passwdNotMatchVisibility.postValue(passwdNotMatchVisibility);
    }

    public CompoundButton.OnCheckedChangeListener getShowOrHidePasswordListener(){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isToHide) {
                if (isToHide){
                    setHideOrShowPasswordMutableData(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                }else {
                    setHideOrShowPasswordMutableData(
                            android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                }
            }
        };
    }
}
