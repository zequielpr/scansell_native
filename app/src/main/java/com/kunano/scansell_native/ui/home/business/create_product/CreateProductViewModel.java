package com.kunano.scansell_native.ui.home.business.create_product;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateProductViewModel extends ViewModel {

    MutableLiveData<Bitmap> bitmapImgMutableLiveData;

    public CreateProductViewModel(){
        bitmapImgMutableLiveData = new MutableLiveData<>();
    }








    public MutableLiveData<Bitmap> getBitmapImgMutableLiveData() {
        return bitmapImgMutableLiveData;
    }

    public void setBitmapImgMutableLiveData(Bitmap bitmapImgMutableLiveData) {
        this.bitmapImgMutableLiveData.postValue(bitmapImgMutableLiveData);
    }
}