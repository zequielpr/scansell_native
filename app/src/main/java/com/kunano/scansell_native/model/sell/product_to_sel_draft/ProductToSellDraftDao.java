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
    @Query("SELECT * FROM product INNER JOIN (SELECT * FROM producttoselldraft" +
            " WHERE businessIdIdFK = (:businessID)) ON product.productId = productIdFK")
    LiveData<List<Product>> getProductsInDraft(Long businessID);

    @Insert
    ListenableFuture<Long> insertProductInDraft(ProductToSellDraft productToSellDraft);


    @Query("DELETE  FROM producttoselldraft WHERE businessIdIdFK = (:businessID)")
    void empryDraft(Long businessID);


    @Query("DELETE  FROM producttoselldraft WHERE businessIdIdFK = (:businessID)" +
            "AND productIdFK = (:productId)")
    void deleteProductFromDraft(Long businessID, String productId);;



}
