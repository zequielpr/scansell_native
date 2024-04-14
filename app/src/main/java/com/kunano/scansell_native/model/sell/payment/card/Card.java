package com.kunano.scansell_native.model.sell.payment.card;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell_native.model.sell.payment.Payment;

@Entity(
        foreignKeys = @ForeignKey(entity = Payment.class,
        parentColumns = {"receiptIdFK"},
        childColumns = {"receiptIdFKInCard" },
        onDelete = ForeignKey.CASCADE),
        indices = { @Index("receiptIdFKInCard")})
public class Card {


    @NonNull
    @PrimaryKey
    private String receiptIdFKInCard;


    public Card( String receiptIdFKInCard) {
        this.receiptIdFKInCard = receiptIdFKInCard;
    }


    public String getReceiptIdFKInCard() {
        return receiptIdFKInCard;
    }

    public void setReceiptIdFKInCard(String receiptIdFKInCard) {
        this.receiptIdFKInCard = receiptIdFKInCard;
    }
}
