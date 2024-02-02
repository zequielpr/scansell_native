package com.kunano.scansell_native;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    HostComponentListener hostComponentListener;
    MutableLiveData<Integer> bottomNavBarVisibility;

    HandleBackPress handleBackPress;

    public MainActivityViewModel() {
        bottomNavBarVisibility = new MutableLiveData<>();
    }


    public void hideBottomNavBar(){
        hostComponentListener.hideBottomNavBar();
    }

    public void showBottomNavBar(){
        hostComponentListener.showBottomNavBar();
    }

    public void notifyBackPressed(){
        if(handleBackPress != null){
            handleBackPress.backButtonPressed();
        };
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

    public interface HostComponentListener{
        abstract void hideBottomNavBar();
        abstract void showBottomNavBar();
    }


    @FunctionalInterface
    public interface HandleBackPress{
        abstract void backButtonPressed();
    }


    public HandleBackPress getHandleBackPress() {
        return handleBackPress;
    }

    public void setHandleBackPress(HandleBackPress handleBackPress) {
        this.handleBackPress = handleBackPress;
    }
}

