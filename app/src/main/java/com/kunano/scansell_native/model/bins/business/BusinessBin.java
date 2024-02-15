package com.kunano.scansell_native.model.bins.business;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;

import java.time.LocalDate;

@Entity(tableName = "BusinessBin",

        foreignKeys = {
                @ForeignKey(entity = Business.class,
                parentColumns = "businessId",
                childColumns = "businessIdFk",
                onDelete = ForeignKey.CASCADE),

                @ForeignKey(entity = Product.class,
                        parentColumns = "productId",
                        childColumns = "productIdFk",
                        onDelete = ForeignKey.CASCADE),

        },
        indices = {@Index("businessIdFk"), @Index("productIdFk")})
public class BusinessBin {

    @PrimaryKey(autoGenerate = true)
    private long binId;


    private long businessIdFk;

    private String productIdFk;

    LocalDate recyclingDate;


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

    public long getBinId() {
        return binId;
    }

    public void setBinId(long binId) {
        this.binId = binId;
    }
}
