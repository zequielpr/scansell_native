package com.kunano.scansell_native.model;


import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DB {
    static DocumentReference documentReference;

    private static  FirebaseFirestore db = FirebaseFirestore.getInstance();
    public  static DocumentReference getUserDocument(){
        try {

            System.out.println("holaaa");
            documentReference = db.collection("users").document("user_id");
            return documentReference;
        }catch (Exception e){
            Log.d(TAG, "Error getting documents: ", e);
            return documentReference;
        }
    }
}
