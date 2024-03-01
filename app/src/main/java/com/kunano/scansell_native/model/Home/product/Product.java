package com.kunano.scansell_native.model.Home.product;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.kunano.scansell_native.model.Home.business.Business;

@Entity(tableName = "product",
        primaryKeys = {"productId", "businessIdFK"},
        foreignKeys = @ForeignKey(entity = Business.class,
                parentColumns = "businessId",
                childColumns = "businessIdFK",
                onDelete = ForeignKey.CASCADE),
indices = {@Index(value = {"businessIdFK", "productId"}, unique = true), })
public class Product {

    @NonNull
    private String  productId;

    @NonNull
    private  long businessIdFK;

    @ColumnInfo(name = "product_name")
    private  String productName;

    @ColumnInfo(defaultValue = "0.0")
    private  double buying_price;
    @ColumnInfo(defaultValue = "0.0")
    private  double selling_price;

    @ColumnInfo(defaultValue = "0.0")
    private  Integer stock;

    @ColumnInfo(defaultValue = "creating_date")
    private  String cratingDate;


    public Product(){

    }



    public Product(String productId,  long createdInbusinessId, String productName, double buying_price,
                   double selling_price, Integer stock, String cratingDate) {
        this.productId = productId;
        this. businessIdFK = createdInbusinessId;
        this.productName = productName;
        this.buying_price = buying_price;
        this.selling_price = selling_price;
        this.stock = stock;
        this.cratingDate = cratingDate;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getBusinessIdFK() {
        return businessIdFK;
    }

    public void setBusinessIdFK(long businessIdFK) {
        this.businessIdFK = businessIdFK;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getBuying_price() {
        return buying_price;
    }

    public void setBuying_price(double buying_price) {
        this.buying_price = buying_price;
    }

    public double getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(double selling_price) {
        this.selling_price = selling_price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCratingDate() {
        return cratingDate;
    }

    public void setCratingDate(String cratingDate) {
        this.cratingDate = cratingDate;
    }
}

