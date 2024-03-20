package com.kunano.scansell_native.ui.login.sing_up;

import androidx.lifecycle.ViewModel;

import com.kunano.scansell_native.ui.profile.auth.UserData;

public class SignUpViewModel extends ViewModel {
    private Boolean termsAndPoliciesStateCondition;
    UserData userData;

    public SignUpViewModel(){
        termsAndPoliciesStateCondition = false;
        userData = new UserData();
    }


    public void setEmail(String email){
        userData.setUserEmail(email);
    }

    public void setName(String name){
        userData.setUserName(name);
    }

    public void setPasswd(String passwd){
        userData.setPassword(passwd);
    }



    public String getEmail(){
        return userData.getUserEmail();
    }

    public String getName(){
        return userData.getUserName();
    }

    public String getPasswd(){
       return userData.getPassword();
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public Boolean getTermsAndPoliciesStateCondition() {
        return termsAndPoliciesStateCondition;
    }

    public void setTermsAndPoliciesStateCondition(Boolean termsAndPoliciesStateCondition) {
        this.termsAndPoliciesStateCondition = termsAndPoliciesStateCondition;
    }
}
