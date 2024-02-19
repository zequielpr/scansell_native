package com.kunano.scansell_native.model.Home.product;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertProduct(Product product);

    @Delete
    ListenableFuture<Integer> deleteProduct(Product product);

    @Transaction
    @Query("SELECT * FROM product WHERE product.businessIdFk = (:businessId) AND productId =(:productId)" )
    public ListenableFuture<Product> getProductByIds(Long businessId, String productId);
















}
