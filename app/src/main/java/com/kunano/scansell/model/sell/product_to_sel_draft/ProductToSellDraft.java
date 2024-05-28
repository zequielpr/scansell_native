package com.kunano.scansell.model.sell.product_to_sel_draft;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.kunano.scansell.model.Home.product.Product;

import java.time.LocalDateTime;

@Entity(primaryKeys = {"businessIdIdFK", "productIdFK", "id"},
        foreignKeys = @ForeignKey(entity = Product.class,
                parentColumns = {"productId", "businessIdFK"},
                childColumns = {"productIdFK", "businessIdIdFK"},
                onDelete = ForeignKey.CASCADE),
        indices = {@Index({"productIdFK", "businessIdIdFK"})})
public class ProductToSellDraft {

    @NonNull
    private String id;

    @NonNull
    private String productIdFK;

    @NonNull
    private Long businessIdIdFK;

    @NonNull
    LocalDateTime addedDate;

    @Ignore
    public ProductToSellDraft(){
    }

    public ProductToSellDraft(String productIdFK, Long businessIdIdFK, String id, LocalDateTime addedDate ) {
        this.id = id;
        this.productIdFK = productIdFK;
        this.businessIdIdFK = businessIdIdFK;
        this.addedDate = addedDate;
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

    @NonNull
    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(@NonNull LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
}
