package com.kunano.scansell.ui.sell;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.components.ViewModelListener;
import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.model.sell.Receipt;
import com.kunano.scansell.model.sell.payment.Payment;
import com.kunano.scansell.model.sell.payment.card.Card;
import com.kunano.scansell.model.sell.payment.cash.Cash;
import com.kunano.scansell.model.sell.product_to_sel_draft.ProductToSellDraft;
import com.kunano.scansell.repository.home.BusinessRepository;
import com.kunano.scansell.repository.home.ProductRepository;
import com.kunano.scansell.repository.sell.SellRepository;
import com.kunano.scansell.model.sell.sold_products.SoldProduct;
import com.kunano.scansell.ui.sell.collect_payment_method.CollectPaymentMethodFragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SellViewModel extends AndroidViewModel {
    private MutableLiveData<List<Product>> productToSellMutableLiveData;
    private MutableLiveData<BigDecimal> totalToPay;
    private ProductRepository productRepository;
    private BusinessRepository businessRepository;
    private ExecutorService executorService;
    private LiveData<List<Business>> businessesListLiveData;
    private Long currentBusinessId;
    private SellRepository sellRepository;
    private LiveData<List<Receipt>> liveDataReceipts;
    private LiveData<List<Product>> liveDataSoldProducts;
    private MutableLiveData<Integer> finishButtonStateVisibility;
    private String currentReceiptId;
    private MutableLiveData<Integer> selectedIndexSpinner;

    private double cashTendered;
    private MutableLiveData<BigDecimal> cashDue;
    private MutableLiveData<Integer> cashTenderedAndDueVisibility;
    private MutableLiveData<Boolean> isScanActiveMutableLiveData;

    /**
     * it observes at listProductsToSellLiveData
     **/
    private Observer<List<Product>> observer;
    private LiveData<List<Product>> listProductsToSellLiveData;
    private Integer radioButtonChecked;
    private MutableLiveData<List<Receipt>> mutableLiveDataReceipt;
    private Observer<List<Receipt>> receiptObserver;
    private MutableLiveData<Integer> sellProductsVisibilityMD;
    private MutableLiveData<Integer> createNewBusinessVisibilityMD;
    private DecimalFormat df;
    private MutableLiveData<Integer> emptyReceiptSectionVisibility;
    private MutableLiveData<String> totalItemsSellMutableLIveData;


    public SellViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        businessRepository = new BusinessRepository(application);
        sellRepository = new SellRepository(application);
        productToSellMutableLiveData = new MutableLiveData<>();
        totalItemsSellMutableLIveData = new MutableLiveData<>();
        emptyReceiptSectionVisibility = new MutableLiveData<>();

        businessesListLiveData = businessRepository.getAllBusinesses();
        totalToPay = new MutableLiveData<>(new BigDecimal("0.0"));
        liveDataReceipts = new MutableLiveData<>();
        finishButtonStateVisibility = new MutableLiveData<>(View.GONE);
        selectedIndexSpinner = new MutableLiveData<>(0);
        cashDue = new MutableLiveData<>();
        df = new DecimalFormat("#.##");
        cashTenderedAndDueVisibility = new MutableLiveData<>();
        listProductsToSellLiveData = new MutableLiveData<>();
        mutableLiveDataReceipt = new MutableLiveData<>();
        sellProductsVisibilityMD = new MutableLiveData<>();
        createNewBusinessVisibilityMD = new MutableLiveData<>();
        isScanActiveMutableLiveData = new MutableLiveData<>();

        observer = (List<Product> productsToSellList) -> {

            productToSellMutableLiveData.postValue(productsToSellList);
            totalItemsSellMutableLIveData.postValue(String.valueOf(productsToSellList.size()));


            BigDecimal t = BigDecimal.valueOf(productsToSellList.stream().reduce(0.0, (partialAgeResult, p) -> partialAgeResult + p.getSelling_price(), Double::sum));
            totalToPay.postValue(Utils.formatDecimal(t));
            cashDue.postValue(t.negate());

            System.out.println("Number: " + t);

            finishButtonStateVisibility.postValue(productsToSellList.size() > 0 ? View.VISIBLE : View.GONE);
        };

        receiptObserver = (List<Receipt> receiptList) -> {
            System.out.println("Observing: " + receiptList.size());
            emptyReceiptSectionVisibility.postValue(receiptList.size()>0?View.GONE:View.VISIBLE);
            mutableLiveDataReceipt.postValue(receiptList);
        };

    }

    public void requestProduct(String productId, ViewModelListener<Product> viewModelListener) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> fetchProduct(productId, viewModelListener));
    }

    private void fetchProduct(String productId, ViewModelListener<Product> viewModelListener) {
        productRepository.getProductByIds(productId, currentBusinessId, viewModelListener::result);
    }


    public MutableLiveData<List<Product>> getProductToSellMutableLiveData() {
        return productToSellMutableLiveData;
    }

    public void addProductToSell(Product product, ViewModelListener<Boolean> listener) {
        String draftId = UUID.randomUUID().toString();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime currentDate = LocalDateTime.now();

            ProductToSellDraft productToSellDraft = new ProductToSellDraft(product.getProductId(), currentBusinessId, draftId, currentDate);

            executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                   Long result = sellRepository.insertProductInDraft(productToSellDraft).get();
                   listener.result(result > 0);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


    public void deleteProductToSell(Product product) {

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                sellRepository.removeProductFromDraft(currentBusinessId, product.getProductId());
                ;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void clearProductsToSell() {

        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                sellRepository.clearDraft(currentBusinessId);
                cashTendered = 0.0;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    public MutableLiveData<BigDecimal> getTotalToPay() {
        return totalToPay;
    }


    /**
     * Finish sell and create receipt
     **/
    public void finishSell(CollectPaymentMethodFragment.PaymentInfo paymentInfo, ViewModelListener<Boolean> listenResponse) {
        String generatedReceiptId = UUID.randomUUID().toString();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            LocalDateTime actualDate = LocalDateTime.now();
            BigDecimal totalPay = totalToPay.getValue();
            Receipt receipt = new Receipt(generatedReceiptId, currentBusinessId, "", actualDate, totalPay.doubleValue());
            currentReceiptId = receipt.getReceiptId();//Return the substring of the given id

            executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    Long result = sellRepository.insertReceipt(receipt).get();
                    if (result > 0) {
                        updateProductStock();
                        insertPayment(receipt.getReceiptId()).get();
                        insertPaymentMethod(paymentInfo, receipt.getReceiptId()).get();
                        listenResponse.result(insertSoldProducts(receipt.getReceiptId()).get().size() > 0);
                    } else {
                        listenResponse.result(false);
                    }

                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


    private ListenableFuture<Long> insertPayment(String receiptId) {
        Payment payment = new Payment(receiptId);
        return sellRepository.insertPayment(payment);
    }

    private ListenableFuture<Long> insertPaymentMethod(CollectPaymentMethodFragment.PaymentInfo paymentInfo,
                                                       String receiptId) {

        if (paymentInfo.getMethod() == CollectPaymentMethodFragment.CARD) {
            Card paymentMethod = new Card(receiptId);
            return sellRepository.insertPaymentMethod(paymentMethod);
        } else {
            Cash paymentMethod = new Cash(receiptId, paymentInfo.getCashTendered(), paymentInfo.getCashDue());
            return sellRepository.insertPaymentMethod(paymentMethod);
        }


    }


    private ListenableFuture<List<Long>> insertSoldProducts(String receiptID) {
        List<SoldProduct> soldProductList = new ArrayList<>();
        String soldProductId;
        SoldProduct soldProduct;

        for (Product p : productToSellMutableLiveData.getValue()) {
            soldProductId = UUID.randomUUID().toString();
            soldProduct = new SoldProduct(p.getProductId(), receiptID, p.getBusinessIdFK(), soldProductId);
            soldProductList.add(soldProduct);
        }

        return sellRepository.inertSoldProductsList(soldProductList);
    }


    private void updateProductStock() {

        List<Product> productList = productToSellMutableLiveData.getValue();
        productList.sort((product, p) -> {
            return product.getProductId().compareTo(p.getProductId());
        });

        Product product = productList.get(0);

        Map<String, Integer> productQuantityToSell = new HashMap<>();
        int counter = 0;

        for (Product p : productToSellMutableLiveData.getValue()) {

            if (product.getProductId().equalsIgnoreCase(p.getProductId())) {
                counter++;
                productQuantityToSell.put(p.getProductId(), counter);
            } else {
                counter = 0;
                product = p;
            }
        }

        productQuantityToSell.forEach((pId, stockToDecrease) -> {
            try {
                productRepository.updateProductStock(currentBusinessId, pId, stockToDecrease).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("product: " + pId + "Quantity: " + stockToDecrease);
        });

    }


    public boolean isStockEnough(Product product) {
        int productToSel = productToSellMutableLiveData.getValue().stream().filter(p -> {
            return p.getProductId().equalsIgnoreCase(product.getProductId());
        }).collect(Collectors.toList()).size();

        return productToSel < product.getStock();
    }


    //Receipt and sold products
    public LiveData<List<Receipt>> getReceipts() {
        liveDataReceipts.removeObserver(receiptObserver);
        liveDataReceipts = sellRepository.getReceiptList(currentBusinessId);
        liveDataReceipts.observeForever(receiptObserver);


        return mutableLiveDataReceipt;
    }


    public void searchReceipt(String query) {
        liveDataReceipts.removeObserver(receiptObserver);
        liveDataReceipts = sellRepository.getReceiptList(currentBusinessId, query);
        liveDataReceipts.observeForever(receiptObserver);
    }


    public void deleteReceipt(Receipt receipt) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                sellRepository.deleteReceipt(receipt).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }


    public void setTotalToPay(BigDecimal totalToPay) {
        this.totalToPay.postValue(totalToPay);
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

    /**
     * When a new observer is set the previous one is deleted
     **/
    private void setObserverOnNewBusiness() {
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

    public MutableLiveData<Integer> getFinishButtonStateVisibility() {
        return finishButtonStateVisibility;
    }

    public void setFinishButtonStateVisibility(Integer finishButtonStateVisibility) {
        this.finishButtonStateVisibility.postValue(finishButtonStateVisibility);
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
        this.cashTendered = Double.valueOf(df.format(cashTendered));

        BigDecimal cashDueBigDec = BigDecimal.valueOf(cashTendered).subtract(totalToPay.getValue());
        System.out.println("cash due: " + cashDueBigDec);

        cashDue.postValue(Utils.formatDecimal(cashDueBigDec));
    }

    public MutableLiveData<BigDecimal> getCashDue() {
        return cashDue;
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

    public MutableLiveData<String> getTotalItemsSellMutableLIveData() {
        return totalItemsSellMutableLIveData;
    }

    public void setTotalItemsSellMutableLIveData(String totalItemsSellMutableLIveData) {
        this.totalItemsSellMutableLIveData.postValue(totalItemsSellMutableLIveData);
    }

    public MutableLiveData<Boolean> isScanActiveMutableLiveData() {
        return isScanActiveMutableLiveData;
    }

    public void setIsScanActiveMutableLiveData(Boolean isScanActiveMutableLiveData) {
        this.isScanActiveMutableLiveData.postValue(isScanActiveMutableLiveData);
    }

    public MutableLiveData<Integer> getEmptyReceiptSectionVisibility() {
        return emptyReceiptSectionVisibility;
    }

    public void setEmptyReceiptSectionVisibility(MutableLiveData<Integer> emptyReceiptSectionVisibility) {
        this.emptyReceiptSectionVisibility = emptyReceiptSectionVisibility;
    }
}