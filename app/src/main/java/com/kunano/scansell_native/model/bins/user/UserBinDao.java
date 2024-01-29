package com.kunano.scansell_native.model.bins.user;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.business.Business;

import java.util.List;

@Dao
public interface UserBinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    ListenableFuture<Long> sendBusinessToBin(UserBin businessBin);

    //Restorage business

    @Query("DELETE FROM UserBin WHERE businessIdFk IN (:businessId)")
    public  ListenableFuture<Integer> restorageBusiness(Long businessId);



    //Get products in the bind
    @Query("SELECT * FROM business WHERE EXISTS (SELECT 1 FROM userbin" +
            " WHERE userbin.businessIdFk = business.businessId) " )
    public LiveData<List<Business>> getBusinessInBin();




}
