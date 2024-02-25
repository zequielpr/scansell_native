package com.kunano.scansell_native.repository.sell;


import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.ReceiptDao;
import com.kunano.scansell_native.model.sell.sold_products.SoldProduct;
import com.kunano.scansell_native.model.sell.sold_products.SoldProductDao;

import java.util.List;

public class SellRepository {
    private ReceiptDao receiptDao;
    private SoldProductDao soldProductDao;

    public SellRepository(Application aplication){
        AppDatabase appDatabase = AppDatabase.getInstance(aplication);
        receiptDao = appDatabase.receiptDao();
        soldProductDao = appDatabase.soldProductDao();
    }

    public LiveData<List<Receipt>> getReceiptList(Long businessId){
        return receiptDao.getReceipts(businessId);
    }

    public LiveData<Receipt> getReceiptById(Long businessId, String receiptId){
        return receiptDao.getReceiptById(businessId, receiptId);
    }

    public ListenableFuture<Long> insertReceipt(Receipt receipt){
        return receiptDao.insertReceipt(receipt);
    }

    public ListenableFuture<Integer>deleteReceipt(Receipt receipt){
        return receiptDao.deleteReceipt(receipt);
    }


    //sold products______________________________________________

    public ListenableFuture<List<Long>> inertSoldProductsList(List<SoldProduct> soldProductList){
        return soldProductDao.insertSoldProductList(soldProductList);
    }
    public  ListenableFuture<Long> insertSoldProduct(SoldProduct soldProduct){
        return soldProductDao.insertSoldProduct(soldProduct);
    }

    public ListenableFuture<Integer> deleteSoldProduct(SoldProduct soldProduct){
       return soldProductDao.deleteSoldProduct(soldProduct);
    }

    public LiveData<List<Product>> getSoldProductList(String receiptId){
        return soldProductDao.getSoldProducts(receiptId);
    }
    //
}
