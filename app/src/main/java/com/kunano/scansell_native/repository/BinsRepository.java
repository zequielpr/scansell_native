package com.kunano.scansell_native.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.bins.business.BusinessBin;
import com.kunano.scansell_native.model.bins.business.BusinessBinDao;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.db.relationship.business.BusinessWithBin;

import java.util.List;

public class BinsRepository {

    private BusinessBinDao businessBinDao;
    public BinsRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        businessBinDao = appDatabase.businessBinDao();

    }



    public ListenableFuture<Long> sendBusinessTobin(long businessId){
        return businessBinDao.sendBusinessToBin(new BusinessBin(businessId));
    }


    public LiveData<List<BusinessWithBin>> getBusinessWithBin(){
        return businessBinDao.getBusinessInBin();
    }


    public ListenableFuture<Integer> restorageBusiness(Long businessId){
        return businessBinDao.restorageBusiness(businessId);
    }


    public LiveData<BusinessBin> checkIfBisinessInBin(Long businessId){
        return businessBinDao.chechIdBusinessInBin(businessId);
    }
}
