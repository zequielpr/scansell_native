package com.kunano.scansell.repository.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kunano.scansell.components.ViewModelListener;

public class PremiumRepository {
    private FirebaseDB firebaseDb;

    public PremiumRepository(){
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

        firebaseDb.getUserDocument(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

    public void getPremiumStateRealTime(String productId, String userId, ViewModelListener<Boolean> listener){
        firebaseDb.getUserDocument(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.get(productId) == null){
                    listener.result(false);
                } else if (documentSnapshot.get(productId) instanceof  Boolean) {
                    Boolean result = (boolean)documentSnapshot.get(productId);
                    listener.result(result);
                } else {
                    listener.result(false);
                }
            }
        });

    }



    public interface premiumListener<S, F>{
        abstract void onSuccess(S result);

        abstract void onFailure(F result);
    }

}
