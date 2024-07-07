package com.kunano.scansell.model.bins.user;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell.model.Home.business.Business;

import java.time.LocalDateTime;

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
    LocalDateTime recyclingDate;


    @Ignore
    public UserBin(){
        super();
    }

    public UserBin(long businessIdFk, LocalDateTime recyclingDate) {
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

    public LocalDateTime getRecyclingDate() {
        return recyclingDate;
    }

    public void setRecyclingDate(LocalDateTime recyclingDate) {
        this.recyclingDate = recyclingDate;
    }
}
