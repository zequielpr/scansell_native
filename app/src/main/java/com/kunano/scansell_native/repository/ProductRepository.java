package com.kunano.scansell_native.repository;

import android.app.Application;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.Home.product.ProductDao;
import com.kunano.scansell_native.model.Home.product.ProductImg;
import com.kunano.scansell_native.model.Home.product.ProductImgDao;
import com.kunano.scansell_native.model.db.AppDatabase;
import com.kunano.scansell_native.ui.home.business.ProductCardAdapter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
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
              /*  for (int i = 0; i < 10000; i++){
                    product.setProductId(UUID.randomUUID().toString());
                    resultado = productDao.insertProduct(product).get();
                    ProductImg img = new ProductImg(product.getProductId(), productImg);
                    productImgDao.insertProductImg(img).get();
                }*/

                product.setProductId(UUID.randomUUID().toString());
                resultado = productDao.insertProduct(product).get();
                ProductImg img = new ProductImg(product.getProductId(), productImg);
                productImgDao.insertProductImg(img).get();
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


    public ListenableFuture<Integer> deleteProduct(Product product) {
        return productDao.deleteProduct(product);
    }


    public void getProdductImage(String productId,
                                 ProductCardAdapter.LisnedProductImage lisnedProductImage){
        Executor executor = Executors.newSingleThreadExecutor();


        executor.execute(() -> {
            try {
                ProductImg productImg = productImgDao.getProductImg(productId).get();
                lisnedProductImage.recieveProducImage(productImg);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ;
        });
    }

}
