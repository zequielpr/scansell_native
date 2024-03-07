package com.kunano.scansell_native.model.bins.business;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.kunano.scansell_native.model.Home.product.Product;

import java.time.LocalDate;

@Entity(tableName = "BusinessBin",
        primaryKeys = {"businessIdFk", "productIdFk"},
        foreignKeys = @ForeignKey(entity = Product.class,
                parentColumns = {"productId", "businessIdFK"},
                childColumns = {"productIdFk", "businessIdFk"},
                onDelete = ForeignKey.CASCADE),
indices = {@Index(value = {"businessIdFk", "productIdFk"})})
public class BusinessBin {

    @NonNull
    private long businessIdFk;

    @NonNull
    private String productIdFk;

    LocalDate recyclingDate;

    @Ignore
    public BusinessBin(){
        super();
    }

    public BusinessBin(long businessIdFk, String productIdFk, LocalDate recyclingDate) {
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

    public LocalDate getRecyclingDate() {
        return recyclingDate;
    }

    public void setRecyclingDate(LocalDate recyclingDate) {
        this.recyclingDate = recyclingDate;
    }
}
