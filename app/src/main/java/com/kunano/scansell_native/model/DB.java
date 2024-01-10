package com.kunano.scansell_native.model;


import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunano.scansell_native.db.AppDatabase;

public class DB {
    static DocumentReference documentReference;

    public static AppDatabase db;

    public static  FirebaseFirestore db0 = FirebaseFirestore.getInstance();

    public  static DocumentReference getUserDocument(){

        try {

            System.out.println("holaaa");
            documentReference = db0.collection("users").document("user_id");
            return documentReference;
        }catch (Exception e){
            Log.d(TAG, "Error getting documents: ", e);
            return documentReference;
        }
    }
}
