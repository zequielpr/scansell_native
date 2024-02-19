package com.kunano.scansell_native.ui.home.business;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.repository.ProductRepository;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BusinessViewModel extends DeleteItemsViewModel {

    private LiveData<List<BusinessWithProduct>> businessListWithProductsList;
    private Long currentBusinessId;
    LiveData<List<Product>> allProductLive;
    private MutableLiveData<Business> currentBusinessLiveData;
    private String businessName;
    private MutableLiveData<String> businessAddress;
    private   List<Product> productList;
    private ProductRepository productRepository;


    public BusinessViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        businessName = "";
        businessAddress = new MutableLiveData<>();
        allProductLive = new MutableLiveData<>();
        currentBusinessId = new Long(-1);
        currentBusinessLiveData = new MutableLiveData<>();
        productList = new ArrayList<>();
    }


    public void updateCurrentBusiness(Business currentBusiness) {
        //Optional<Business> business = currentBusiness.stream().findFirst();

        if (currentBusiness!= null) {

            //Set name and address of the business
            businessName = currentBusiness.getBusinessName();
            if(!isDeleteModeActive)setSelectedItemsNumbLiveData(businessName);

            businessAddress.postValue(currentBusiness.getBusinessAddress());

        }
    }


    //prueba
    public LiveData<List<Product>> queryAllProducts(long currentBusinessId){
        if(this.currentBusinessId != currentBusinessId){
            System.out.println("New businessID: " + currentBusinessId);
            this.currentBusinessId = currentBusinessId;
            allProductLive = businessRepository.getProductsList(currentBusinessId);
            return allProductLive;
        }
        return allProductLive;

    }



    public void shortTap(Product product){
        if (isDeleteModeActive) {

            if (itemsToDelete.contains(product)) {
                itemsToDelete.remove(product);

            } else {
                itemsToDelete.add((Object) product);
                //Select to delete
            }


            selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
            isAllSelected = productList.size() == itemsToDelete.size();
            return;
        }

    }

    public void longTap(Product product){
        this.itemTypeToDelete = ItemTypeToDelete.PRODUCT;
        if (itemsToDelete.contains(product)) {
            itemsToDelete.remove(product);
        } else {
            itemsToDelete.add(product);
        }
        selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
        isAllSelected = productList.size() == itemsToDelete.size();

    }








    public List<Object> parseProductListToGeneric() {
        return allProductLive.getValue().stream()
                .map(product-> (Object) product)
                .collect(Collectors.toList());
    }



    public void createProduct(String productId,  String name, String buyingPrice, String sellingPrice, String stock,
                              String creatingDate, byte[] img, ListenResponse response) {

        double bPrice = Double.parseDouble(buyingPrice);
        double sPrice = Double.parseDouble(sellingPrice);
        int stck = Integer.parseInt(stock);
        Product product = new Product(productId, currentBusinessId, name, bPrice, sPrice, stck,
                creatingDate);

        productRepository.insertProduct(product, img, response::isSuccessfull);
    }


    public LiveData<List<Product>> getAllProductLive() {
        return allProductLive;
    }


    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public MutableLiveData<String> getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(MutableLiveData<String> businessAddress) {
        this.businessAddress = businessAddress;
    }

    public LiveData<List<BusinessWithProduct>> getBusinessListWithProductsList() {
        return businessListWithProductsList;
    }

    public void setBusinessListWithProductsList(LiveData<List<BusinessWithProduct>> businessListWithProductsList) {
        this.businessListWithProductsList = businessListWithProductsList;
    }

    public LiveData<Business> getCurrentBusinessLiveData() {
        return currentBusinessLiveData;
    }

    public void setCurrentBusinessLiveData(Business currentBusinessData) {
        currentBusinessLiveData.postValue(currentBusinessData);
    }


    public Long getCurrentBusinessId() {
        return currentBusinessId;
    }

    public void setCurrentBusinessId(Long currentBusinessId) {

        this.currentBusinessId = currentBusinessId;
    }
}