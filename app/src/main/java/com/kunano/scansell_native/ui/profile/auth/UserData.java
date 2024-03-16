package com.kunano.scansell_native.ui.profile.auth;

public class UserData{

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
}
