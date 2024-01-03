package com.kunano.scansell_native.model;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class DbSetting {
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
        db.disableNetwork();
    }

    public  void habilitarAccesoRed() {
        db.enableNetwork();
    }
}
