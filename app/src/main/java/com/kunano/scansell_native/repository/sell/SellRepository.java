package com.kunano.scansell_native.repository.sell;


import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.sell.Receipt;
import com.kunano.scansell_native.model.sell.ReceiptDao;
import com.kunano.scansell_native.model.sell.payment.Payment;
import com.kunano.scansell_native.model.sell.payment.PaymentDao;
import com.kunano.scansell_native.model.sell.payment.card.Card;
import com.kunano.scansell_native.model.sell.payment.card.CardDao;
import com.kunano.scansell_native.model.sell.payment.cash.Cash;
import com.kunano.scansell_native.model.sell.payment.cash.CashDao;
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraft;
import com.kunano.scansell_native.model.sell.product_to_sel_draft.ProductToSellDraftDao;
import com.kunano.scansell_native.model.sell.sold_products.MostSoldProducts;
import com.kunano.scansell_native.model.sell.sold_products.ProductWithReceiptDate;
import com.kunano.scansell_native.model.sell.sold_products.SoldProduct;
import com.kunano.scansell_native.model.sell.sold_products.SoldProductDao;

import java.time.LocalDateTime;
import java.util.List;

public class SellRepository {
    private ReceiptDao receiptDao;
    private SoldProductDao soldProductDao;
    private ProductToSellDraftDao productToSellDraftDao;
    private PaymentDao paymentDao;
    private CashDao cashDao;
    private CardDao cardDao;

    public SellRepository(Application aplication){
        AppDatabase appDatabase = AppDatabase.getInstance(aplication);
        receiptDao = appDatabase.receiptDao();
        soldProductDao = appDatabase.soldProductDao();
        productToSellDraftDao = appDatabase.productToSellDraftDao();

        paymentDao = appDatabase.paymentDao();
        cardDao = appDatabase.cardDao();
        cashDao = appDatabase.cashDao();
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


    //Get sell info
    public LiveData<List<Receipt>> getCurrentWeekSells(Long businessId, LocalDateTime startOfCurrentWeek){
        return receiptDao.geCurrentWeekSells(businessId, startOfCurrentWeek);
    }
    public LiveData<List<Receipt>> getLastWeekSells(Long businessId, LocalDateTime startOfLastWeek,
                                                    LocalDateTime currentWeekDate){
        return receiptDao.getLastWeekSells(businessId, startOfLastWeek, currentWeekDate);
    }

    public LiveData<List<ProductWithReceiptDate>> geSoldProductInCurrentWeek(Long businessId, LocalDateTime startOfCurrentWeek){
        return soldProductDao.geSoldProductInCurrentWeek(businessId, startOfCurrentWeek);
    }

    public LiveData<List<ProductWithReceiptDate>> geSoldProductInLastWeek(Long businessId, LocalDateTime startOfLastWeek,
                                                                          LocalDateTime currentWeekDate){
        return soldProductDao.geSoldProductInLastWeek(businessId, startOfLastWeek, currentWeekDate);
    }





    //get most sold products
    public LiveData<List<MostSoldProducts>> getMostSoldProductsInCurrentWeek(Long businessId, LocalDateTime startOfCurrentWeek){
        return soldProductDao.getMostSoldProductsInCurrentWeek(businessId, startOfCurrentWeek);
    }

    public LiveData<List<MostSoldProducts>> getMostSoldProductsInLastWeek(Long businessId,
                                                                          LocalDateTime startOfLastWeek,
                                                                          LocalDateTime currentWeekDate){
        return soldProductDao.getMostSoldProductsInLastWeek(businessId, startOfLastWeek, currentWeekDate);
    }




    //Handle payment
    public ListenableFuture<Long> insertPayment(Payment payment){
        return paymentDao.insertPayment(payment);
    }
    public ListenableFuture<Integer> deletePayment(Payment payment){
        return paymentDao.deletePayment(payment);
    }

    public LiveData<Payment> getPayment(String receiptId){
        return paymentDao.getPayment(receiptId);
    }

    //PaymentMethod
    public ListenableFuture<Long> insertPaymentMethod(Card paymentMethod){
        return cardDao.insertPaymentMethod(paymentMethod);
    }
    public ListenableFuture<Integer> deletePaymentMethod(Card paymentMethod){
        return cardDao.deletePaymentMethod(paymentMethod);
    }
    public ListenableFuture<Long> insertPaymentMethod(Cash paymentMethod){
        return cashDao.insertPaymentMethod(paymentMethod);
    }
    public ListenableFuture<Integer> deletePaymentMethod(Cash paymentMethod){
        return cashDao.deletePaymentMethod(paymentMethod);
    }

    public LiveData<Cash> getPaymentMethodByCash(String receiptId){
        return cashDao.getPaymentMethodByCash(receiptId);
    }







}
