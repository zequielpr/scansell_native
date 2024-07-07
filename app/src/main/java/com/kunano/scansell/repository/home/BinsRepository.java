package com.kunano.scansell.repository.home;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.model.bins.business.BusinessBin;
import com.kunano.scansell.model.bins.business.BusinessBinDao;
import com.kunano.scansell.model.bins.user.UserBin;
import com.kunano.scansell.model.bins.user.UserBinDao;
import com.kunano.scansell.model.db.AppDatabase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BinsRepository {

    private UserBinDao userBinDao;
    private BusinessBinDao businessBinDao;
    public BinsRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        userBinDao = appDatabase.userBinDao();
        businessBinDao = appDatabase.businessBinDao();

    }



    public ListenableFuture<Long> sendBusinessTobin(long businessId){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime actualDate = Utils.getCurrentDate(Utils.YYYY_MM_DD_HH_MM_SS);
            return userBinDao.sendBusinessToBin(new UserBin(businessId,actualDate ));
        }

        return null;

    }

    public LiveData<List<Business>> getBusinessInBin(){
        return userBinDao.getBusinessInBin();
    }

    public ListenableFuture<Integer> restorageBusiness(Long businessId){
        return userBinDao.restorageBusiness(businessId);
    }


    public ListenableFuture<LocalDate> getRecycleDate(Long businessId){
       return userBinDao.getRecycleDate(businessId);
    }




    //admin business bin---------------------------
    public ListenableFuture<Long> sendProductTobin(long businessIdFK, String productIdFk){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime localDateTime = Utils.getCurrentDate(Utils.YYYY_MM_DD_HH_MM_SS);
            return businessBinDao.senProductToBin(new BusinessBin(businessIdFK, productIdFk, localDateTime));
        }

        return null;

    }

    public LiveData<List<Product>> getProductInBin(long businessIdFK){
        return businessBinDao.getProductsInBin(businessIdFK);
    }

    public ListenableFuture<Integer> restorageProducts(String productId, Long businessId){
        return businessBinDao.restorageProduct(productId, businessId);
    }


    public ListenableFuture<LocalDate> getRecycleDate(String productId){
        return businessBinDao.getRecycleDate(productId);
    }


}
