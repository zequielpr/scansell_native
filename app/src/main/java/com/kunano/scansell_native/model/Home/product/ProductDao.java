package com.kunano.scansell_native.model.Home.product;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    ListenableFuture<Long> insertProduct(Product product);

@Transaction
    @Query("SELECT * FROM business")
    public LiveData<List<BusinessWithProduct>> getBusinessWithProduct();

}
