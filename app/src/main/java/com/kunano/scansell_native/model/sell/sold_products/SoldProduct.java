package com.kunano.scansell_native.model.sell.sold_products;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.kunano.scansell_native.model.Home.product.Product;

@Entity( primaryKeys = {"productIdFK", "businessIdFK", "receiptIdFK"},
        foreignKeys = @ForeignKey(entity = Product.class,
                parentColumns = {"productId", "businessIdFK"},
                childColumns = {"productIdFK", "businessIdFK"},
                onDelete = ForeignKey.CASCADE))
public class SoldProduct {

    @NonNull
    private String productIdFK;

    @NonNull
    private Long businessIdFK;

    @NonNull
    private String receiptIdFK;
    private Integer itemsQuantity;


    public SoldProduct() {
        super();
    }

    public SoldProduct(String productIdFK, String receiptId, Long businessIdFK) {
        this.productIdFK = productIdFK;
        this.receiptIdFK = receiptId;
        this.businessIdFK = businessIdFK;
    }



    public String getProductIdFK() {
        return productIdFK;
    }

    public void setProductIdFK(String productIdFK) {
        this.productIdFK = productIdFK;
    }

    public String getReceiptIdFK() {
        return receiptIdFK;
    }

    public void setReceiptIdFK(String receiptIdFK) {
        this.receiptIdFK = receiptIdFK;
    }

    public Integer getItemsQuantity() {
        return itemsQuantity;
    }

    public void setItemsQuantity(Integer itemsQuantity) {
        this.itemsQuantity = itemsQuantity;
    }

    @NonNull
    public Long getBusinessIdFK() {
        return businessIdFK;
    }

    public void setBusinessIdFK(@NonNull Long businessIdFK) {
        this.businessIdFK = businessIdFK;
    }
}
