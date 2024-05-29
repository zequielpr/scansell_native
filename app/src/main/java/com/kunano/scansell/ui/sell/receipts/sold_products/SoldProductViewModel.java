package com.kunano.scansell.ui.sell.receipts.sold_products;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.kunano.scansell.components.Utils;
import com.kunano.scansell.components.ViewModelListener;
import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.model.sell.Receipt;
import com.kunano.scansell.model.sell.payment.cash.Cash;
import com.kunano.scansell.repository.sell.SellRepository;
import com.kunano.scansell.R;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoldProductViewModel extends AndroidViewModel {

    private SellRepository sellRepository;
    ExecutorService executorService;
    private MutableLiveData<Integer> cashDueAndTenderedVisibility;
    private MutableLiveData<String> paymentMethod;
    private MutableLiveData<String> soldItems;
    private MutableLiveData<String> cashTendered;
    private MutableLiveData<String> cashDue;

    private Observer<Cash> cashObserver;
    private LiveData<Cash> paymentMethodByCash;


    public SoldProductViewModel(@NonNull Application application) {
        super(application);
        sellRepository = new SellRepository(application);

        paymentMethodByCash = new MutableLiveData<>();
        cashDueAndTenderedVisibility = new MutableLiveData<>();
        paymentMethod = new MutableLiveData<>();
        soldItems = new MutableLiveData<>();
        cashTendered = new MutableLiveData<>();
        cashDue = new MutableLiveData<>();

        cashObserver = (Cash c) -> {
            if (c == null) {
                paymentMethod.postValue(getApplication().getString(R.string.card));
                cashDueAndTenderedVisibility.postValue(View.GONE);
            } else {
                paymentMethod.postValue(getApplication().getString(R.string.cash));
                cashDueAndTenderedVisibility.postValue(View.VISIBLE);
                cashTendered.postValue(String.valueOf(Utils.formatDecimal(BigDecimal.valueOf(c.getCashTendered()))));
                cashDue.postValue(String.valueOf(Utils.formatDecimal(BigDecimal.valueOf(c.getCashDue()))));
            }
        };

    }

    public void cancelProductSell(Product product, Receipt receipt, ViewModelListener<Boolean> listener) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Integer result = sellRepository.deleteSoldProduct(product, receipt.getReceiptId()).get();
                listener.result(result > 0);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void populatePaymentInfo(String receiptId) {
        paymentMethodByCash.removeObserver(cashObserver);
        paymentMethodByCash = sellRepository.getPaymentMethodByCash(receiptId);
        paymentMethodByCash.observeForever(cashObserver);
    }


    public LiveData<Receipt> getReceiptByid(Long currentBusinessId, String currentReceiptId) {
        return sellRepository.getReceiptById(currentBusinessId, currentReceiptId);
    }


    public LiveData<List<Product>> getSoldProducts(Long currentBusinessId, String currentReceiptId) {
        return sellRepository.getSoldProductList(currentReceiptId, currentBusinessId);
    }


    public MutableLiveData<Integer> getCashDueAndTenderedVisibility() {
        return cashDueAndTenderedVisibility;
    }

    public void setCashDueAndTenderedVisibility(Integer cashDueAndTenderedVisibility) {
        this.cashDueAndTenderedVisibility.postValue(cashDueAndTenderedVisibility);
    }

    public MutableLiveData<String> getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.postValue(paymentMethod);
    }

    public MutableLiveData<String> getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(String soldItems) {
        this.soldItems.postValue(soldItems);
    }

    public MutableLiveData<String> getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(String cashTendered) {
        this.cashTendered.postValue(cashTendered);
    }

    public MutableLiveData<String> getCashDue() {
        return cashDue;
    }

    public void setCashDue(String cashDue) {
        this.cashDue.postValue(cashDue);
    }
}