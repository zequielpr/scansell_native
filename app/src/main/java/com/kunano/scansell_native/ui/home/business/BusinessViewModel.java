package com.kunano.scansell_native.ui.home.business;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.repository.Repository;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BusinessViewModel extends DeleteItemsViewModel {

    private LiveData<List<BusinessWithProduct>> businessListWithProductsList;


    private Long businessId;
    LiveData<List<Product>> allProductLive;
    private MutableLiveData<Business> currentBusinessLiveData;
    private MutableLiveData<String> businessName;
    private MutableLiveData<String> businessAddress;

    public BusinessViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        businessListWithProductsList = repository.getAllBusinessWithProduct();
        businessName = new MutableLiveData<>();
        businessAddress = new MutableLiveData<>();
        currentBusinessLiveData = new MutableLiveData<>();

        businessId = new Long(0);
    }


    public void getProducts() {

    }


    public void updateCurrentBusiness(List<Business> currentBusiness) {
        Optional<Business> business = currentBusiness.stream().findFirst();

        if (business != null) {
            currentBusinessLiveData.postValue(business.get());
            businessName.postValue(business.get().getBusinessName());
            businessAddress.postValue(business.get().getBusinessAddress());
        }

    }





    public List<Product> getProductsFromBusiness(List<BusinessWithProduct> businessWithProducts) {
        Optional<List<Product>> productList = businessWithProducts.stream().
                filter((bp) -> bp.business.getBusinessId() == getBusinessId()).
                map((bp) -> (List<Product>) bp.productsList).
                findFirst();


        try {
            return productList.get();
        }catch (Exception e){
            return new ArrayList<>();
        }

    }



    public void createProduct(String name, String buyingPrice, String sellingPrice, String stock,
                              String creatingDate, byte[] img, ListenResponse response) {

        double bPrice = Double.parseDouble(buyingPrice);
        double sPrice = Double.parseDouble(sellingPrice);
        int stck = Integer.parseInt(stock);

        System.out.println("business id: " + getBusinessId());
        Product product = new Product(businessId, name, bPrice, sPrice, stck, img,
                creatingDate);

        repository.insertProduct(product, response::isSuccessfull);
    }


    public LiveData<List<Product>> getAllProductLive() {
        return allProductLive;
    }

    public void setAllProductLive(LiveData<List<Product>> allProductLive) {
        this.allProductLive = allProductLive;
    }

    public void setActualBusinessLiveData(Business business) {
        this.currentBusinessLiveData.postValue(business);
    }


    public MutableLiveData<String> getBusinessName() {
        return businessName;
    }

    public void setBusinessName(MutableLiveData<String> businessName) {
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

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
        System.out.println("Business di: " + this.businessId);
    }
}