package com.kunano.scansell_native.model.Home;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kunano.scansell_native.model.DB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Business {


   private String name;
   private String address;
   private DocumentReference documentReferenceBusiness;
    private CollectionReference businessesCollection = DB.getUserDocument().collection("businesses");

    public Business(){
        super();
    }

    public Business(String businessId){
        this.documentReferenceBusiness = businessesCollection.document(businessId);
    }

    public Business(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address.trim();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DocumentReference getDocumentReferenceBusiness() {
        return documentReferenceBusiness;
    }

    public void setDocumentReferenceBusiness(DocumentReference documentReferenceBusiness) {
        this.documentReferenceBusiness = documentReferenceBusiness;
    }

    public CollectionReference getBusinessesCollection() {
        return businessesCollection;
    }


    //Add business
    public CompletableFuture<Boolean> addBusiness(HashMap<String, Object> data){

        CompletableFuture<Boolean> furure = new CompletableFuture<>();

        businessesCollection.document(data.get("name").toString()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                furure.complete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                furure.complete(false);
                Log.d(TAG, "Error adding document: "+ e.getMessage());
            }
        });
        return furure;
    }

    //Delet business
    public boolean deleteBusiness(String id_Busines){
        return businessesCollection.document(id_Busines).delete().isSuccessful();
    }


    //Get business list data
    public CompletableFuture<List<Map<String, Object>>> getBusinessListDataAsync(){

        CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
        List<Map<String, Object>> BusinessesListData = new ArrayList<>();

        businessesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()  {
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

                    future.complete(BusinessesListData);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        return future;
    }



}
