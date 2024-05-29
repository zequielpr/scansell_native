package com.kunano.scansell.model.sell.sold_products;

import androidx.room.Ignore;

public class MostSoldProducts {
    private String productName;
    private int soldQuantity;
    private int soldProductsTotal;

    @Ignore
    public MostSoldProducts() {
    }

    public MostSoldProducts(String productName, int soldQuantity, int soldProductsTotal) {
        this.productName = productName;
        this.soldQuantity = soldQuantity;
        this.soldProductsTotal = soldProductsTotal;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public int getSoldProductsTotal() {
        return soldProductsTotal;
    }

    public void setSoldProductsTotal(int soldProductsTotal) {
        this.soldProductsTotal = soldProductsTotal;
    }
}
