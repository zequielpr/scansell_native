package com.kunano.scansell_native.model.sell.payment;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell_native.model.sell.Receipt;

@Entity(
        foreignKeys = @ForeignKey(entity = Receipt.class,
        parentColumns = {"receiptId"},
        childColumns = {"receiptIdFK" },
        onDelete = ForeignKey.CASCADE),
        indices = {@Index("receiptIdFK")})
public class Payment {
    @NonNull
    @PrimaryKey
    protected String receiptIdFK;





    public Payment(String receiptIdFK) {
        this.receiptIdFK = receiptIdFK;
    }



    public String getReceiptIdFK() {
        return receiptIdFK;
    }

    public void setReceiptIdFK(String receiptIdFK) {
        this.receiptIdFK = receiptIdFK;
    }

}
