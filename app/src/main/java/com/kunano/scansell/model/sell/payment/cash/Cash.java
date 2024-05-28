package com.kunano.scansell.model.sell.payment.cash;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell.model.sell.payment.Payment;

@Entity(
        foreignKeys = @ForeignKey(entity = Payment.class,
        parentColumns = {"receiptIdFK"},
        childColumns = {"receiptIdFKInCash" },
        onDelete = ForeignKey.CASCADE),
        indices = {@Index("receiptIdFKInCash")})
public class Cash {

    @NonNull
    @PrimaryKey
    private String receiptIdFKInCash;

    @NonNull
    private double cashTendered;

    @NonNull
    private double cashDue;


    public Cash(@NonNull String receiptIdFKInCash,
                double cashTendered, double cashDue) {
        this.receiptIdFKInCash = receiptIdFKInCash;
        this.cashTendered = cashTendered;
        this.cashDue = cashDue;
    }


    @NonNull
    public String getReceiptIdFKInCash() {
        return receiptIdFKInCash;
    }

    public void setReceiptIdFKInCash(@NonNull String receiptIdFKInCash) {
        this.receiptIdFKInCash = receiptIdFKInCash;
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(double cashTendered) {
        this.cashTendered = cashTendered;
    }

    public double getCashDue() {
        return cashDue;
    }

    public void setCashDue(double cashDue) {
        this.cashDue = cashDue;
    }
}
