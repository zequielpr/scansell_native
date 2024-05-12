package com.kunano.scansell_native;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    HostComponentListener hostComponentListener;
    MutableLiveData<Integer> bottomNavBarVisibility;

    Boolean isPremiumActive;

    public MainActivityViewModel() {
        bottomNavBarVisibility = new MutableLiveData<>();
    }


    public void hideBottomNavBar(){
        hostComponentListener.hideBottomNavBar();
    }

    public void showBottomNavBar(){
        hostComponentListener.showBottomNavBar();
    }


    public HostComponentListener getHostComponentListener() {
        return hostComponentListener;
    }

    public void setHostComponentListener(HostComponentListener hostComponentListener) {
        this.hostComponentListener = hostComponentListener;
    }


    public MutableLiveData<Integer> getBottomNavBarVisibility() {
        return bottomNavBarVisibility;
    }

    public void setBottomNavBarVisibility(Integer visibility) {
        this.bottomNavBarVisibility.postValue(visibility);
    }


    public Boolean isPremiumActive() {
        return isPremiumActive;
    }

    public void setPremiumActive(Boolean premiumActive) {
        isPremiumActive = premiumActive;
    }

    public interface HostComponentListener{
        abstract void hideBottomNavBar();
        abstract void showBottomNavBar();
    }




}

