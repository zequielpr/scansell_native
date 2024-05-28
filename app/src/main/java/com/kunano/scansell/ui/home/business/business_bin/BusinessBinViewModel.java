package com.kunano.scansell.ui.home.business.business_bin;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell.components.ProcessItemsComponent;
import com.kunano.scansell.model.Home.product.Product;
import com.kunano.scansell.R;
import com.kunano.scansell.repository.home.BinsRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BusinessBinViewModel extends AndroidViewModel{
    private  ListenBusinessBinViewModel listenBusinessBinViewMode;

    private LiveData<List<Product>> recycledProductLiveData;

    private MutableLiveData<String> daysLeftTobeDeletedLiveDate;

    private MutableLiveData<Integer> restoreButtonVisibilityLiveData;

    private MutableLiveData<String> toolBarTitle;

    private BinsRepository binsRepository;

    private MutableLiveData<Drawable> checkedOrUncheckedCircleLivedata;

    private long currentBusinessId;


    public BusinessBinViewModel(@NonNull Application application) {
        super(application);

        binsRepository = new BinsRepository(application);

        recycledProductLiveData = new MutableLiveData<>();
        daysLeftTobeDeletedLiveDate = new MutableLiveData();
        restoreButtonVisibilityLiveData = new MutableLiveData<>(View.VISIBLE);
        currentBusinessId = new Long(-0);
        checkedOrUncheckedCircleLivedata = new MutableLiveData<>();
        toolBarTitle = new MutableLiveData<>();
    }


    public void restoreSingleProduct(Product product){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(()->{
            try {
                Integer resultado = binsRepository.restorageProducts(product.getProductId(), currentBusinessId).get();
                if (resultado > 0){
                    listenBusinessBinViewMode.requestResult(getApplication().getString(R.string.product_restored_successfully));
                }else {
                    listenBusinessBinViewMode.requestResult(getApplication().getString(R.string.there_has_been_an_error));
                }

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }


    /*public String setDaysLeftToBeDeleted(String productId){

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
    }*/



    public void shortTap(Product product, ProcessItemsComponent<Product> productProcessItemsComponent) {
        selectItem(product, productProcessItemsComponent);
    }

    public void longTap(Product product, ProcessItemsComponent<Product> productProcessItemsComponent) {
        selectItem(product, productProcessItemsComponent);
    }

    private void selectItem(Product product, ProcessItemsComponent<Product> productProcessItemsComponent){
        if (productProcessItemsComponent.isItemToBeProcessed(product)){
            productProcessItemsComponent.removeItemToProcess(product);
        }else {
            productProcessItemsComponent.addItemToProcess(product);
        }

        toolBarTitle.postValue(String.valueOf(productProcessItemsComponent.getItemsToProcess().size()));
    }



    public List<Object> parseBusinessListToGeneric() {
        return recycledProductLiveData.getValue().stream()
                .map(product -> (Object) product)
                .collect(Collectors.toList());
    }






    public LiveData<List<Product>> getRecycledProductLiveData(long  currentBusinessId) {
        if(this.currentBusinessId != currentBusinessId){
            this.currentBusinessId = currentBusinessId;
            recycledProductLiveData = binsRepository.getProductInBin(this.currentBusinessId);
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

    public MutableLiveData<String> getToolBarTitle() {
        return toolBarTitle;
    }

    public void setToolBarTitle(String toolBarTitle) {
        this.toolBarTitle.postValue(toolBarTitle);
    }

    public MutableLiveData<Drawable> getCheckedOrUncheckedCircleLivedata() {
        return checkedOrUncheckedCircleLivedata;
    }

    public void setCheckedOrUncheckedCircleLivedata(Drawable checkedOrUncheckedCircleLivedata) {
        this.checkedOrUncheckedCircleLivedata.postValue(checkedOrUncheckedCircleLivedata);
    }

    public interface ListenBusinessBinViewModel{
        abstract void requestResult(String message);
    }
}