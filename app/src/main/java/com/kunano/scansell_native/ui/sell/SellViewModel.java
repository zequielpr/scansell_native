package com.kunano.scansell_native.ui.sell;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.sold_products.SoldProduct;
import com.kunano.scansell_native.repository.home.BusinessRepository;
import com.kunano.scansell_native.repository.home.ProductRepository;
import com.kunano.scansell_native.repository.sell.SellRepository;
import com.kunano.scansell_native.ui.components.ViewModelListener;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
    private SellRepository sellRepository;
    private LiveData<List<Receipt>> liveDataReceipts;
    private LiveData<List<Product>> liveDataSoldProducts;
    private MutableLiveData<Boolean> finishButtonState;
    private String currentReceiptId;
    private MutableLiveData<Integer> selectedIndexSpinner;

    private double cashTendered;
    private MutableLiveData<Double> cashDue;
    private MutableLiveData<Integer> cashTenderedAndDueVisibility;
    Integer radioButtonChecked;
    DecimalFormat df;

    public SellViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        businessRepository = new BusinessRepository(application);
        sellRepository = new SellRepository(application);

        businessesListLiveData = businessRepository.getAllBusinesses();
        productToSellMutableLiveData = new MutableLiveData<>();
        totalToPay = new MutableLiveData<>(0.0);
        productList = new ArrayList<>();
        liveDataReceipts = new MutableLiveData<>();
        finishButtonState = new MutableLiveData<>(false);
        selectedIndexSpinner = new MutableLiveData<>(0);
        cashDue  = new MutableLiveData<>();
        totalToPay.observeForever(v->cashDue.postValue(0-v));
        df = new DecimalFormat("#.##");
        cashTenderedAndDueVisibility = new MutableLiveData<>();
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
        sumPrice(product);
        productList.add(product);
        this.productToSellMutableLiveData.postValue(productList);
        finishButtonState.postValue(true);
    }

    private void sumPrice(Product product){
        Double summedPrice =  Double.
                valueOf(df.format(totalToPay.getValue() + product.getSelling_price()));
        totalToPay.postValue(summedPrice);
    }

    public void deleteProductToSellMutableLiveData(Product product) {
        productList.remove(product);
        decreasePrice(product);
        this.productToSellMutableLiveData.postValue(productList);
        finishButtonState.postValue(productList.size()>0);
    }

    public void clearProductsToSell(){
        productList.clear();
        productToSellMutableLiveData.postValue(productList);
        totalToPay.postValue(0.0);
        finishButtonState.postValue(false);
        cashTendered = 0.0;
        cashDue.postValue(0.0);
    }

    private void decreasePrice(Product product){
        Double decreasedPrice =  Double.
                valueOf(df.format(totalToPay.getValue() - product.getSelling_price()));
        totalToPay.postValue(decreasedPrice);
    }

    public MutableLiveData<Double> getTotalToPay() {
        return totalToPay;
    }



    /**Finish sell and create receipt**/
    public void finishSell(byte paymentMethod){
        String receiptId = UUID.randomUUID().toString();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime actualDate = LocalDateTime.now();
            double totalPay = totalToPay.getValue();
            Receipt receipt = new Receipt (receiptId, currentBusinessId, "", actualDate,
                    totalPay,  paymentMethod);

            executorService = Executors.newSingleThreadExecutor();
            executorService.execute(()->{
                try {
                  Long result =  sellRepository.insertReceipt(receipt).get();
                  if (result > 0)insertSoldProducts(receipt.getReceiptId());
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void insertSoldProducts(String receiptID){
        List<SoldProduct> soldProductList = new ArrayList<>();
        SoldProduct soldProduct = new SoldProduct();

        for (Product p:productList){
            soldProduct = new SoldProduct(p.getProductId(), receiptID);
            soldProductList.add(soldProduct);
        }

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
                List<Long> result;
                result = sellRepository.inertSoldProductsList(soldProductList).get();
                if(result.size() > 0){
                    System.out.println("Sell has been successful");
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }





    public LiveData<List<Receipt>> getReceipts(){
        liveDataReceipts = sellRepository.getReceiptList(currentBusinessId);
        return liveDataReceipts;
    }

    public void deleteReceipt(Receipt receipt){
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
                sellRepository.deleteReceipt(receipt).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public LiveData<List<Product>> getSoldProducts(){
        liveDataSoldProducts = sellRepository.getSoldProductList(currentReceiptId);
        return liveDataSoldProducts;
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

    public String getCurrentReceiptId() {
        return currentReceiptId;
    }

    public void setCurrentReceiptId(String currentReceiptId) {
        this.currentReceiptId = currentReceiptId;
    }

    public MutableLiveData<Boolean> getFinishButtonState() {
        return finishButtonState;
    }

    public void setFinishButtonState(Boolean finishButtonState) {
        this.finishButtonState.postValue(finishButtonState);
    }

    public MutableLiveData<Integer> getSelectedIndexSpinner() {
        return selectedIndexSpinner;
    }

    public void setSelectedIndexSpinner(Integer selectedIndexSpinner) {
        this.selectedIndexSpinner.postValue(selectedIndexSpinner);
    }

    public double getCashTendered() {
        return Double.valueOf(df.format(cashTendered));
    }

    public void setCashTendered(double cashTendered) {
        this.cashTendered = Double.valueOf(df.format(cashTendered));;
        cashDue.postValue(Double.valueOf(df.format((cashTendered-totalToPay.getValue()))));
    }

    public MutableLiveData<Double> getCashDue() {
        return cashDue;
    }

    public void setCashDue(Double cashDue) {
        this.cashDue.postValue(cashDue);
    }

    public MutableLiveData<Integer> getCashTenderedAndDueVisibility() {
        return cashTenderedAndDueVisibility;
    }

    public void setCashTenderedAndDueVisibility(Integer cashTenderedAndDueVisibility) {
        this.cashTenderedAndDueVisibility.postValue(cashTenderedAndDueVisibility);
    }

    public Integer getRadioButtonChecked() {
        return radioButtonChecked;
    }

    public void setRadioButtonChecked(Integer radioButtonChecked) {
        this.radioButtonChecked = radioButtonChecked;
    }
}