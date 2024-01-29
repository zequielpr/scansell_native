package com.kunano.scansell_native.model.db.relationship;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductImg;

public class ProductWithImage {
        @Embedded
        public Product product;
        @Relation(
                parentColumn = "productId",
                entityColumn = "productIdFk"
        )
        public ProductImg productImg;

}
