package com.kunano.scansell.model.sell.payment;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface PaymentDao {

    @Insert
    public ListenableFuture<Long> insertPayment(Payment payment);

    @Delete
    public ListenableFuture<Integer> deletePayment(Payment payment);

    @Query("SELECT * FROM payment WHERE receiptIdFK = (:receiptId)")
    LiveData<Payment> getPayment(String receiptId);


}
