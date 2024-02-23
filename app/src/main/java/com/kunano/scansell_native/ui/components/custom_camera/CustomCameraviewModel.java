package com.kunano.scansell_native.ui.components.custom_camera;

import androidx.camera.core.ImageCapture;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CustomCameraviewModel extends ViewModel {
    private MutableLiveData<Integer> flashMode;
    private MutableLiveData<Boolean> newProductInCamera;


    public CustomCameraviewModel() {
        this.flashMode = new MutableLiveData<>(ImageCapture.FLASH_MODE_OFF);
        newProductInCamera = new MutableLiveData<>(false);
    }

    public MutableLiveData<Integer> getFlashMode() {
        return flashMode;
    }

    public void setFlashMode(int flashMode) {
        this.flashMode.postValue(flashMode);
    }


    public MutableLiveData<Boolean> getNewProductInCamera() {
        return newProductInCamera;
    }

    public void setNewProductInCamera(Boolean isNewProductInCamera) {
        this.newProductInCamera.postValue(isNewProductInCamera);
    }
}
