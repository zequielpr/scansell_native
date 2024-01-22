package com.kunano.scansell_native.model.Home.product;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.db.relationship.ProductWithImage;

@Dao
public interface ProductImgDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertProductImg(ProductImg productImg);


    @Transaction
    @Query("SELECT * FROM product  WHERE productId = :businessId")
    public ListenableFuture<ProductWithImage> getBusinessWithProduct(String businessId);
}
