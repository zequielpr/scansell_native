package com.kunano.scansell_native.model.sell.product_to_sel_draft;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.kunano.scansell_native.model.Home.product.Product;

@Entity(primaryKeys = {"businessIdIdFK", "productIdFK", "id"},
        foreignKeys = @ForeignKey(entity = Product.class,
                parentColumns = {"productId", "businessIdFK"},
                childColumns = {"productIdFK", "businessIdIdFK"},
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("productIdFK"), @Index("businessIdIdFK")})
public class ProductToSellDraft {

    @NonNull
    private String id;

    @NonNull
    private String productIdFK;

    @NonNull
    private Long businessIdIdFK;
    public ProductToSellDraft(){
    }

    public ProductToSellDraft(String productIdFK, Long businessIdIdFK, String id) {
        this.id = id;
        this.productIdFK = productIdFK;
        this.businessIdIdFK = businessIdIdFK;
    }




    public String getProductIdFK() {
        return productIdFK;
    }

    public void setProductIdFK(String productIdFK) {
        this.productIdFK = productIdFK;
    }

    public Long getBusinessIdIdFK() {
        return businessIdIdFK;
    }

    public void setBusinessIdIdFK(Long businessIdIdFK) {
        this.businessIdIdFK = businessIdIdFK;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
