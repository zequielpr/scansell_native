package com.kunano.scansell_native.model.sell.sold_products;

import androidx.room.Embedded;

import com.kunano.scansell_native.model.Home.product.Product;

import java.time.LocalDateTime;

public class ProductWithReceiptDate {
    @Embedded
    private Product product;

    private LocalDateTime receiptDate;

    public ProductWithReceiptDate(Product product, LocalDateTime receiptDate) {
        this.product = product;
        this.receiptDate = receiptDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(LocalDateTime receiptDate) {
        this.receiptDate = receiptDate;
    }
}
