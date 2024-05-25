package com.kunano.scansell_native.ui.sales.auth;

import com.kunano.scansell_native.model.ValidateData;

public class UserData{
    public static enum EmailWarns{
        EMAIL_VALID,
        EMAIL_NO_VALID,
        EMAIL_EMPTY,
        EMAIL_TO_CONFIRM_EMPTY,
        EMAILS_NOT_MATCHES,
    }

    protected String userName;
    protected String userEmail;
    private String password;

    public UserData(){
        super();
    }


    public UserData(String userName, String userEmail, String password) {
        super();
        this.userName = userName;
        this.userEmail = userEmail;
        this.password = password;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmailWarns validateEmail(String email, String emailToConfirm ){

        EmailWarns emailWarns = EmailWarns.EMAIL_VALID;

        if (email.isEmpty()) emailWarns = EmailWarns.EMAIL_EMPTY;
        else if (!ValidateData.validateEmailAddress(email))emailWarns = EmailWarns.EMAIL_NO_VALID;
        else if (emailToConfirm.isEmpty()) emailWarns = EmailWarns.EMAIL_TO_CONFIRM_EMPTY;
        else if (!email.equals(emailToConfirm))emailWarns = EmailWarns.EMAILS_NOT_MATCHES;

        return emailWarns;
    }
}
