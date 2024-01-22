package com.kunano.scansell_native.ui;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.repository.ProductRepository;
import com.kunano.scansell_native.repository.BusinessRepository;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DeleteItemsViewModel extends AndroidViewModel {

    protected BusinessRepository businessRepository;
    private ProductRepository productRepository;
    protected boolean isDeleteModeActive;
    protected boolean isAllSelected;
    protected boolean continueDeleting;
    protected LinkedHashSet<Object> itemsToDelete;
    protected HashSet<Object> deletedItems;

    /**example 10/20. 10 ites out of 20 have been eliminated **/
    protected String deletedItemsQuantity;
    protected int percentageDeleted;
    protected MutableLiveData<Integer> deleteProgressLiveData;

    /**Quantity of items to show in the appBar**/
    protected MutableLiveData<String> selectedItemsNumbLiveData;

    protected MutableLiveData<Drawable> checkedOrUncheckedCirclLivedata;

    /**example 10/20. 10 ites out of 20 have been eliminated **/
    protected MutableLiveData<String> deletedItemsLiveData;

    public static ItemTypeToDelete itemTypeToDelete;
    public static enum ItemTypeToDelete{
        BUSINESS, PRODUCT
    }


    public void selectAll(List<Object> items){
        isAllSelected = true;
        itemsToDelete.addAll(items);
        selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));

    }

    public void unSelectAll(){
        isAllSelected = false;
        itemsToDelete.clear();
        selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
    }

    public void  desactivateDeleteMod(String appBarTitle){
        selectedItemsNumbLiveData.postValue(appBarTitle);
        itemsToDelete.clear();
        isAllSelected = false;
        isDeleteModeActive = false;
    }


    protected void updateProgressBar() {
        deletedItemsQuantity = deletedItems.size() + "/" + itemsToDelete.size();
        if (percentageDeleted != (deletedItems.size() * 100 / itemsToDelete.size())) {
            percentageDeleted = (deletedItems.size() * 100 / itemsToDelete.size());
        }

        deleteProgressLiveData.postValue(percentageDeleted);
        deletedItemsLiveData.postValue(deletedItemsQuantity);

    }


    public void cancelDeleteProcess() {
        continueDeleting = false;
    }





    public void deletetItems(ListenResponse response, String appBarTitle) {
        selectedItemsNumbLiveData.postValue(getApplication().getString(R.string.businesses_title));
        checkedOrUncheckedCirclLivedata.postValue(null);
        isAllSelected = false;
        isDeleteModeActive = false;
        deletedItems.clear();
        continueDeleting = true;
        percentageDeleted = 0;


        System.out.println("type to delete " + itemTypeToDelete);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            switch (itemTypeToDelete){
                case BUSINESS:
                    deleteBusiness();
                    response.isSuccessfull(true);
                    desactivateDeleteMod(appBarTitle);
                    return;
                case PRODUCT:
                    deleteProduct();
                    response.isSuccessfull(true);
                    desactivateDeleteMod(appBarTitle);
                    return;
                default:

            }



            // Update the LiveData with the result

        });
    }

    private void deleteBusiness(){
        for (Object item : itemsToDelete) {

            try {

                if (!continueDeleting) {
                    break;
                }
                updateProgressBar();
                Thread.sleep(Math.round(1000 / itemsToDelete.size()));

                businessRepository.deleteBusiness((Business) item).get();
                deletedItems.add(item);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void deleteProduct(){
        for (Object item : itemsToDelete) {

            try {

                if (!continueDeleting) {
                    break;
                }
                updateProgressBar();
                Thread.sleep(Math.round(1000 / itemsToDelete.size()));

                productRepository.deleteProduct((Product) item).get();
                deletedItems.add(item);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }

        }

    }






    public BusinessRepository getRepository() {
        return businessRepository;
    }

    public void setRepository(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public boolean isDeleteModeActive() {
        return isDeleteModeActive;
    }

    public void setDeleteModeActive(boolean deleteModeActive) {
        isDeleteModeActive = deleteModeActive;
    }

    public boolean isAllSelected() {
        return isAllSelected;
    }

    public void setAllSelected(boolean allSelected) {
        isAllSelected = allSelected;
    }

    public boolean isContinueDeleting() {
        return continueDeleting;
    }

    public void setContinueDeleting(boolean continueDeleting) {
        this.continueDeleting = continueDeleting;
    }

    public LinkedHashSet<Object> getItemsToDelete() {
        return itemsToDelete;
    }

    public void setItemsToDelete(LinkedHashSet<Object> itemsToDelete) {
        this.itemsToDelete = itemsToDelete;
    }

    public HashSet<Object> getDeletedItems() {
        return deletedItems;
    }

    public void setDeletedItems(HashSet<Object> deletedItems) {
        this.deletedItems = deletedItems;
    }

    public String getDeletedItemsQuantity() {
        return deletedItemsQuantity;
    }

    public void setDeletedItemsQuantity(String deletedItemsQuantity) {
        this.deletedItemsQuantity = deletedItemsQuantity;
    }

    public int getPercentageDeleted() {
        return percentageDeleted;
    }

    public void setPercentageDeleted(int percentageDeleted) {
        this.percentageDeleted = percentageDeleted;
    }

    public MutableLiveData<Integer> getDeleteProgressLiveData() {
        return deleteProgressLiveData;
    }

    public void setDeleteProgressLiveData(MutableLiveData<Integer> deleteProgressLiveData) {
        this.deleteProgressLiveData = deleteProgressLiveData;
    }

    public MutableLiveData<String> getSelectedItemsNumbLiveData() {
        return selectedItemsNumbLiveData;
    }

    public void setSelectedItemsNumbLiveData(String selectedItemsNumbLiveData) {
        this.selectedItemsNumbLiveData.postValue(selectedItemsNumbLiveData);
    }

    public MutableLiveData<Drawable> getCheckedOrUncheckedCirclLivedata() {
        return checkedOrUncheckedCirclLivedata;
    }

    public void setCheckedOrUncheckedCirclLivedata(Drawable checkedOrUncheckedCirclLivedata) {
        this.checkedOrUncheckedCirclLivedata.postValue(checkedOrUncheckedCirclLivedata);
    }

    public MutableLiveData<String> getDeletedItemsLiveData() {
        return deletedItemsLiveData;
    }

    public void setDeletedItemsLiveData(MutableLiveData<String> deletedItemsLiveData) {
        this.deletedItemsLiveData = deletedItemsLiveData;
    }

    public DeleteItemsViewModel(@NonNull Application application) {
        super(application);
        businessRepository = new BusinessRepository(application);
        productRepository = new ProductRepository(application);
        this.deleteProgressLiveData = new MutableLiveData<>();
        this.selectedItemsNumbLiveData = new MutableLiveData<>();
        checkedOrUncheckedCirclLivedata = new MutableLiveData<>();
        this.deletedItemsLiveData = new MutableLiveData<>();
        this.itemsToDelete = new LinkedHashSet<>();
        this.deletedItems = new HashSet<>();
        this.isDeleteModeActive = false;
        this.isAllSelected = false;
        itemTypeToDelete = null;
    }

    public ItemTypeToDelete getItemTypeToDelete() {
        return itemTypeToDelete;
    }

    public void setItemTypeToDelete(ItemTypeToDelete itemTypeToDelete) {
        this.itemTypeToDelete = itemTypeToDelete;
    }



}



