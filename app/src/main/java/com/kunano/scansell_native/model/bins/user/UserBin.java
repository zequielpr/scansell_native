package com.kunano.scansell_native.model.bins.user;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell_native.model.Home.business.Business;

import java.time.LocalDate;

@Entity(tableName = "UserBin",
        foreignKeys = @ForeignKey(entity = Business.class,
                parentColumns = "businessId",
                childColumns = "businessIdFk",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("businessIdFk")})
public class UserBin {
    @PrimaryKey(autoGenerate = true)
    long bindId;

    long businessIdFk;
    LocalDate recyclingDate;


    @Ignore
    public UserBin(){
        super();
    }

    public UserBin(long businessIdFk, LocalDate recyclingDate) {
        this.businessIdFk = businessIdFk;
        this.recyclingDate = recyclingDate;
    }

    public long getBindId() {
        return bindId;
    }

    public void setBindId(long bindId) {
        this.bindId = bindId;
    }

    public long getBusinessIdFk() {
        return businessIdFk;
    }

    public void setBusinessIdFk(long businessIdFk) {
        this.businessIdFk = businessIdFk;
    }

    public LocalDate getRecyclingDate() {
        return recyclingDate;
    }

    public void setRecyclingDate(LocalDate recyclingDate) {
        this.recyclingDate = recyclingDate;
    }
}
