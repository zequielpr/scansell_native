package com.kunano.scansell_native.model.sell;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface ReceiptDao {


    @Query("SELECT * FROM receipt WHERE receipt.businessIdFK = (:businessId) " +
            "AND receiptId LIKE (:query) ORDER BY receipt.sellingDate DESC")
    LiveData<List<Receipt>> getReceipts(Long businessId, String query);


    @Query("SELECT * FROM receipt WHERE receipt.businessIdFK = (:businessId)" +
            "AND receipt.receiptId = (:receiptId)")
    LiveData<Receipt> getReceiptById(Long businessId, String receiptId);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    ListenableFuture<Long> insertReceipt(Receipt receipt);

    @Delete
    ListenableFuture<Integer> deleteReceipt(Receipt receipt);



    @Query("SELECT * FROM receipt WHERE receipt.businessIdFK = (:businessId) " +
            "AND sellingDate >= (:startOfCurrentWeek)")
    LiveData<List<Receipt>> geCurrentWeekSells(Long businessId, LocalDateTime startOfCurrentWeek);

    @Query("SELECT * FROM receipt WHERE receipt.businessIdFK = (:businessId) " +
            "AND sellingDate <= (:startOfLastWeek)")
    LiveData<List<Receipt>> getLastWeekSells(Long businessId, LocalDateTime startOfLastWeek);

}
