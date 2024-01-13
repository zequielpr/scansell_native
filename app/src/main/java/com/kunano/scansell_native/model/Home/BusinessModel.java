package com.kunano.scansell_native.model.Home;

import android.os.Build;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.kunano.scansell_native.model.db.Business;
import com.kunano.scansell_native.model.db.BusinessDao;
import com.kunano.scansell_native.model.db.DB;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BusinessModel {


   private String name;
   private String address;
    private BusinessDao businessDao = DB.DB.businessDao();

    public BusinessModel(){
        super();
    }


    public BusinessModel(BusinessDao businessDao){
        this.businessDao = businessDao;
    }

    public BusinessModel(String name, String address) {
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


    Business business;
    //Add business
    public CompletableFuture<Boolean> addBusiness(){

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        business = new Business();

        business.businessName = this.name;
        business.businessAddress = this.address;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            business.cratingDate = LocalDateTime.now().toString();
        }



        ListenableFuture<Long> insertBusines = businessDao.insertBusiness(business);
    /* for (int i = 1; i < 5000; i++){
            insertBusines = businessDao.insertBusiness(business);
        }*/

        Futures.addCallback(insertBusines,  new FutureCallback<Long>() {
            @Override
            public void onSuccess(Long result) {
                future.complete(true);
            }

            @Override
            public void onFailure(Throwable t) {
                future.complete(false);
                System.err.println("Failed to insert users: " + t.getMessage());
            }
        }, MoreExecutors.directExecutor());

        return future;
    }

    public void addBusinessList(List<Business> businessesList){
        businessDao.insertBusinessList(businessesList);
    }


    //Delet business
    public CompletableFuture<Boolean> deleteBusinessOffline(Business business){
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        ListenableFuture<Integer> deleteBusiness = businessDao.delete(business);


        Futures.addCallback(deleteBusiness, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if(result == 1){
                    future.complete(true);
                } else{
                    future.complete(false);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                future.complete(false);

            }
        }, MoreExecutors.directExecutor());

        return future;

    }


    //Get business list data
    public LiveData<List<Business>> getBusinessEsList(){
        return businessDao.getAllBusinesses();
    }






}
