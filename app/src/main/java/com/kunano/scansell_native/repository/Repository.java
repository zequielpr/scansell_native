package com.kunano.scansell_native.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.business.BusinessDao;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductDao;
import com.kunano.scansell_native.model.Home.product.ProductImg;
import com.kunano.scansell_native.model.Home.product.ProductImgDao;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.model.db.relationship.ProductWithImage;
import com.kunano.scansell_native.ui.home.business.ProductCardAdapter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Repository {
    private BusinessDao businessDao;

    public ProductDao getProductDao() {
        return productDao;
    }

    private ProductDao productDao;

    private ProductImgDao productImgDao;
    private LiveData<List<Business>> allBusiness;
    private LiveData<List<BusinessWithProduct>> allProductsWithBusiness;
    private LiveData<Product> allProducts;

    public Repository(Application application) {
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

              /* for (int i = 0; i < 5000; i++){
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


    public void insertProduct(Product product, byte[] productImg, ListenResponse response) {
        Executor executor = Executors.newSingleThreadExecutor();


        executor.execute(() -> {
            Long resultado = null;
            try {
                /*for (int i = 0; i < 2000; i++){

                }*/
                product.setProductId(UUID.randomUUID().toString());
                resultado = productDao.insertProduct(product).get();
                ProductImg img = new ProductImg(product.getProductId(), productImg);
                productImgDao.insertProductImg(img).get();
                //resultado = productDao.insertProduct(product).get();
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

    public void insertProduct(List<Product> product) {

    }

    //Delete oprations--------------------------------------------------------------
    public ListenableFuture<Integer> deleteBusiness(Business business) {
        return businessDao.delete(business);
    }

    public ListenableFuture<Integer> deleteProduct(Product product) {
        return productDao.deleteProduct(product);
    }

    //Read operations--------------------------------------------------------------
    public LiveData<List<Business>> getAllBusinesses() {
        return allBusiness;
    }
    public LiveData<Business> getBusinesById(Long id){
        return businessDao.getBusinessById(id);
    }

    public LiveData<List<BusinessWithProduct>> getAllBusinessWithProduct() {
        return allProductsWithBusiness;
    }

    public LiveData<BusinessWithProduct> getProductsList(Long businessId){
        return businessDao.getBusinessWithProduct(businessId);
    }


    public void getProdductImage(String productId,
                                 ProductCardAdapter.LisnedProductImage lisnedProductImage){
        Executor executor = Executors.newSingleThreadExecutor();


        executor.execute(() -> {
            try {
                ProductWithImage productWithImage = productImgDao.getBusinessWithProduct(productId).get();
                lisnedProductImage.recieveProducImage(productWithImage);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ;
        });
    }


}
