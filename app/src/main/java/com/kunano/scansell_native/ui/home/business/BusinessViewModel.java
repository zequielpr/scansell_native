package com.kunano.scansell_native.ui.home.business;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.repository.home.ProductRepository;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.components.ViewModelListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BusinessViewModel extends DeleteItemsViewModel {

    private LiveData<List<BusinessWithProduct>> businessListWithProductsList;
    private Long currentBusinessId;
    MutableLiveData<List<Product>> allProductMuyableLiveData;
    private LiveData<Business> currentBusinessLiveData;
    private String businessName;
    private String businessAddress;
    private List<Product> productList;
    private ProductRepository productRepository;
    private boolean searchModeActive;
    ExecutorService executorService;
    private LiveData<List<Product>> allProductLiveData;
    Observer<List<Product>> productsObserver;


    public BusinessViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        businessName = "";
        allProductLiveData = new MutableLiveData<>();
        allProductMuyableLiveData = new MutableLiveData<>();
        currentBusinessId = new Long(-1);
        currentBusinessLiveData = new MutableLiveData<>();
        productList = new ArrayList<>();
        searchModeActive = false;

        productsObserver = allProductMuyableLiveData::postValue;
    }


    public void updateCurrentBusiness(Business currentBusiness) {
        //Optional<Business> business = currentBusiness.stream().findFirst();

        System.out.println("Business name: " + businessName);
        if (currentBusiness != null) {
            this.currentBusiness = currentBusiness;
            //Set name and address of the business
            businessName = currentBusiness.getBusinessName();
            System.out.println("Business name: " + businessName);
            if (!isDeleteModeActive) setSelectedItemsNumbLiveData(businessName);

            businessAddress = currentBusiness.getBusinessAddress();

        }
    }


    //prueba
    public LiveData<List<Product>> queryAllProducts(long currentBusinessId) {
        System.out.println("New businessID: " + currentBusinessId);
        if (this.currentBusinessId != currentBusinessId) {
            System.out.println("New businessID: " + currentBusinessId);
            this.currentBusinessId = currentBusinessId;
            allProductLiveData.removeObserver(productsObserver);
            allProductLiveData = businessRepository.getProductsList(currentBusinessId);
            allProductLiveData.observeForever(productsObserver);

            return allProductMuyableLiveData;
        }
        return allProductMuyableLiveData;

    }


    //Search
    public void searchProduct(String query) {
        allProductLiveData.removeObserver(productsObserver);
        allProductLiveData = businessRepository.searchProducts(currentBusinessId, query);
        allProductLiveData.observeForever(productsObserver);
    }

    public void sortProductByNameAsc() {
        allProductLiveData.removeObserver(productsObserver);
        allProductLiveData = businessRepository.sortProductByNameAsc(currentBusinessId);
        allProductLiveData.observeForever(productsObserver);
    }

    public void sortProductByNameDesc() {
        allProductLiveData.removeObserver(productsObserver);
        allProductLiveData = businessRepository.sortProductByNameDesc(currentBusinessId);
        allProductLiveData.observeForever(productsObserver);
    }

    public void sortProductByStockAsc() {
        allProductLiveData.removeObserver(productsObserver);
        allProductLiveData = businessRepository.sortProductByStockAsc(currentBusinessId);
        allProductLiveData.observeForever(productsObserver);
    }

    public void sortProductByStcokDesc() {
        allProductLiveData.removeObserver(productsObserver);
        allProductLiveData = businessRepository.sortProductByStockDesc(currentBusinessId);
        allProductLiveData.observeForever(productsObserver);
    }


    public void shortTap(Product product) {
        if (isDeleteModeActive) {

            if (itemsToDelete.contains(product)) {
                itemsToDelete.remove(product);

            } else {
                itemsToDelete.add((Object) product);
                //Select to delete
            }


            selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
            isAllSelected = allProductMuyableLiveData.getValue().size() == itemsToDelete.size();
            return;
        }

    }

    public void longTap(Product product) {
        this.itemTypeToDelete = ItemTypeToDelete.PRODUCT;
        if (itemsToDelete.contains(product)) {
            itemsToDelete.remove(product);
        } else {
            itemsToDelete.add(product);
        }
        selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
        isAllSelected = allProductMuyableLiveData.getValue().size() == itemsToDelete.size();

    }


    public void binSingleBusiness(ViewModelListener viewModelListener) {

        executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            Long result;
            try {
                result = super.binsRepository.sendBusinessTobin(currentBusinessId).get();
                viewModelListener.result(result > 0);

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


    }


    public List<Object> parseProductListToGeneric() {
        return allProductMuyableLiveData.getValue().stream()
                .map(product -> (Object) product)
                .collect(Collectors.toList());
    }

    public void updateBusiness(String name, String address, String creatingData, ListenResponse listenResponse) {
        Business business = new Business(name, address, creatingData);
        business.setBusinessId(currentBusinessId);
        businessRepository.updateBusiness(business, listenResponse);
    }


    public LiveData<List<Product>> getAllProductMuyableLiveData() {
        return allProductMuyableLiveData;
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

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public LiveData<List<BusinessWithProduct>> getBusinessListWithProductsList() {
        return businessListWithProductsList;
    }

    public void setBusinessListWithProductsList(LiveData<List<BusinessWithProduct>> businessListWithProductsList) {
        this.businessListWithProductsList = businessListWithProductsList;
    }

    public LiveData<Business> getCurrentBusinessLiveData() {

        if (this.currentBusinessId != null) {
            currentBusinessLiveData = businessRepository.getBusinesById(currentBusinessId);

        }
        return currentBusinessLiveData;
    }

    private Business currentBusiness;

    public Business getCurrentBusiness() {
        return currentBusiness;
    }

    public void setCurrentBusiness(Business currentBusiness) {
        this.currentBusiness = currentBusiness;
    }

    public Long getCurrentBusinessId() {
        return currentBusinessId;
    }

    public void setCurrentBusinessId(Long currentBusinessId) {

        this.currentBusinessId = currentBusinessId;
    }

    public boolean isSearchModeActive() {
        return searchModeActive;
    }

    public void setSearchModeActive(boolean searchModeActive) {
        this.searchModeActive = searchModeActive;
    }
}