package com.kunano.scansell_native.model.bins.business;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.db.relationship.business.BusinessWithBin;

import java.util.List;

@Dao
public interface BusinessBinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    ListenableFuture<Long> sendBusinessToBin(BusinessBin businessBin);

    //Restorage business

    @Query("DELETE FROM BusinessBin WHERE businessIdFk IN (:businessId)")
    public  ListenableFuture<Integer> restorageBusiness(Long businessId);


    //Get products in the bind
    @Transaction
    @Query("SELECT * FROM Business")
    public LiveData<List<BusinessWithBin>> getBusinessInBin();


    @Query("SELECT * FROM businessbin WHERE businessIdFk in (:businessId)")
    LiveData<BusinessBin> chechIdBusinessInBin(Long businessId);



}
