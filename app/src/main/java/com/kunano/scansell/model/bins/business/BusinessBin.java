package com.kunano.scansell.model.bins.business;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.kunano.scansell.model.Home.product.Product;

import java.time.LocalDateTime;

@Entity(tableName = "BusinessBin",
        primaryKeys = {"businessIdFk", "productIdFk"},
        foreignKeys = @ForeignKey(entity = Product.class,
                parentColumns = {"productId", "businessIdFK"},
                childColumns = {"productIdFk", "businessIdFk"},
                onDelete = ForeignKey.CASCADE),
indices = {@Index(value = {"businessIdFk", "productIdFk"}, unique = true)})
public class BusinessBin {

    @NonNull
    private long businessIdFk;

    @NonNull
    private String productIdFk;

    LocalDateTime recyclingDate;

    @Ignore
    public BusinessBin(){
        super();
    }

    public BusinessBin(long businessIdFk, String productIdFk, LocalDateTime recyclingDate) {
        this.businessIdFk = businessIdFk;
        this.productIdFk = productIdFk;
        this.recyclingDate = recyclingDate;
    }

    public long getBusinessIdFk() {
        return businessIdFk;
    }

    public void setBusinessIdFk(long businessIdFk) {
        this.businessIdFk = businessIdFk;
    }

    public String getProductIdFk() {
        return productIdFk;
    }

    public void setProductIdFk(String productIdFk) {
        this.productIdFk = productIdFk;
    }

    public LocalDateTime getRecyclingDate() {
        return recyclingDate;
    }

    public void setRecyclingDate(LocalDateTime recyclingDate) {
        this.recyclingDate = recyclingDate;
    }
}
