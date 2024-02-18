package com.kunano.scansell_native.ui.home.business.create_product;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateProductViewModel extends ViewModel {

    private MutableLiveData<Drawable> bitmapImgMutableLiveData;
    private MutableLiveData<Boolean> handleSaveButtonClickLiveData;
    private MutableLiveData<String> warningName;
    private MutableLiveData<String> warningBuyinPrice;
    private MutableLiveData<String> warningSellingPrice;
    private MutableLiveData<String> warningStock;
    private MutableLiveData<Color> buttonColor;
    private MutableLiveData<Integer> cancelImageButtonVisibility;

    private Bitmap bitmapImg;


    public CreateProductViewModel(){
        bitmapImgMutableLiveData = new MutableLiveData<>();
        bitmapImg = null;
        handleSaveButtonClickLiveData = new MutableLiveData<>();
        warningName = new MutableLiveData<>();
        warningBuyinPrice = new MutableLiveData<>();
        warningSellingPrice = new MutableLiveData<>();
        warningStock = new MutableLiveData<>();
        cancelImageButtonVisibility = new MutableLiveData<>(View.GONE);
    }








    public MutableLiveData<Drawable> getBitmapImgMutableLiveData() {
        return bitmapImgMutableLiveData;
    }

    public void setDrawableImgMutableLiveData(Drawable bitmapImgMutableLiveData) {
        this.bitmapImgMutableLiveData.postValue(bitmapImgMutableLiveData);
    }

    public void setBitmapImg(Bitmap bitmapImgMutableLiveData) {
        bitmapImg = bitmapImgMutableLiveData;
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

    public Bitmap getBitmapImg() {
        return bitmapImg;
    }

    public MutableLiveData<Integer> getCancelImageButtonVisibility() {
        return cancelImageButtonVisibility;
    }

    public void setCancelImageButtonVisibility(Integer cancelImageButtonVisibility) {
        this.cancelImageButtonVisibility.postValue(cancelImageButtonVisibility);
    }
}