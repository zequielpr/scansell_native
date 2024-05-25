package com.kunano.scansell_native.ui.sales.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.kunano.scansell_native.components.ViewModelListener;
import com.kunano.scansell_native.ui.login.LogInActivity;

public class AccountHelper extends UserData {
    public static final String SIG_IN_AGAIN = "Log in again";
    public static final String SUCCESS = "SUCCESS";
    public static final String NETWORK_ERROR = "network error";
    public static final String CREDENTIAL_NOT_LONGER_VALID = "credential is no longer valid";
    private static final String UNUSUAL_ACTIVITY = "unusual activity";

    private FirebaseUser currentUser;
    private static FirebaseAuth firebaseAuth;
    private UserProfileChangeRequest userProfileChangeRequest;


    public enum SEND_EMAIL_VERIFY_RESULT {
        SUCCESS,
        NETWORK_ERROR,
        UNKNOWN_ERROR,
        UNUSUAL_ACTIVITY
    }

    public AccountHelper() {
        super();
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        currentUser = firebaseAuth.getCurrentUser();
    }


    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public String getUserEmail() {
        if (currentUser != null) userEmail = currentUser.getEmail();
        return userEmail;
    }

    public String getUserId() {
        if (currentUser != null) return currentUser.getUid();
        return null;
    }

    public boolean isEmailVerified() {
        return currentUser == null?false:currentUser.isEmailVerified();
    }



    public String getUserName() {
        if (currentUser != null) userName = currentUser.getDisplayName();
        return userName;
    }

    public void signOut(Fragment fragment) {
        firebaseAuth.signOut();
        Intent intent = new Intent(fragment.getContext(), LogInActivity.class);

        fragment.startActivity(intent);
        fragment.getActivity().finish();

    }

    public void signOut(Activity activity) {
        firebaseAuth.signOut();
        Intent intent = new Intent(activity, LogInActivity.class);

        activity.startActivity(intent);
        activity.finish();

    }



    public void sendEmailVerification(ViewModelListener<SEND_EMAIL_VERIFY_RESULT> listener) {
        if (!currentUser.isEmailVerified()) {
            currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    listener.result(SEND_EMAIL_VERIFY_RESULT.SUCCESS);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("result: " + e.getMessage());
                    if (e.getMessage().contains(NETWORK_ERROR)){
                        listener.result(SEND_EMAIL_VERIFY_RESULT.NETWORK_ERROR);
                    } else if (e.getMessage().contains(UNUSUAL_ACTIVITY)) {
                        listener.result(SEND_EMAIL_VERIFY_RESULT.UNUSUAL_ACTIVITY);
                    } else {
                        listener.result(SEND_EMAIL_VERIFY_RESULT.UNKNOWN_ERROR);
                    }
                }
            });
        }
    }


    public void setUserName(String userName, ViewModelListener<String> listenResponse) {
        userProfileChangeRequest = new UserProfileChangeRequest.
                Builder().setDisplayName(userName).build();

        if (currentUser != null) currentUser.updateProfile(userProfileChangeRequest).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AccountHelper.this.userName = userName;
                        listenResponse.result(SUCCESS);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listenResponse.result(e.getMessage());
                    }
                });

    }



    public void setUserEmail(String userEmail, ViewModelListener<String> listener) {
        if (currentUser == null) return;




        currentUser.verifyBeforeUpdateEmail(userEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.result(SUCCESS);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.result(e.getMessage());
                    }
                });


        // Prompt the user to re-provide their sign-in credentials

    }

    public void setPassword(String passwd, ViewModelListener<String> listener) {
        currentUser.updatePassword(passwd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.result(SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.result(e.getMessage());
            }
        });

    }

    public void deleteAccount(ViewModelListener<String> listener) {
        currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.result(SUCCESS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.result(e.getMessage());
            }
        });
    }

    public Uri getProfilePic(){
        return currentUser != null? currentUser.getPhotoUrl():null;
    }


}
