package com.kunano.scansell_native.ui.home;

import android.view.LayoutInflater;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<BusinessCardAdepter>  listBusinessApader;
    private LayoutInflater layoutInflater;

    public HomeViewModel(LayoutInflater inflater) {
        this.layoutInflater = inflater;
        mText = new MutableLiveData<>();
        listBusinessApader = new MutableLiveData<>();
    }

    public void setBusinessList(List<Map<String, Object>> businessesListData){
        this.listBusinessApader.postValue(new BusinessCardAdepter(businessesListData, layoutInflater.getContext()));
    }

    public LiveData<BusinessCardAdepter> getListBusinessApader(){return listBusinessApader;}
}