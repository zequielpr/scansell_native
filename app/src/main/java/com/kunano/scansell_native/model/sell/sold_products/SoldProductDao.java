package com.kunano.scansell_native.model.sell.sold_products;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.model.Home.product.Product;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface SoldProductDao {

    @Insert
    ListenableFuture<Long> insertSoldProduct(SoldProduct soldProduct);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public ListenableFuture<List<Long>> insertSoldProductList(List<SoldProduct> soldProductList);


    @Query("DELETE FROM soldproduct WHERE receiptIdFK = (:receiptId) AND productIdFK = (:productId) " +
            "AND businessIdFK = (:businessId) AND ROWID IN (SELECT ROWID FROM soldproduct WHERE " +
            "receiptIdFK = (:receiptId) AND productIdFK = (:productId) " +
            "AND businessIdFK = (:businessId) LIMIT 1)")
    ListenableFuture<Integer> deleteSoldProduct(String productId, Long businessId, String receiptId);


    //Stil to be develop
    @Query("SELECT productId, product.businessIdFK, product_name, buying_price," +
            "selling_price, stock, cratingDate" +
            " FROM product INNER JOIN (SELECT * FROM soldproduct" +
            " WHERE receiptIdFK = (:receiptId)) ON product.productId = productIdFK " +
            "AND product.businessIdFK =(:businessId)")
    LiveData<List<Product>> getSoldProducts(String receiptId, Long businessId);





    @Query("SELECT COUNT(*) AS soldQuantity, product_name AS productName, " +
            "(SELECT COUNT(*) FROM soldproduct INNER JOIN receipt on soldproduct.receiptIdFK = receipt.receiptId AND soldproduct.businessIdFK = (:businessId)" +
            "WHERE soldproduct.businessIdFK = (:businessId) AND sellingDate >= (:startOfCurrentWeek)) AS soldProductsTotal " +
            "FROM product " +
            "INNER JOIN soldproduct ON productId = productIdFK AND soldproduct.businessIdFK = (:businessId)" +
            "INNER JOIN receipt ON soldproduct.receiptIdFK = receipt.receiptId " +
            "WHERE product.businessIdFK = (:businessId) AND soldproduct.businessIdFK " +
            "AND sellingDate >= (:startOfCurrentWeek)" +
            "GROUP BY productId, product_name " +
            "ORDER BY soldQuantity DESC LIMIT 3")
    LiveData<List<MostSoldProducts>> getMostSoldProductsInCurrentWeek(Long businessId, LocalDateTime startOfCurrentWeek);


    @Query("SELECT COUNT(*) AS soldQuantity, product_name AS productName, " +
            "(SELECT COUNT(*) FROM soldproduct " +
            "INNER JOIN receipt on soldproduct.receiptIdFK = receipt.receiptId WHERE soldproduct.businessIdFK = (:businessId) " +
            "AND sellingDate >= (:startOfCurrentWeek) AND sellingDate < (:currentWeekDate)) AS soldProductsTotal " +
            "FROM product " +
            "INNER JOIN soldproduct ON productId = productIdFK " +
            "INNER JOIN receipt on soldproduct.receiptIdFK = receipt.receiptId " +
            "WHERE product.businessIdFK = (:businessId) AND soldproduct.businessIdFK " +
            "AND sellingDate >= (:startOfCurrentWeek) AND sellingDate < (:currentWeekDate)" +
            "GROUP BY productId, product_name " +
            "ORDER BY soldQuantity DESC LIMIT 3")
    LiveData<List<MostSoldProducts>> getMostSoldProductsInLastWeek(Long businessId,
                                                                   LocalDateTime startOfCurrentWeek,
                                                                   LocalDateTime currentWeekDate);

}
