package com.kunano.scansell_native.ui.home;

import android.view.LayoutInflater;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kunano.scansell_native.ui.CustomAdapter;

import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<CustomAdapter>  listBusinessApader;
    private LayoutInflater layoutInflater;

    public HomeViewModel(LayoutInflater inflater) {
        this.layoutInflater = inflater;
        mText = new MutableLiveData<>();
        listBusinessApader = new MutableLiveData<>();
    }

    public void setBusinessList(List<Map<String, Object>> businessesListData){
        this.listBusinessApader.postValue(new CustomAdapter(businessesListData, layoutInflater.getContext()));

    }



    /*public void setBusinessList(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            list.add("Item: " + i);
        }
        this.listBusinessApader.postValue(new CustomAdapter(list, inflater.getContext()));
    }*/

    /*public void mostrar_data(String nombre){
        this.mText.postValue(nombre);
    }


    public LiveData<String> getText() {
        return mText;
    }*/
    public LiveData<CustomAdapter> getListBusinessApader(){return listBusinessApader;}
}