package com.kunano.scansell_native.model.Home.product;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertProduct(Product product);



    @Query("SELECT * FROM Product WHERE businessIdFK in (:businessId)")
    public LiveData<List<Product>> getProductList(Long[] businessId);



}
