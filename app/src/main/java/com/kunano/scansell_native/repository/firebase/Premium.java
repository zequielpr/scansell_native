package com.kunano.scansell_native.repository.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kunano.scansell_native.ui.components.ViewModelListener;

public class Premium {
    private FirebaseDB firebaseDb;

    public Premium(){
        firebaseDb = new FirebaseDB();
    }

    public void setPremiumState(String productId, String userId,
                                 boolean premiumStatus, ViewModelListener<Boolean> listener){

        firebaseDb.setPremiumStatus(productId, userId, premiumStatus).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.result(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.result(false);
                    }
                });
    }

    public void getPremiumState(String productId, String userId, premiumListener<Boolean, Void> listener){

        firebaseDb.getUserDocument(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get(productId) == null){
                    listener.onSuccess(false);
                }else {
                    Boolean result = (boolean)documentSnapshot.get(productId);
                    listener.onSuccess(result);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(null);
            }
        });
    }

    public interface premiumListener<S, F>{
        abstract void onSuccess(S result);

        abstract void onFailure(F result);
    }

}
