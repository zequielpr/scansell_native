package com.kunano.scansell_native.model.Home;


import com.kunano.scansell_native.model.db.Product;
import com.kunano.scansell_native.model.db.ProductDao;
import com.kunano.scansell_native.model.db.relationships.BusinessWithProduct;

import java.util.List;
import java.util.stream.Collectors;

public class ProductModel {
    private ProductDao productDao;
    private String name;
    private Long businessId;
    private byte[] img;
    private double buyingPrice;
    private double sellingPrice;
    private int stock;
    Product productEntity;

    public ProductModel() {
        super();
    }
    public ProductModel(Long businessId) {
        super();
        this.businessId = businessId;
    }

    public ProductModel(ProductDao productDao, String name, Long businessId, byte[] img,
                        double buyingPrice, double sellingPrice, int stock) {
        super();
        this.productDao = productDao;
        this.name = name;
        this.businessId = businessId;
        this.img = img;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
    }

    public void insertProduct(){
         productEntity = new Product();
        setProductEntityData();
        productDao.insertProduct(productEntity);
    }

    public List<Product> getAllProducts(){
        List<BusinessWithProduct> businessWithProductList = productDao.getBusinessWithProduct();

        List<Object> productsAndBusiness = businessWithProductList.stream().map((businessWithProduct)-> (BusinessWithProduct)businessWithProduct).
                map(businessWithProduct -> (Long)businessWithProduct.business.businessId).
                filter((businessId)-> businessId.equals(this.businessId)).collect(Collectors.toList());

        List<Product> productsList = ((BusinessWithProduct) productsAndBusiness).productsList;

        return productsList;
    }





    private void setProductEntityData(){
        productEntity.productName = this.name;
        productEntity.buying_price = this.buyingPrice;
        productEntity.selling_price = this.sellingPrice;
        productEntity.stock = this.stock;
        productEntity.img = this.img;
        productEntity.createdInbusinessId = this.businessId;
    }









    public ProductDao getProductDao() {
        return productDao;
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
