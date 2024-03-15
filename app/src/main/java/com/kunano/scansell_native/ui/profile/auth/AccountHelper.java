package com.kunano.scansell_native.ui.profile.admin.account;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.kunano.scansell_native.ui.components.ListenResponse;

public class AccountHelper {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String userName;
    private String userEmail;
    private UserProfileChangeRequest userProfileChangeRequest;


    public AccountHelper() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }


    public String getUserEmail() {
        if (currentUser != null) userEmail = currentUser.getEmail();
        return userEmail;
    }

    public boolean isEmailVerified(){
        return currentUser.isEmailVerified();
    }


    public String getUserName() {
        if (currentUser != null) userName = currentUser.getDisplayName();
        return userName;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }


    public void sendEmailVerification(ListenResponse listenResponse){
        if (!currentUser.isEmailVerified()){
            currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    listenResponse.isSuccessfull(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listenResponse.isSuccessfull(false);
                }
            });
        }
    }


    public void setUserName(String userName, ListenResponse listenResponse) {
        userProfileChangeRequest = new UserProfileChangeRequest.
                Builder().setDisplayName(userName).build();

        if (currentUser != null) currentUser.updateProfile(userProfileChangeRequest).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AccountHelper.this.userName = userName;
                        listenResponse.isSuccessfull(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listenResponse.isSuccessfull(false);
                    }
                });

    }


    public void setUserEmail(String userEmail, ListenResponse listenResponse) {
        if (currentUser == null) return;
        currentUser.verifyBeforeUpdateEmail(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                AccountHelper.this.userEmail = userEmail;
                listenResponse.isSuccessfull(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listenResponse.isSuccessfull(false);
            }
        });

    }

    private void changePassword(String passwd, ListenResponse listenResponse) {
        currentUser.updatePassword(passwd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listenResponse.isSuccessfull(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listenResponse.isSuccessfull(false);
            }
        });

    }

    public void deleteAccount(ListenResponse listenResponse) {
        currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listenResponse.isSuccessfull(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listenResponse.isSuccessfull(false);
            }
        });
    }


}
