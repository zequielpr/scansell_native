package com.kunano.scansell.model.db.relationship.business;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.model.bins.user.UserBin;

public class UserWithBin {
    @Embedded
    public Business business;

    @Relation(
            parentColumn = "businessId",
            entityColumn = "businessIdFk"
    )
    public UserBin userBin;
}