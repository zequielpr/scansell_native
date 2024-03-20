package com.kunano.scansell_native.ui.profile.auth;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kunano.scansell_native.ui.components.ViewModelListener;

public class Auth {

    public static enum SIGN_UP_REQUEST_RESULT {
        SUCCESS,
        NETWORK_ERROR,
        EMAIL_IN_USE,
        UNKNOWN_ERROR
    }


    public static enum SIGN_IN_REQUEST_RESULT {
        SUCCESS,
        NETWORK_ERROR,
        EMAIL_OR_PASSWD_INCORRECT,
        UNKNOWN_ERROR
    }

    public static String OPERATION_EXCEEDED = "unusual activity";

    public static enum RESET_PASSWORD_REQUEST {
        SUCCESS,
        NETWORK_ERROR,
        OPERATION_EXCEEDED,
        UNKNOWN_ERROR
    }

    ;


    private FirebaseAuth firebaseAuth;

    public Auth() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void createUserWithEmailAndPassword(UserData userData, ViewModelListener<SIGN_UP_REQUEST_RESULT>
            result) {
        firebaseAuth.createUserWithEmailAndPassword(userData.getUserEmail(), userData.getPassword()).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        AccountHelper accountHelper = new AccountHelper();
                        accountHelper.setUserName(userData.userName, (r) -> {
                        });

                        result.result(SIGN_UP_REQUEST_RESULT.SUCCESS);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        if (error.contains("email address is already")) {
                            result.result(SIGN_UP_REQUEST_RESULT.EMAIL_IN_USE);
                        } else if (error.contains("network error")) {
                            result.result(SIGN_UP_REQUEST_RESULT.NETWORK_ERROR);
                        } else {
                            result.result(SIGN_UP_REQUEST_RESULT.UNKNOWN_ERROR);
                        }
                    }
                });
    }

    public void signInWithEmailAndPasswd(String email, String passwd,
                                         ViewModelListener<SIGN_IN_REQUEST_RESULT> listener) {
        firebaseAuth.signInWithEmailAndPassword(email, passwd).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        listener.result(SIGN_IN_REQUEST_RESULT.SUCCESS);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        if (error.contains("The supplied auth credential is incorrect")) {
                            listener.result(SIGN_IN_REQUEST_RESULT.EMAIL_OR_PASSWD_INCORRECT);
                        } else if (error.contains("network error")) {
                            listener.result(SIGN_IN_REQUEST_RESULT.NETWORK_ERROR);
                        } else {
                            listener.result(SIGN_IN_REQUEST_RESULT.UNKNOWN_ERROR);
                        }
                    }
                });
    }

    public void recoverPassword(String email, ViewModelListener<RESET_PASSWORD_REQUEST> listener) {
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.result(RESET_PASSWORD_REQUEST.SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Result: " + e.getMessage());
                String error = e.getMessage();

                if (error.contains(AccountHelper.NETWORK_ERROR)) {
                    listener.result(RESET_PASSWORD_REQUEST.NETWORK_ERROR);
                } else if (error.contains(OPERATION_EXCEEDED)) {
                    listener.result(RESET_PASSWORD_REQUEST.OPERATION_EXCEEDED );
                } else {
                    listener.result(RESET_PASSWORD_REQUEST.UNKNOWN_ERROR);
                }
            }
        });
    }

}
