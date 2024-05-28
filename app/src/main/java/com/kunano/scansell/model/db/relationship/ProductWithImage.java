package com.kunano.scansell.model.db.relationship;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.model.Home.product.ProductImg;

public class ProductWithImage {
        @Embedded
        public Product product;
        @Relation(
                parentColumn = "productId",
                entityColumn = "productIdFk"
        )
        public ProductImg productImg;

}
