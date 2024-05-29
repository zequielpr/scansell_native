package com.kunano.scansell.model.sell.payment.card;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.google.common.util.concurrent.ListenableFuture;

@Dao
public interface CardDao{
    @Insert
    public ListenableFuture<Long> insertPaymentMethod(Card paymentMethod);

    @Delete
    public ListenableFuture<Integer> deletePaymentMethod(Card paymentMethod);

}
