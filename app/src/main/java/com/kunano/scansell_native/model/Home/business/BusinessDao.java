package com.kunano.scansell_native.model.Home.business;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;

import java.util.List;

@Dao
public interface BusinessDao  {
    @Query("SELECT * FROM business WHERE NOT EXISTS (SELECT 1 FROM userbin" +
            " WHERE userbin.businessIdFk = business.businessId) " )
    LiveData<List<Business>> getAllBusinesses();

    @Query("SELECT * FROM Business WHERE businessId IN (:businessId)")
    LiveData<Business> getBusinessById(Long businessId);

    /*
    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);
     */


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertBusiness(Business business);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Void> insertBusinessList(List<Business> business);

    @Delete
    ListenableFuture<Integer> delete(Business business);


    @Transaction
    @Query("SELECT * FROM Business  WHERE businessId = :businessId")
    public LiveData<BusinessWithProduct> getBusinessWithProduct(Long businessId);
}