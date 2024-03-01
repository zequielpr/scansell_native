package com.kunano.scansell_native.ui.sell.receipts.sold_products;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.sold_products.SoldProduct;
import com.kunano.scansell_native.repository.sell.SellRepository;
import com.kunano.scansell_native.ui.components.ListenResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoldProductViewModel extends AndroidViewModel {

    private SellRepository sellRepository;
    ExecutorService executorService;

    public SoldProductViewModel(@NonNull Application application) {
        super(application);
        sellRepository = new SellRepository(application);
    }

    public void cancelProductSell(Product product, Receipt receipt, ListenResponse listenResponse){
        SoldProduct soldProduct = new SoldProduct(product.getProductId(), receipt.getReceiptId(), product.getBusinessIdFK());
        executorService =  Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
               Integer result = sellRepository.deleteSoldProduct(soldProduct).get();
               listenResponse.isSuccessfull(result>0);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

}