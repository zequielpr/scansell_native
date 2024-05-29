package com.kunano.scansell.model.sell.sold_products;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.model.sell.Receipt;

@Entity( primaryKeys = {"tableId", "productIdFK", "businessIdFK", "receiptIdFK"},
        foreignKeys = {@ForeignKey(entity = Product.class,
                parentColumns = {"productId", "businessIdFK"},
                childColumns = {"productIdFK", "businessIdFK"},
                onDelete = ForeignKey.CASCADE),

                    @ForeignKey(entity = Receipt.class,
                    parentColumns = "receiptId",
                    childColumns = "receiptIdFK",
                    onDelete = ForeignKey.CASCADE),},
indices = {@Index(value = {"receiptIdFK", "productIdFK", "businessIdFK", "tableId"}, unique = true)})
public class SoldProduct {


    @NonNull
    private String tableId;

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
