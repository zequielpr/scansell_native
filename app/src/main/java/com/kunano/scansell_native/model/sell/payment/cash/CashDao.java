package com.kunano.scansell_native.model.sell.payment.cash;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface CashDao {
    @Insert
    public ListenableFuture<Long> insertPaymentMethod(Cash paymentMethod);

    @Delete
    public ListenableFuture<Integer> deletePaymentMethod(Cash paymentMethod);

    @Query("SELECT * FROM cash WHERE receiptIdFKInCash = (:receiptId)")
    LiveData<Cash> getPaymentMethodByCash(String receiptId);
}
