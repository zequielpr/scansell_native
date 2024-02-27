package com.kunano.scansell_native.model.sell.sold_products;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.product.Product;

import java.util.List;

@Dao
public interface SoldProductDao {

    @Insert
    ListenableFuture<Long> insertSoldProduct(SoldProduct soldProduct);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<List<Long>> insertSoldProductList(List<SoldProduct> soldProductList);


    @Delete
    ListenableFuture<Integer> deleteSoldProduct(SoldProduct soldProduct);


    //Stil to be develop
    @Query("SELECT * FROM product INNER JOIN (SELECT * FROM soldproduct" +
            " WHERE receiptIdFK = (:receiptId)) ON product.productId = productIdFK")
    LiveData<List<Product>> getSoldProducts(String receiptId);

}