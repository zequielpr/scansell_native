package com.kunano.scansell_native.model.sell.product_to_sel_draft;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.product.Product;

import java.util.List;

@Dao
public interface ProductToSellDraftDao {
    @Query("SELECT * FROM product INNER JOIN producttoselldraft ON product.productId = producttoselldraft.productIdFK " +
            "WHERE product.businessIdFK = (:businessID) AND producttoselldraft.businessIdIdFK = (:businessID);")
    LiveData<List<Product>> getProductsInDraft(Long businessID);

    @Insert
    ListenableFuture<Long> insertProductInDraft(ProductToSellDraft productToSellDraft);


    @Query("DELETE  FROM producttoselldraft WHERE businessIdIdFK = (:businessID)")
    void empryDraft(Long businessID);


    //With not limit, it would delete al the product  with the given id
    @Query("DELETE FROM producttoselldraft WHERE businessIdIdFK = (:businessID) AND productIdFK = (:productId) AND" +
            " ROWID IN (SELECT ROWID FROM producttoselldraft WHERE businessIdIdFK = (:businessID) AND productIdFK = (:productId) LIMIT 1)")
    void deleteProductFromDraft(Long businessID, String productId);;



}
