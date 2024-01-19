package com.kunano.scansell_native.ui;

import android.app.Application;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.repository.Repository;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DeleteItemsViewModel extends AndroidViewModel {

    protected Repository repository;
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


    public void selectAll(List<Object> items){
        isAllSelected = true;
        itemsToDelete.addAll( items);
        selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));

    }

    public void unSelectAll(){
        isAllSelected = false;
        itemsToDelete.clear();
        selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
    }

    public void  desactivateDeleteMod(){
        selectedItemsNumbLiveData.postValue(getApplication().getString(R.string.businesses_title));
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





    public void deletetItems(ListenResponse response) {
        selectedItemsNumbLiveData.postValue(getApplication().getString(R.string.businesses_title));
        checkedOrUncheckedCirclLivedata.postValue(null);
        isAllSelected = false;
        isDeleteModeActive = false;
        deletedItems.clear();
        continueDeleting = true;
        percentageDeleted = 0;



        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            for (Object item : itemsToDelete) {

                try {

                    if (!continueDeleting) {
                        break;
                    }
                    updateProgressBar();
                    Thread.sleep(Math.round(1000 / itemsToDelete.size()));


                    //Evaluar que tipo de item se recibe
                    if(item.getClass() == Business.class){
                        deleteBusiness(item);
                    }
                    deletedItems.add(item);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

            }

            response.isSuccessfull(true);
            desactivateDeleteMod();

            // Update the LiveData with the result

        });
    }

    private  void deleteBusiness(Object business) throws ExecutionException, InterruptedException {
        repository.deleteBusiness((Business) business).get();
    }





    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
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

    public void setSelectedItemsNumbLiveData(MutableLiveData<String> selectedItemsNumbLiveData) {
        this.selectedItemsNumbLiveData = selectedItemsNumbLiveData;
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
        repository = new Repository(application);
    }
}
