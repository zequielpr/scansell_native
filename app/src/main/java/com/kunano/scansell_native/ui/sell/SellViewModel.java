package com.kunano.scansell_native.ui.sell;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.repository.BusinessRepository;
import com.kunano.scansell_native.repository.ProductRepository;
import com.kunano.scansell_native.ui.components.ViewModelListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SellViewModel extends AndroidViewModel {
    private MutableLiveData<List<Product>> productToSellMutableLiveData;
    private MutableLiveData<Double> totalToPay;
    private ProductRepository productRepository;
    private BusinessRepository businessRepository;
    private ExecutorService executorService;
    private LiveData<List<Business>> businessesListLiveData;
    private Long currentBusinessId;
    private List<Product> productList;

    public SellViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        businessRepository = new BusinessRepository(application);
        businessesListLiveData = businessRepository.getAllBusinesses();
        productToSellMutableLiveData = new MutableLiveData<>();
        totalToPay = new MutableLiveData<>(0.0);
        productList = new ArrayList<>();
    }

    public void requestProduct(String productId, ViewModelListener viewModelListener){
        executorService = Executors.newSingleThreadExecutor();

        executorService.execute(()->fetchProduct(productId, viewModelListener));
    }

    private void fetchProduct(String productId, ViewModelListener viewModelListener){
        productRepository.getProductByIds(productId, currentBusinessId,
                (p)->receiveProduct(p, viewModelListener));
    }

    private void receiveProduct(Object product, ViewModelListener viewModelListener){

        if (product == null){
            viewModelListener.result(null);
            return;
        }

        Product p = (Product) product;
        viewModelListener.result(p);
    }





    public MutableLiveData<List<Product>> getProductToSellMutableLiveData() {
        return productToSellMutableLiveData;
    }

    public void addProductToSellMutableLiveData(Product product) {
        productList.add(product);
        this.productToSellMutableLiveData.postValue(productList);
    }
    public void deleteProductToSellMutableLiveData(Product product) {
        productList.remove(product);
        this.productToSellMutableLiveData.postValue(productList);
    }

    public MutableLiveData<Double> getTotalToPay() {
        return totalToPay;
    }

    public void setTotalToPay(MutableLiveData<Double> totalToPay) {
        this.totalToPay = totalToPay;
    }


    public LiveData<List<Business>> getBusinessesListLiveData() {
        return businessesListLiveData;
    }

    public void setBusinessesListLiveData(LiveData<List<Business>> businessesListLiveData) {
        this.businessesListLiveData = businessesListLiveData;
    }

    public Long getCurrentBusinessId() {
        return currentBusinessId;
    }

    public void setCurrentBusinessId(Long currentBusinessId) {
        this.currentBusinessId = currentBusinessId;
    }
}