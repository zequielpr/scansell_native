package com.kunano.scansell_native.model.db.relationship;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;

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