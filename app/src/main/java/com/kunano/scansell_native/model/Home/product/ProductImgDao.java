package com.kunano.scansell_native.model.Home.product;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface ProductImgDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertProductImg(ProductImg productImg);


    @Transaction
    @Query("SELECT * FROM productImg  WHERE productIdFk = :productId")
    public ListenableFuture<ProductImg> getProductImg(String productId);
}
