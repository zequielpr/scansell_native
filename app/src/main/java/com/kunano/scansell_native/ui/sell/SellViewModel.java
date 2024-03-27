package com.kunano.scansell_native.ui.sell;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraft;
import com.kunano.scansell_native.model.sell.sold_products.SoldProduct;
import com.kunano.scansell_native.repository.home.BusinessRepository;
import com.kunano.scansell_native.repository.home.ProductRepository;
import com.kunano.scansell_native.repository.sell.SellRepository;
import com.kunano.scansell_native.ui.components.ListenResponse;
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
    private SellRepository sellRepository;
    private LiveData<List<Receipt>> liveDataReceipts;
    private LiveData<List<Product>> liveDataSoldProducts;
    private MutableLiveData<Boolean> finishButtonState;
    private String currentReceiptId;
    private MutableLiveData<Integer> selectedIndexSpinner;

    private double cashTendered;
    private MutableLiveData<Double> cashDue;
    private MutableLiveData<Integer> cashTenderedAndDueVisibility;

    /** it observes at listProductsToSellLiveData **/
    private Observer<List<Product>> observer;
    private LiveData<List<Product>> listProductsToSellLiveData;
    private Integer radioButtonChecked;
    private MutableLiveData<List<Receipt>> mutableLiveDataReceipt;
    private Observer<List<Receipt>> receiptObserver;
    private MutableLiveData<Integer> sellProductsVisibilityMD;
    private MutableLiveData<Integer> createNewBusinessVisibilityMD;
    private DecimalFormat df;



    public SellViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        businessRepository = new BusinessRepository(application);
        sellRepository = new SellRepository(application);
        productToSellMutableLiveData = new MutableLiveData<>();

        businessesListLiveData = businessRepository.getAllBusinesses();
        totalToPay = new MutableLiveData<>(0.0);
        liveDataReceipts = new MutableLiveData<>();
        finishButtonState = new MutableLiveData<>(false);
        selectedIndexSpinner = new MutableLiveData<>(0);
        cashDue  = new MutableLiveData<>();
        totalToPay.observeForever(v->cashDue.postValue(0-v));
        df = new DecimalFormat("#.##");
        cashTenderedAndDueVisibility = new MutableLiveData<>();
        listProductsToSellLiveData = new MutableLiveData<>();
        mutableLiveDataReceipt = new MutableLiveData<>();
        sellProductsVisibilityMD = new MutableLiveData<>();
        createNewBusinessVisibilityMD = new MutableLiveData<>();

        observer = ( List<Product> productsToSellList) -> {

            productToSellMutableLiveData.postValue(productsToSellList);

            Double t =  productsToSellList.stream().reduce(0.0, (partialAgeResult, p) ->
                    partialAgeResult + p.getSelling_price(), Double::sum);
            totalToPay.postValue(t);
            finishButtonState.postValue(productsToSellList.size()>0);
        };

        receiptObserver = (List<Receipt> receiptList)->{
            System.out.println("Observing: " + receiptList.size());
            mutableLiveDataReceipt.postValue(receiptList);
        };

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

    public void addProductToSell(Product product) {
        String draftId = UUID.randomUUID().toString();

        ProductToSellDraft productToSellDraft = new ProductToSellDraft(product.getProductId(), currentBusinessId,
                draftId);

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
                Long result = sellRepository.insertProductInDraft(productToSellDraft).get();
                if (result>0){
                    //sumPrice(product);
                    finishButtonState.postValue(true);
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });



    }


    public void deleteProductToSell(Product product) {

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
                sellRepository.removeProductFromDraft(currentBusinessId, product.getProductId());;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void clearProductsToSell(){

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()->{
            try {
                sellRepository.clearDraft(currentBusinessId);
                cashTendered = 0.0;
                cashDue.postValue(0.0);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    public MutableLiveData<Double> getTotalToPay() {
        return totalToPay;
    }



    /**Finish sell and create receipt**/
    public void finishSell(byte paymentMethod, ListenResponse listenResponse){
        String generatedReceiptId = UUID.randomUUID().toString();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            LocalDateTime actualDate = LocalDateTime.now();
            double totalPay = totalToPay.getValue();
            Receipt receipt = new Receipt (generatedReceiptId, currentBusinessId, "", actualDate,
                    totalPay,  paymentMethod);
            currentReceiptId = receipt.getReceiptId();//Return the substring of the given id

            executorService = Executors.newSingleThreadExecutor();
            executorService.execute(()->{
                try {
                  Long result =  sellRepository.insertReceipt(receipt).get();
                  if (result > 0)listenResponse.isSuccessfull(insertSoldProducts(receipt.getReceiptId()).get().size()>0);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private ListenableFuture<List<Long>> insertSoldProducts(String receiptID){
        List<SoldProduct> soldProductList = new ArrayList<>();
        String soldProductId;
        SoldProduct soldProduct;

        for (Product p:productToSellMutableLiveData.getValue()){
            soldProductId = UUID.randomUUID().toString();
            soldProduct = new SoldProduct(p.getProductId(), receiptID, p.getBusinessIdFK(), soldProductId);
            soldProductList.add(soldProduct);
        }

        return sellRepository.inertSoldProductsList(soldProductList);
    }

    //Receipt and sold products
    public LiveData<List<Receipt>> getReceipts(){
        liveDataReceipts.removeObserver(receiptObserver);
        liveDataReceipts = sellRepository.getReceiptList(currentBusinessId);
        liveDataReceipts.observeForever(receiptObserver);


        return mutableLiveDataReceipt;
    }


    public void searchReceipt(String query){
        liveDataReceipts.removeObserver(receiptObserver);
        liveDataReceipts = sellRepository.getReceiptList(currentBusinessId, query);
        liveDataReceipts.observeForever(receiptObserver);
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
        setObserverOnNewBusiness();
    }

    /**When a new observer is set the previous one is deleted**/
    private void setObserverOnNewBusiness(){
        listProductsToSellLiveData.removeObserver(observer);
        listProductsToSellLiveData = sellRepository.getProductToSellDraft(currentBusinessId);
        listProductsToSellLiveData.observeForever(observer);
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

    public MutableLiveData<Integer> getSellProductsVisibilityMD() {
        return sellProductsVisibilityMD;
    }

    public void setSellProductsVisibilityMD(Integer sellProductsVisibilityMD) {
        this.sellProductsVisibilityMD.postValue(sellProductsVisibilityMD);
    }

    public MutableLiveData<Integer> getCreateNewBusinessVisibilityMD() {
        return createNewBusinessVisibilityMD;
    }

    public void setCreateNewBusinessVisibilityMD(Integer createNewBusinessVisibilityMD) {
        this.createNewBusinessVisibilityMD.postValue(createNewBusinessVisibilityMD);
    }
}