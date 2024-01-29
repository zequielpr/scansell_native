package com.kunano.scansell_native.ui.home.bin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.ui.DeleteItemsViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserBinViewModel extends DeleteItemsViewModel {
    private ListenUserBinViewModel listenUserBinViewModel;

    private LiveData<List<Business>> recycledBusinessLiveData;

    public UserBinViewModel(@NonNull Application application) {
        super(application);
        recycledBusinessLiveData = getBinsRepository().getBusinessInBin();
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

    public interface ListenUserBinViewModel{
        abstract void requestResult(String message);
    }
}