package com.kunano.scansell_native.model.db.relationships;


import androidx.room.Embedded;
import androidx.room.Relation;

import com.kunano.scansell_native.model.db.Business;
import com.kunano.scansell_native.model.db.Product;

import java.util.List;

public class BusinessWithProduct{
    @Embedded
    public Business business;
    @Relation(
            parentColumn = "businessId",
            entityColumn = "createdInbusinessId"
    )
    public List<Product> productsList;
}