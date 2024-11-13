package com.kunano.scansell.model.bins.business;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell.model.Home.product.Product;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface BusinessBinDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    ListenableFuture<Long> senProductToBin(BusinessBin businessBin);

    //Restorage product

    @Query("DELETE FROM BusinessBin WHERE productIdFk IN (:productId) AND businessIdFk IN (:businessId)")
    public  ListenableFuture<Integer> restorageProduct(String productId, Long businessId);



    //Get products in the bind
    @Query("SELECT productId, product.businessIdFK, product_name,  buying_price, " +
            " selling_price,  stock, cratingDate FROM product INNER JOIN (SELECT * FROM BusinessBin" +
            " WHERE BusinessBin.businessIdFk = (:businessId)) as bin WHERE bin.productIdFk = productId AND " +
            "bin.businessIdFk = product.businessIdFK ORDER BY recyclingDate DESC" )
    public LiveData<List<Product>> getProductsInBin(long businessId);


    //Get recycle date
    @Query("SELECT recyclingDate FROM businessbin WHERE productIdFk IN(:productId) ")
    public ListenableFuture<LocalDate> getRecycleDate(String productId);
}
