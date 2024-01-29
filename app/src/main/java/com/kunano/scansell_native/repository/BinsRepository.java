package com.kunano.scansell_native.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.bins.user.UserBin;
import com.kunano.scansell_native.model.bins.user.UserBinDao;
import com.kunano.scansell_native.model.db.AppDatabase;

import java.util.List;

public class BinsRepository {

    private UserBinDao userBinDao;
    public BinsRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        userBinDao = appDatabase.userBinDao();

    }



    public ListenableFuture<Long> sendBusinessTobin(long businessId){
        return userBinDao.sendBusinessToBin(new UserBin(businessId));
    }

    public LiveData<List<Business>> getBusinessInBin(){
        return userBinDao.getBusinessInBin();
    }

    public ListenableFuture<Integer> restorageBusiness(Long businessId){
        return userBinDao.restorageBusiness(businessId);
    }
}
