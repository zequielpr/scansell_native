package com.kunano.scansell_native.model.Home.product;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertProduct(Product product);

    @Delete
    ListenableFuture<Integer> deleteProduct(Product product);
















}
