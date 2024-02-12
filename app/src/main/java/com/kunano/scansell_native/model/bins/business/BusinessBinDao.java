package com.kunano.scansell_native.model.bins.business;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.product.Product;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface BusinessBinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    ListenableFuture<Long> senProductToBin(BusinessBin businessBin);

    //Restorage product

    @Query("DELETE FROM BusinessBin WHERE productIdFk IN (:productId)")
    public  ListenableFuture<Integer> restorageProduct(String productId);



    //Get products in the bind
    @Query("SELECT * FROM product WHERE EXISTS (SELECT 1 FROM BusinessBin" +
            " WHERE BusinessBin.productIdFk  = product.productId AND BusinessBin.businessIdFk = (:businessId)) " )
    public LiveData<List<Product>> getProductsInBin(long businessId);


    //Get recycle date
    @Query("SELECT recyclingDate FROM businessbin WHERE productIdFk IN(:productId) ")
    public ListenableFuture<LocalDate> getRecycleDate(String productId);
}
