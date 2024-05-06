package com.kunano.scansell_native.ui.home.bin;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.repository.home.BinsRepository;
import com.kunano.scansell_native.ui.sell.receipts.dele_component.ProcessItemsComponent;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserBinViewModel extends AndroidViewModel {
    private ListenUserBinViewModel listenUserBinViewModel;

    private LiveData<List<Business>> recycledBusinessLiveData;

    private MutableLiveData<String> daysLeftTobeDeletedLiveDate;

    private MutableLiveData<Integer> restoreButtonVisibilityLiveData;
    private MutableLiveData<Integer> cardBackgroundColor;
    private BinsRepository binsRepository;
    private MutableLiveData<String> selectedItemsNumbLiveData;
    private MutableLiveData<Drawable> checkedOrUncheckedCircleLivedata;

    public UserBinViewModel(@NonNull Application application) {
        super(application);
        binsRepository = new BinsRepository(application);
        recycledBusinessLiveData = binsRepository.getBusinessInBin();
        daysLeftTobeDeletedLiveDate = new MutableLiveData();
        restoreButtonVisibilityLiveData = new MutableLiveData<>(View.VISIBLE);
        cardBackgroundColor = new MutableLiveData<>();
        selectedItemsNumbLiveData = new MutableLiveData<>();
        checkedOrUncheckedCircleLivedata = new MutableLiveData<>();
    }



    public void restoreSingleBusiness(Business business){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(()->{
            try {
                Integer resultado = binsRepository.restorageBusiness(business.getBusinessId()).get();
                if (resultado > 0){
                    listenUserBinViewModel.requestResult(getApplication().getString(R.string.business_restored_successfully));
                }else {
                    listenUserBinViewModel.requestResult(getApplication().getString(R.string.there_has_been_an_error));
                }

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }


    /*public String setDaysLeftToBeDeleted(Long businessId){

        if (continuePassing) return "";
        Executor executor = Executors.newSingleThreadExecutor();
        String daysLeftTobeDeleted = "";

        executor.execute(()->{
            try {
                calculateDaysTobeDeleted( binsRepository.getRecycleDate(businessId).get());
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
*/


    public void shortTap(Business business, ProcessItemsComponent<Business> businessProcessItemsComponent) {
       processBusiness(business, businessProcessItemsComponent);
    }

    public void longTap(Business business, ProcessItemsComponent<Business> businessProcessItemsComponent) {
        processBusiness(business, businessProcessItemsComponent);
    }

    private void processBusiness(Business business, ProcessItemsComponent<Business> businessProcessItemsComponent){
        if (businessProcessItemsComponent.isItemToBeProcessed(business)) {
            businessProcessItemsComponent.removeItemToProcess(business);

        } else {
            businessProcessItemsComponent.addItemToProcess(business);
            //Select to delete
        }

        selectedItemsNumbLiveData.postValue(Integer.toString(businessProcessItemsComponent.getItemsToProcess().size()));
    }




    public LiveData<List<Business>> getRecycledBusinessLiveData() {
        return recycledBusinessLiveData;
    }

    public void setRecycledBusinessLiveData(LiveData<List<Business>> recycledBusinessLiveData) {
        this.recycledBusinessLiveData = recycledBusinessLiveData;
    }


    public ListenUserBinViewModel getListenUserBinViewModel() {
        return listenUserBinViewModel;
    }

    public void setListenUserBinViewModel(ListenUserBinViewModel listenUserBinViewModel) {
        this.listenUserBinViewModel = listenUserBinViewModel;
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

    public MutableLiveData<Integer> getCardBackgroundColor() {
        return cardBackgroundColor;
    }

    public void setCardBackgroundColor(Integer cardBackgroundColor) {
        this.cardBackgroundColor.postValue(cardBackgroundColor);
    }

    public MutableLiveData<Drawable> getCheckedOrUncheckedCircleLivedata() {
        return checkedOrUncheckedCircleLivedata;
    }

    public void setCheckedOrUncheckedCircleLivedata(Drawable checkedOrUncheckedCircleLivedata) {
        this.checkedOrUncheckedCircleLivedata.postValue(checkedOrUncheckedCircleLivedata);
    }

    public interface ListenUserBinViewModel{
        abstract void requestResult(String message);
    }

    public MutableLiveData<String> getSelectedItemsNumbLiveData() {
        return selectedItemsNumbLiveData;
    }

    public void setSelectedItemsNumbLiveData(String selectedItemsNumbLiveData) {
        this.selectedItemsNumbLiveData.postValue(selectedItemsNumbLiveData);
    }
}