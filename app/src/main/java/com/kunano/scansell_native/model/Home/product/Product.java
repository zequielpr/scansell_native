package com.kunano.scansell_native.model.Home.product;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell_native.model.Home.business.Business;

@Entity(tableName = "product",
        foreignKeys = @ForeignKey(entity = Business.class,
                parentColumns = "businessId",
                childColumns = "businessIdFK",
                onDelete = ForeignKey.CASCADE),
indices = {@Index("img")})
public class Product {
    @PrimaryKey(autoGenerate = true)
    private long productId;

    private  long businessIdFK;

    @ColumnInfo(name = "product_name")
    private  String productName;

    @ColumnInfo(defaultValue = "0.0")
    private  double buying_price;
    @ColumnInfo(defaultValue = "0.0")
    private  double selling_price;

    @ColumnInfo(defaultValue = "0.0")
    private  Integer stock;


    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private  byte[] img;

    @ColumnInfo(defaultValue = "creating_date")
    private  String cratingDate;


    public Product(){

    }



    public Product(long createdInbusinessId, String productName, double buying_price,
                   double selling_price, Integer stock, byte[] img, String cratingDate) {
        this. businessIdFK = createdInbusinessId;
        this.productName = productName;
        this.buying_price = buying_price;
        this.selling_price = selling_price;
        this.stock = stock;
        this.img = img;
        this.cratingDate = cratingDate;
    }


    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
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

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getCratingDate() {
        return cratingDate;
    }

    public void setCratingDate(String cratingDate) {
        this.cratingDate = cratingDate;
    }
}

