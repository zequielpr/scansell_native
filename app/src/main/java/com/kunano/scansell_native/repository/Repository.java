package com.kunano.scansell_native.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.business.BusinessDao;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductDao;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.ListenResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository {
    private BusinessDao businessDao;
    private ProductDao productDao;
    private LiveData<List<Business>> allBusiness;
    private LiveData<List<BusinessWithProduct>> allProducts;

    public Repository(Application application){
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        businessDao = appDatabase.businessDao();
        productDao = appDatabase.productDao();
        allBusiness = businessDao.getAllBusinesses();
        allProducts = productDao.getBusinessWithProduct();
    }


    //insert oprations--------------------------------------------------------------
    public void insertBusiness(Business business, ListenResponse response){
        InsertBusinessAsyncTask insertBusinessAsyncTask = new InsertBusinessAsyncTask(businessDao, business, response);
        insertBusinessAsyncTask.start();
    }

    public void insertBusiness(List<Business> business){
        InsertBusinessListAsyncTask insertBusinessAsyncTask = new InsertBusinessListAsyncTask(businessDao, business);

        insertBusinessAsyncTask.start();
    }


    public void insertProduct(Product product){

    }
    public void insertProduct(List<Product> product){

    }

    //Delete oprations--------------------------------------------------------------
    public void deleteBusiness(Business business){

    }
    public void deleteProduct(Product product){

    }

    //Read operations--------------------------------------------------------------
    public LiveData<List<Business>> getAllBusinesses(){
        return allBusiness;
    }

    public LiveData<List<BusinessWithProduct>> getAllproducts(){
        return allProducts;
    }


    //InsertAsync insert business task--------------------------------------------------------------
    private static  class InsertBusinessAsyncTask extends Thread implements Runnable{
        private BusinessDao businessDao;
        private Business business;
        ListenResponse replay;
        Long resultado;

        final Object LOCK = new Object();
        public InsertBusinessAsyncTask(BusinessDao businessDao, Business business, ListenResponse replay) {
            this.businessDao = businessDao;
            this.business = business;
            this.replay = replay;
        }

        @Override
        public void run() {
            synchronized (LOCK){
                try {
                    sleep(3000);
                    resultado = businessDao.insertBusiness(business).get();
                    if(resultado >0){
                        replay.isSuccessfull(true);
                    }else {
                        replay.isSuccessfull(false);
                    }

                } catch (ExecutionException e) {
                    replay.isSuccessfull(false);
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    replay.isSuccessfull(false);
                    throw new RuntimeException(e);
                }
            }

        }
    }



    private static  class InsertBusinessListAsyncTask extends Thread implements Runnable{
        private BusinessDao businessDao;
        private List<Business> business;

        final Object LOCK = new Object();
        public InsertBusinessListAsyncTask(BusinessDao businessDao, List<Business> business) {
            this.businessDao = businessDao;
            this.business = business;
        }

        @Override
        public void run() {
            synchronized (LOCK){
                businessDao.insertBusinessList(business);
            }

        }
    }
}
