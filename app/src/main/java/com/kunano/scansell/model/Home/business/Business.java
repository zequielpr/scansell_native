package com.kunano.scansell.model.Home.business;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;


@Entity
public class Business {

    @PrimaryKey(autoGenerate = true)
    private long businessId;

    @ColumnInfo(name = "business_name")
    private String businessName;

    @ColumnInfo(defaultValue = "business_address")
    private String businessAddress;

    @ColumnInfo(defaultValue = "creating_date")
    private LocalDateTime cratingDate;
    @Ignore
    public Business(){
    }

    public Business(String businessName, String businessAddress, LocalDateTime cratingDate) {
        this.businessName = businessName;
        this.businessAddress = businessAddress;
        this.cratingDate = cratingDate;
    }



    public long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public LocalDateTime getCratingDate() {
        return cratingDate;
    }

    public void setCratingDate(LocalDateTime cratingDate) {
        this.cratingDate = cratingDate;
    }

}