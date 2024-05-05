package com.kunano.scansell_native.ui.components.custom_camera;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CustomCameraviewModel extends ViewModel {
    private MutableLiveData<Integer> flashMode;
    private MutableLiveData<Boolean> newProductInCamera;
    private MutableLiveData<Boolean> torchState;
    private Integer lenFace;


    public CustomCameraviewModel() {
        this.flashMode = new MutableLiveData<>(ImageCapture.FLASH_MODE_OFF);
        newProductInCamera = new MutableLiveData<>(false);
        torchState = new MutableLiveData<>(false);
        lenFace = CameraSelector.LENS_FACING_BACK;
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

    public MutableLiveData<Boolean> getTorchState() {
        return torchState;
    }

    public void setTorchState(Boolean torchState) {
        this.torchState.postValue(torchState);
    }

    public Integer getLenFace() {
        return lenFace;
    }

    public void setLenFace(Integer lenFace) {
        this.lenFace = lenFace;
    }
}
