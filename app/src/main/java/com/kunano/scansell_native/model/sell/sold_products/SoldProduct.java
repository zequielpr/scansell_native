package com.kunano.scansell_native.model.sell.sold_products;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.sell.Receipt;

@Entity(foreignKeys = {@ForeignKey(entity = Product.class,
        parentColumns = "productId",
        childColumns = "productIdFK",
        onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Receipt.class,
                parentColumns = "receiptId",
                childColumns = "receiptIdFK",
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index("productIdFK"), @Index("receiptIdFK")})
public class SoldProduct {

    @PrimaryKey(autoGenerate = true)
    private Integer soldProductId;
    private String productIdFK;
    private String receiptIdFK;
    private Integer itemsQuantity;


    public SoldProduct() {
        super();
    }

    public SoldProduct(String productIdFK, String receiptId) {
        this.productIdFK = productIdFK;
        this.receiptIdFK = receiptId;
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

    public Integer getSoldProductId() {
        return soldProductId;
    }

    public void setSoldProductId(Integer soldProductId) {
        this.soldProductId = soldProductId;
    }
}
