package com.kunano.scansell_native.ui.components;

import androidx.camera.core.ImageCapture;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CustomCameraviewModel extends ViewModel {
    private MutableLiveData<Integer> flashMode;


    public CustomCameraviewModel() {
        this.flashMode = new MutableLiveData<>(ImageCapture.FLASH_MODE_OFF);
    }

    public MutableLiveData<Integer> getFlashMode() {
        return flashMode;
    }

    public void setFlashMode(int flashMode) {
        this.flashMode.postValue(flashMode);
    }
}
