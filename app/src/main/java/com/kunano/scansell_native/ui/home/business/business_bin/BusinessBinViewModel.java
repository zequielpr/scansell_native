package com.kunano.scansell_native.ui.home.business.business_bin;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.Converters;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BusinessBinViewModel extends DeleteItemsViewModel {
    private  ListenBusinessBinViewModel listenBusinessBinViewMode;

    private LiveData<List<Product>> recycledProductLiveData;

    private MutableLiveData<String> daysLeftTobeDeletedLiveDate;

    private MutableLiveData<Integer> restoreButtonVisibilityLiveData;

    private long currentBusinessId;


    public BusinessBinViewModel(@NonNull Application application) {
        super(application);
        recycledProductLiveData = new MutableLiveData<>();
        daysLeftTobeDeletedLiveDate = new MutableLiveData();
        restoreButtonVisibilityLiveData = new MutableLiveData<>(View.VISIBLE);
        currentBusinessId = new Long(-0);
    }


    public void restoreSingleProduct(Product product){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(()->{
            try {
                Integer resultado = binsRepository.restorageProducts(product.getProductId(), currentBusinessId).get();
                if (resultado > 0){
                    listenBusinessBinViewMode.requestResult("exitoso");
                }else {
                    listenBusinessBinViewMode.requestResult("no exitoso");
                }

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }


    public String setDaysLeftToBeDeleted(String productId){

        if (continuePassing) return "";
        Executor executor = Executors.newSingleThreadExecutor();
        String daysLeftTobeDeleted = "";

        executor.execute(()->{
            try {
                calculateDaysTobeDeleted( binsRepository.getRecycleDate(productId).get());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }catch (Exception e){
                System.out.println("Exception: " + e.getMessage());
            }

        });

        ;
        return "";
    }

    public void calculateDaysTobeDeleted(LocalDate recycleDate){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Long daysLeftTimestamp = Converters.dateToTimestamp(LocalDate.now()) - Converters.dateToTimestamp(recycleDate)  ;

            int days = (int) (30 - (daysLeftTimestamp /(1000*60*60*24)));

            String daysLeft = Integer.toString(days).concat(" ").
                    concat( days > 1?
                            getApplication().getString(R.string.days):
                            getApplication().getString(R.string.day));

            daysLeftTobeDeletedLiveDate.postValue(daysLeft);
        }
    }



    public void shortTap(Product product) {
        if (isDeleteModeActive) {

            if (itemsToDelete.contains(product)) {
                itemsToDelete.remove(product);

            } else {
                itemsToDelete.add(product);
                //Select to delete
            }


            selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
            isAllSelected = recycledProductLiveData.getValue().size() == itemsToDelete.size();
            return;
        }

        //currentBusiness = repository.getBusinesById(product.getBusinessId());
    }

    public void longTap(Product product) {
        if (itemsToDelete.contains(product)) {
            itemsToDelete.remove(product);
        } else {
            itemsToDelete.add(product);
        }
        selectedItemsNumbLiveData.postValue(Integer.toString(itemsToDelete.size()));
        isAllSelected = recycledProductLiveData.getValue().size() == itemsToDelete.size();
    }



    public List<Object> parseBusinessListToGeneric() {
        return recycledProductLiveData.getValue().stream()
                .map(product -> (Object) product)
                .collect(Collectors.toList());
    }






    public LiveData<List<Product>> getRecycledProductLiveData(long  currentBusinessId) {
        if(this.currentBusinessId != currentBusinessId){
            this.currentBusinessId = currentBusinessId;
            recycledProductLiveData = getBinsRepository().getProductInBin(this.currentBusinessId);
            return recycledProductLiveData;
        }
        return recycledProductLiveData;
    }

    public void setRecycledProductLiveData(LiveData<List<Product>> recycledProductLiveData) {
        this.recycledProductLiveData = recycledProductLiveData;
    }


    public ListenBusinessBinViewModel getListenBusinessBinViewModel() {
        return listenBusinessBinViewMode;
    }

    public void setListenBusinessBinViewModel(ListenBusinessBinViewModel listenBusinessBinViewMode) {
        this.listenBusinessBinViewMode = listenBusinessBinViewMode;
    }


    public MutableLiveData getDaysLeftTobeDeletedLiveDate() {
        return daysLeftTobeDeletedLiveDate;
    }

    public void setDaysLeftTobeDeletedLiveDate(MutableLiveData daysLeftTobeDeletedLiveDate) {
        this.daysLeftTobeDeletedLiveDate = daysLeftTobeDeletedLiveDate;
    }


    public MutableLiveData<Integer> getRestoreButtonVisibilityLiveData() {
        return restoreButtonVisibilityLiveData;
    }

    public void setRestoreButtonVisibilityLiveData(Integer restoreButtonVisibilityLiveData) {
        this.restoreButtonVisibilityLiveData.postValue(restoreButtonVisibilityLiveData);
    }

    public interface ListenBusinessBinViewModel{
        abstract void requestResult(String message);
    }
}