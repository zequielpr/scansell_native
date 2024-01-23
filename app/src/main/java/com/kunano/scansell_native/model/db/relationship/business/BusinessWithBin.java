package com.kunano.scansell_native.model.db.relationship.business;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.bins.business.BusinessBin;

public class BusinessWithBin {
    @Embedded
    public Business business;

    @Relation(
            parentColumn = "businessId",
            entityColumn = "businessIdFk"
    )
    public BusinessBin businessBin;
}