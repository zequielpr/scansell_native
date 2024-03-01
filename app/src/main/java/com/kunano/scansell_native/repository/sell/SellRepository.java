package com.kunano.scansell_native.repository.sell;


import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.ReceiptDao;
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraft;
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraftDao;
import com.kunano.scansell_native.model.sell.sold_products.SoldProduct;
import com.kunano.scansell_native.model.sell.sold_products.SoldProductDao;

import java.util.List;

public class SellRepository {
    private ReceiptDao receiptDao;
    private SoldProductDao soldProductDao;
    private ProductToSellDraftDao productToSellDraftDao;

    public SellRepository(Application aplication){
        AppDatabase appDatabase = AppDatabase.getInstance(aplication);
        receiptDao = appDatabase.receiptDao();
        soldProductDao = appDatabase.soldProductDao();
        productToSellDraftDao = appDatabase.productToSellDraftDao();
    }

    public LiveData<List<Receipt>> getReceiptList(Long businessId){
        return receiptDao.getReceipts(businessId, "%%");
    }
    public LiveData<List<Receipt>> getReceiptList(Long businessId, String query){
        query = "%" + query + "%";
        return receiptDao.getReceipts(businessId, query);
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

    public ListenableFuture<Integer> deleteSoldProduct(Product soldProduct, String receiptId){
       return soldProductDao.deleteSoldProduct(soldProduct.getProductId(),
               soldProduct.getBusinessIdFK(), receiptId);
    }

    public LiveData<List<Product>> getSoldProductList(String receiptId, Long businessId){
        return soldProductDao.getSoldProducts(receiptId, businessId);
    }


    //Handle draft______________________

    public ListenableFuture<Long> insertProductInDraft(ProductToSellDraft productToSellDraft){
        return productToSellDraftDao.insertProductInDraft(productToSellDraft);
    }

    public LiveData<List<Product>>getProductToSellDraft(Long businessId){
        return productToSellDraftDao.getProductsInDraft(businessId);
    }


    public void removeProductFromDraft(Long businessId, String productId){
        productToSellDraftDao.deleteProductFromDraft(businessId, productId);
    }



    public void clearDraft(Long businessId){
        productToSellDraftDao.empryDraft(businessId);
    }


}
