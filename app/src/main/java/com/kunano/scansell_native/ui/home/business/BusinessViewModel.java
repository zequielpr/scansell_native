package com.kunano.scansell_native.ui.home.business;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.repository.home.ProductRepository;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;
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
    MutableLiveData<List<Product>> allProductLive;
    private LiveData<Business> currentBusinessLiveData;
    private String businessName;
    private String businessAddress;
    private   List<Product> productList;
    private ProductRepository productRepository;
    private boolean searchModeActive;
    ExecutorService executorService;


    public BusinessViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        businessName = "";
        allProductLive = new MutableLiveData<>();
        currentBusinessId = new Long(-1);
        currentBusinessLiveData = new MutableLiveData<>();
        productList = new ArrayList<>();
        searchModeActive = false;
    }


    public void updateCurrentBusiness(Business currentBusiness) {
        //Optional<Business> business = currentBusiness.stream().findFirst();

        System.out.println("Business name: " + businessName);
        if (currentBusiness!= null) {
            this.currentBusiness = currentBusiness;
            //Set name and address of the business
            businessName = currentBusiness.getBusinessName();
            System.out.println("Business name: " + businessName);
            if(!isDeleteModeActive)setSelectedItemsNumbLiveData(businessName);

            businessAddress = currentBusiness.getBusinessAddress();

        }
    }


    //prueba
    public LiveData<List<Product>> queryAllProducts(long currentBusinessId){
        if(this.currentBusinessId != currentBusinessId){
            System.out.println("New businessID: " + currentBusinessId);
            this.currentBusinessId = currentBusinessId;
             businessRepository.getProductsList(currentBusinessId).observeForever(allProductLive::postValue);

            return allProductLive;
        }
        return allProductLive;

    }


    //Search
    public void searchProduct(String query){
        businessRepository.searchProducts(currentBusinessId, query).observeForever(allProductLive::postValue);
    }

    public void sortProductByNameAsc(){

        businessRepository.sortProductByNameAsc(currentBusinessId).observeForever(allProductLive::postValue);
    }

    public void sortProductByNameDesc(){
        businessRepository.sortProductByNameDesc(currentBusinessId).observeForever(allProductLive::postValue);
    }

    public void sortProductByStockAsc(){
        businessRepository.sortProductByStockAsc(currentBusinessId).observeForever(allProductLive::postValue);
    }

    public void sortProductByStcokDesc(){
        businessRepository.sortProductByStockDesc(currentBusinessId).observeForever(allProductLive::postValue);
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
            isAllSelected = allProductLive.getValue().size() == itemsToDelete.size();
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
        isAllSelected = allProductLive.getValue().size() == itemsToDelete.size();

    }


    public void binSingleBusiness(ViewModelListener viewModelListener){

        executorService = Executors.newSingleThreadExecutor();

        executorService.execute(()->{
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

        if(this.currentBusinessId != null){
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