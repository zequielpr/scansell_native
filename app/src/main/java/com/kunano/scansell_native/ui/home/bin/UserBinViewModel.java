package com.kunano.scansell_native.ui.home.bin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.db.Converters;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserBinViewModel extends DeleteItemsViewModel {
    private ListenUserBinViewModel listenUserBinViewModel;

    private LiveData<List<Business>> recycledBusinessLiveData;

    private MutableLiveData<String> daysLeftTobeDeletedLiveDate;

    public UserBinViewModel(@NonNull Application application) {
        super(application);
        recycledBusinessLiveData = getBinsRepository().getBusinessInBin();
        daysLeftTobeDeletedLiveDate = new MutableLiveData();
    }



    public void restoreSingleBusiness(Business business){
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(()->{
            try {
                Integer resultado = binsRepository.restorageBusiness(business.getBusinessId()).get();
                if (resultado > 0){
                    listenUserBinViewModel.requestResult("exitoso");
                }else {
                    listenUserBinViewModel.requestResult("no exitoso");
                }

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }


    public String setDaysLeftToBeDeleted(Long businessId){

        Executor executor = Executors.newSingleThreadExecutor();
        String daysLeftTobeDeleted = "";

        executor.execute(()->{
            try {
                calcuLateDaysTobeDeleted( binsRepository.getRecycleDate(businessId).get());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        ;
        return "";
    }

    public void calcuLateDaysTobeDeleted(LocalDate recycleDate){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Long daysLeftTimestamp = Converters.dateToTimestamp(LocalDate.now()) - Converters.dateToTimestamp(recycleDate)  ;

            int days = (int) (30 - (daysLeftTimestamp /(1000*60*60*24)));

            String daysLeft = Integer.toString(days).concat(" ").
                    concat( days > 1?
                    getApplication().getString(R.string.days):
                    getApplication().getString(R.string.day));


            System.out.println("days left: " + daysLeft);

            daysLeftTobeDeletedLiveDate.postValue(daysLeft);
        }
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

    public interface ListenUserBinViewModel{
        abstract void requestResult(String message);
    }
}