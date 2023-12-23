package com.kunano.scansell_native.model.Home;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kunano.scansell_native.model.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Home {

    public CollectionReference getBusinessCollection() {
        return businessCollection;
    }

    private CollectionReference businessCollection = DB.getUserDocument().collection("businesses");

    //Add business
    public boolean addBusiness(HashMap<String, Object> data){
        return businessCollection.add(data).isSuccessful();
    }

    //Delet business
    public boolean deleteBusiness(String id_Busines){
        return businessCollection.document(id_Busines).delete().isSuccessful();
    }


    //Get business list data
    public CompletableFuture<List<Map<String, Object>>> getBusinessListDataAsync(){

        CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
        List<Map<String, Object>> BusinessesListData = new ArrayList<>();

        businessCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()  {
            @Override
            public  void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Map<String, Object> businessData;
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        //Get the business ID
                        businessData = document.getData();
                        businessData.put("business_id", document.getId());

                        BusinessesListData.add(businessData);
                    }
                    System.out.println("final data" + BusinessesListData);

                    future.complete(BusinessesListData);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        return future;
    }



}
