package com.kunano.scansell_native.ui.home.business.create_product;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateProductViewModel extends ViewModel {

    private MutableLiveData<Bitmap> bitmapImgMutableLiveData;
    private MutableLiveData<Boolean> handleSaveButtonClickLiveData;
    private MutableLiveData<String> warningName;
    private MutableLiveData<String> warningBuyinPrice;
    private MutableLiveData<String> warningSellingPrice;
    private MutableLiveData<String> warningStock;
    private MutableLiveData<Color> buttonColor;


    public CreateProductViewModel(){
        bitmapImgMutableLiveData = new MutableLiveData<>();
        handleSaveButtonClickLiveData = new MutableLiveData<>();
        warningName = new MutableLiveData<>();
        warningBuyinPrice = new MutableLiveData<>();
        warningSellingPrice = new MutableLiveData<>();
        warningStock = new MutableLiveData<>();
    }








    public MutableLiveData<Bitmap> getBitmapImgMutableLiveData() {
        return bitmapImgMutableLiveData;
    }

    public void setBitmapImgMutableLiveData(Bitmap bitmapImgMutableLiveData) {
        this.bitmapImgMutableLiveData.postValue(bitmapImgMutableLiveData);
    }

    public MutableLiveData<Boolean> getHandleSaveButtonClickLiveData() {
        return handleSaveButtonClickLiveData;
    }

    public void setHandleSaveButtonClickLiveData(Boolean isClickcable) {
        this.handleSaveButtonClickLiveData.postValue(isClickcable);
    }


    public MutableLiveData<String> getWarningName() {
        return warningName;
    }

    public void setWarningName(String warningName) {
        this.warningName.postValue(warningName);
    }

    public MutableLiveData<String> getWarningBuyinPrice() {
        return warningBuyinPrice;
    }

    public void setWarningBuyinPrice(String warningBuyinPrice) {
        this.warningBuyinPrice.postValue(warningBuyinPrice);
    }

    public MutableLiveData<String> getWarningSellingPrice() {
        return warningSellingPrice;
    }

    public void setWarningSellingPrice(String warningSellingPrice) {
        this.warningSellingPrice.postValue(warningSellingPrice);
    }

    public MutableLiveData<String> getWarningStock() {
        return warningStock;
    }

    public void setWarningStock(String warningStock) {
        this.warningStock.postValue(warningStock);
    }
}