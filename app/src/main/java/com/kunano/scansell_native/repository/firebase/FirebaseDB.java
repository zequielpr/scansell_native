package com.kunano.scansell_native.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDB {

    private static final String USER_COLLECTION = "users";

    private static FirebaseFirestore firebaseFirestore;

    public FirebaseDB() {
        if (firebaseFirestore == null) firebaseFirestore = FirebaseFirestore.getInstance();
    }


    public Task<Void> setPremiumStatus(String productId, String userId, boolean premiumStatus) {

        Map<String, Object> data = new HashMap<>();
        data.put(productId, premiumStatus);

        return firebaseFirestore.collection(USER_COLLECTION).document(userId).set(data);
    }

    public DocumentReference getUserDocument(String userId){
        return firebaseFirestore.collection(USER_COLLECTION).document(userId);
    }


}
