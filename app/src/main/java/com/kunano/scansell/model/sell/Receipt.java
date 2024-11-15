package com.kunano.scansell.model.sell;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell.model.Home.business.Business;

import org.checkerframework.common.aliasing.qual.Unique;

import java.time.LocalDateTime;

@Entity(foreignKeys = @ForeignKey(entity = Business.class,
        parentColumns = "businessId",
        childColumns = "businessIdFK",
        onDelete = ForeignKey.CASCADE),
        indices = {@Index("businessIdFK"), @Index("receiptId")})
public class Receipt {

    @PrimaryKey()
    @NonNull
    private String receiptId;
    private Long businessIdFK;
    @Unique
    private String userEmail;
    private LocalDateTime sellingDate;
    private double spentAmount;


    @Ignore
    public Receipt(){

    }

    public Receipt(String receiptId, Long businessIdFK, @Unique String userEmail, LocalDateTime sellingDate, double spentAmount) {
        this.receiptId = receiptId;
        this.businessIdFK = businessIdFK;
        this.userEmail = userEmail;
        this.sellingDate = sellingDate;
        this.spentAmount = spentAmount;
    }

    public String getReceiptId() {
        return receiptId.toUpperCase().substring(0,18);
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public Long getBusinessIdFK() {
        return businessIdFK;
    }

    public void setBusinessIdFK(Long businessIdFK) {
        this.businessIdFK = businessIdFK;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getSellingDate() {
        return sellingDate;
    }

    public void setSellingDate(LocalDateTime sellingDate) {
        this.sellingDate = sellingDate;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }
}
