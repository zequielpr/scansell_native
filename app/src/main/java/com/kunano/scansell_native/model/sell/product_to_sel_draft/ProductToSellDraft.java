package com.kunano.scansell_native.model.sell.product_to_sel_draft;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;

@Entity(foreignKeys = {@ForeignKey(entity = Product.class,
        parentColumns = "productId",
        childColumns = "productIdFK",
        onDelete = ForeignKey.CASCADE),

        @ForeignKey(entity = Business.class,
                parentColumns = "businessId",
                childColumns = "businessIdIdFK",
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index("productIdFK"), @Index("businessIdIdFK")})
public class ProductToSellDraft {

    @PrimaryKey(autoGenerate = true)
    private int draftId;

    private String productIdFK;
    private Long businessIdIdFK;

    public ProductToSellDraft(String productIdFK, Long businessIdIdFK) {
        this.productIdFK = productIdFK;
        this.businessIdIdFK = businessIdIdFK;
    }

    public int getDraftId() {
        return draftId;
    }

    public void setDraftId(int draftId) {
        this.draftId = draftId;
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
}
