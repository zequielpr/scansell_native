package com.kunano.scansell_native.model.sell.sold_products;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.sell.Receipt;

@Entity( primaryKeys = {"productIdFK", "businessIdFK", "tableId"},
        foreignKeys = {@ForeignKey(entity = Product.class,
                parentColumns = {"productId", "businessIdFK"},
                childColumns = {"productIdFK", "businessIdFK"},
                onDelete = ForeignKey.CASCADE),
                    @ForeignKey(entity = Receipt.class,
                    parentColumns = "receiptId",
                    childColumns = "receiptIdFK",
                    onDelete = ForeignKey.CASCADE),})
public class SoldProduct {


    @NonNull
    String tableId;

    @NonNull
    private String productIdFK;

    @NonNull
    private Long businessIdFK;

    @NonNull
    private String receiptIdFK;
    private Integer itemsQuantity;


    public SoldProduct() {

    }

    public SoldProduct(String productIdFK, String receiptId, Long businessIdFK, String tableId) {
        this.productIdFK = productIdFK;
        this.receiptIdFK = receiptId;
        this.businessIdFK = businessIdFK;
        this.tableId = tableId;
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

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
