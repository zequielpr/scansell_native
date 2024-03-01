package com.kunano.scansell_native.ui.sell.receipts.sold_products;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.repository.sell.SellRepository;
import com.kunano.scansell_native.ui.components.ListenResponse;

import java.util.List;
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
        executorService =  Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
               Integer result = sellRepository.deleteSoldProduct(product, receipt.getReceiptId()).get();
               listenResponse.isSuccessfull(result>0);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public LiveData<Receipt> getReceiptByid(Long currentBusinessId, String currentReceiptId){
        return sellRepository.getReceiptById(currentBusinessId, currentReceiptId);
    }


    public LiveData<List<Product>> getSoldProducts(Long currentBusinessId, String currentReceiptId){
        return sellRepository.getSoldProductList(currentReceiptId, currentBusinessId);
    }


}