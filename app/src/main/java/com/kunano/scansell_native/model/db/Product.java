package com.kunano.scansell_native.model.db;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {
    @PrimaryKey(autoGenerate = true)
    public long productId;
    public long createdInbusinessId;

    @ColumnInfo(name = "product_name")
    public String productName;

    @ColumnInfo(defaultValue = "0.0")
    public double buying_price;
    @ColumnInfo(defaultValue = "0.0")
    public double selling_price;

    @ColumnInfo(defaultValue = "0.0")
    public double stock;

    @ColumnInfo(defaultValue = "Null")
    public byte[] img;

    @ColumnInfo(defaultValue = "creating_date")
    public String cratingDate;
}

