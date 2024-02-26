package com.kunano.scansell_native.repository.home;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.business.BusinessDao;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductDao;
import com.kunano.scansell_native.model.Home.product.ProductImgDao;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BusinessRepository {
    private BusinessDao businessDao;

    public ProductDao getProductDao() {
        return productDao;
    }

    private ProductDao productDao;

    private ProductImgDao productImgDao;
    private LiveData<List<Business>> allBusiness;
    private LiveData<List<BusinessWithProduct>> allProductsWithBusiness;
    private LiveData<Product> allProducts;

    public BusinessRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        businessDao = appDatabase.businessDao();
        productDao = appDatabase.productDao();
        productImgDao = appDatabase.productImgDao();
        allBusiness = businessDao.getAllBusinesses();

    }


    //insert oprations--------------------------------------------------------------
    public void insertBusiness(Business business, ListenResponse response) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Long resultado = null;
            try {

               /*for (int i = 0; i < 5000; i++){
                    resultado = businessDao.insertBusiness(business).get();
                }*/
                resultado = businessDao.insertBusiness(business).get();
                if (resultado > 0) {
                    response.isSuccessfull(true);
                } else {
                    response.isSuccessfull(false);
                }

            } catch (ExecutionException e) {
                response.isSuccessfull(false);
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                response.isSuccessfull(false);
                throw new RuntimeException(e);
            }
        });
    }

    public void insertBusiness(List<Business> business) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            businessDao.insertBusinessList(business);
        });
    }


    public void updateBusiness(Business business, ListenResponse response) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            Integer resultado = null;
            try {

               /*for (int i = 0; i < 5000; i++){
                    resultado = businessDao.insertBusiness(business).get();
                }*/
                resultado = businessDao.updateBusiness(business).get();
                if (resultado > 0) {
                    response.isSuccessfull(true);
                } else {
                    response.isSuccessfull(false);
                }

            } catch (ExecutionException e) {
                response.isSuccessfull(false);
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                response.isSuccessfull(false);
                throw new RuntimeException(e);
            }
        });
    }










    //Delete oprations--------------------------------------------------------------
    public ListenableFuture<Integer> deleteBusiness(Business business) {
        return businessDao.delete(business);
    }
    //Read operations--------------------------------------------------------------
    public LiveData<List<Business>> getAllBusinesses() {
        return allBusiness;
    }
    public LiveData<Business> getBusinesById(Long id){
        return businessDao.getBusinessById(id);
    }

    public LiveData<List<Product>> getProductsList(Long businessId){
        return businessDao.getProducts(businessId);
    }

    public LiveData<List<Product>> searchProducts(Long businessId, String query){
        query =  "%" + query.concat("%");
        return businessDao.searchProducts(businessId, query);
    }

    public LiveData<List<Product>> sortProductByNameAsc(Long businessId){
        return businessDao.sortProductByNameAsc(businessId);
    }

    public LiveData<List<Product>> sortProductByNameDesc(Long businessId){
        return businessDao.sortProductByNameDesc(businessId);
    }

    public LiveData<List<Product>> sortProductByStockAsc(Long businessId){
        return businessDao.sortProductByStockAsc(businessId);
    }
    public LiveData<List<Product>> sortProductByStockDesc(Long businessId){
        return businessDao.sortProductByStockDesc(businessId);
    }


}
