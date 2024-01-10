package com.kunano.scansell_native.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class DbSetting {
    public static boolean INTERNET_ACCESS;
    FirebaseFirestore db;

    public DbSetting(FirebaseFirestore db){
        this.db = db;
    }


   private FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
           .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
           .build();



    public void habilitarPersistenciaSinConexion() {
        db.getPersistentCacheIndexManager().enableIndexAutoCreation();
        //db.setFirestoreSettings(settings);
    }

    public void setSizeCache() {
        db.setFirestoreSettings(settings);
    }

    public void inabilitarAccesoRed() {
        db.disableNetwork().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                INTERNET_ACCESS = false;
            }
        });

    }

    public  void habilitarAccesoRed() {
        db.enableNetwork().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                INTERNET_ACCESS = true;
            }
        });

    }
}
