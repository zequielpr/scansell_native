package com.kunano.scansell.model.db.relationship;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.model.Home.product.Product;

import java.util.List;

public class BusinessWithProduct{
    @Embedded
    public Business business;
    @Relation(
            parentColumn = "businessId",
            entityColumn = "businessIdFK"
    )
    public List<Product> productsList;
}