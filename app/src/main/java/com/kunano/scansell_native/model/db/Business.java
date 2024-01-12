package com.kunano.scansell_native.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity

public class Business {

    @PrimaryKey(autoGenerate = true)
    public int businessId;

    @ColumnInfo(name = "business_name")
    public String businessName;

    @ColumnInfo(defaultValue = "business_address")
    public String businessAddress;

    @ColumnInfo(defaultValue = "creating_date")
    public String cratingDate;
}