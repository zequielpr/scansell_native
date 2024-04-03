package com.kunano.scansell_native.repository.home;

import android.app.Application;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductDao;
import com.kunano.scansell_native.model.Home.product.ProductImg;
import com.kunano.scansell_native.model.Home.product.ProductImgDao;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.home.business.ProductCardAdapter;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {
    private ProductDao productDao;

    private ProductImgDao productImgDao;


    public ProductRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        productDao = appDatabase.productDao();
        productImgDao = appDatabase.productImgDao();
    }


    public void insertProduct(Product product, byte[] productImg, ListenResponse response) {
        Executor executor = Executors.newSingleThreadExecutor();


        executor.execute(() -> {
            Long resultado = null;
            try {
                /*for (int i = 0; i < 600; i++){
                    product.setProductId(UUID.randomUUID().toString());
                    resultado = productDao.insertProduct(product).get();
                    ProductImg img = new ProductImg(product.getProductId(), productImg, product.getBusinessIdFK());
                    productImgDao.insertProductImg(img).get();
                }*/

                //product.setProductId(UUID.randomUUID().toString());
                resultado = productDao.insertProduct(product).get();


                if (resultado > 0) {
                    ProductImg img = new ProductImg(product.getProductId(), productImg, product.getBusinessIdFK());
                    productImgDao.insertProductImg(img).get();
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

    public void updateProduct(Product product, byte[] productImg, ListenResponse response) {
        Executor executor = Executors.newSingleThreadExecutor();


        executor.execute(() -> {
            Integer resultado = null;
            try {
                /*for (int i = 0; i < 600; i++){
                    product.setProductId(UUID.randomUUID().toString());
                    resultado = productDao.insertProduct(product).get();
                    ProductImg img = new ProductImg(product.getProductId(), productImg, product.getBusinessIdFK());
                    productImgDao.insertProductImg(img).get();
                }*/

                //product.setProductId(UUID.randomUUID().toString());
                resultado = productDao.updateProduct(product).get();


                if (resultado > 0) {
                    ProductImg img = new ProductImg(product.getProductId(), productImg, product.getBusinessIdFK());
                    productImgDao.insertProductImg(img).get();
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


    public ListenableFuture<Integer> deleteProduct(Product product) {
        return productDao.deleteProduct(product);
    }


    public void getProdductImage(String productId, Long businessId,
                                 ProductCardAdapter.LisnedProductImage lisnedProductImage){
        ExecutorService executor = Executors.newSingleThreadExecutor();


        executor.execute(() -> {
            try {
                ProductImg productImg = productImgDao.getProductImg(productId, businessId).get();
                lisnedProductImage.recieveProducImage(productImg);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ;
        });

    }


    public void getProductByIds(String productId, Long businessId, ViewModelListener<Product> listener){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(()->{
            Product product = null;
            try {
                product = productDao.getProductByIds(businessId, productId).get();
            } catch (ExecutionException e) {
                listener.result(null);
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                listener.result(null);
                throw new RuntimeException(e);
            }
            listener.result(product);
        });
    }

    public ListenableFuture<Integer> updateProductStock(Long businessId,
            String productId, int stockToDecrease){
        return productDao.updateStock(businessId, productId, stockToDecrease);
    }





    public interface ProductRepositoryListener{
        abstract void receiveResult(Object result);
    }

}
