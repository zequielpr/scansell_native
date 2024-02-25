package com.kunano.scansell_native.ui.components.custom_camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.util.Size;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.ui.ImageProcessor;
import com.kunano.scansell_native.ui.components.BarcodeScannerCustom;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomCamera {
    private Fragment fragment;
    private Context context;
    private PreviewView previewView;
    private Activity activity;

    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private CustomCameraviewModel customCameraviewModel;
    private PorterDuffColorFilter colorFilterWhite;
    private ImageButton imageButtonFlash;
    private CustomCameraListener customCameraListener;
    boolean scanBarCode;

    Camera camera;

    public CustomCamera(PreviewView previewView, Fragment fragment, ImageButton imageButtonFlash) {
        this.previewView = previewView;
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.activity = fragment.getActivity();
        customCameraviewModel = new ViewModelProvider(fragment).get(CustomCameraviewModel.class);
        colorFilterWhite = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        this.imageButtonFlash = imageButtonFlash;
        boolean scanBarCode = false;


        imageButtonFlash.setOnClickListener(this::handleFlash);


        customCameraviewModel.getFlashMode().observe(fragment.getViewLifecycleOwner(), this::handleFlashIconButton);
        customCameraviewModel.getTorchState().observe(fragment.getViewLifecycleOwner(), this::handleTorchconButton);



    }

    public void shutDwonThread(){
        if (cameraExecutor == null) return;
        cameraExecutor.shutdown();
    }


    public void startCamera(boolean scanBarCode) {
        this.scanBarCode = scanBarCode;
        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);



        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, scanBarCode);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }

        }, ContextCompat.getMainExecutor(context));
    }


    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, boolean scanBarCode) {
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis;
        if(scanBarCode){
             imageAnalysis = new ImageAnalysis.Builder().
                    setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).
                    setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888).
                    setTargetResolution(new Size(1600, 1200)).build();

            BarcodeScannerCustom barcodeScannerCustom = new BarcodeScannerCustom();
            barcodeScannerCustom.setBarcodeScannerCustomListenner(customCameraListener::receiveBarCodeData);
            customCameraviewModel.getNewProductInCamera().observe(fragment.getViewLifecycleOwner(),
                    barcodeScannerCustom::setNewObjectInCamera);
            imageAnalysis.setAnalyzer(cameraExecutor, barcodeScannerCustom);
        }else {
            imageAnalysis = new ImageAnalysis.Builder().
                    setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        }



        ImageCapture.Builder builder = new ImageCapture.Builder();

        imageCapture = builder.build();




      try {
          cameraProvider.unbindAll();
          camera = cameraProvider.
                  bindToLifecycle(fragment.getViewLifecycleOwner(), cameraSelector, preview, imageCapture, imageAnalysis);


          customCameraviewModel.getTorchState().observe(fragment.getViewLifecycleOwner(),
                  (torchState)->{
              System.out.println("Torch state: " + torchState);
                      camera.getCameraControl().enableTorch(torchState);
                  });
      }catch (Exception e){
          e.printStackTrace();
      }
        customCameraviewModel.getFlashMode().observe(fragment.getViewLifecycleOwner(), imageCapture::setFlashMode);

    }




    public void takePhoto(View view) {
        ImageProcessor imageProcessor = new ImageProcessor(fragment);

        String cacheDir = System.getProperty("java.io.tmpdir");
        File file = new File(cacheDir + "xxxxx.jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri uri = outputFileResults.getSavedUri();
                Bitmap btmpImg;
                int imageOrientation = imageProcessor.getOrientation(uri);
                btmpImg = imageProcessor.decodeUri(uri);
                btmpImg = imageProcessor.handleOrientation(btmpImg, imageOrientation);

                //Send image by the callback
                customCameraListener.receiveImg(btmpImg);

            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                customCameraListener.receiveImg(null);
                System.out.println("Failed to save image");
                error.printStackTrace();
            }
        });

    }


    private void handleFlash(View view) {
        Integer flashMode = customCameraviewModel.getFlashMode().getValue();
        boolean torchState = customCameraviewModel.getTorchState().getValue();
        if (flashMode == ImageCapture.FLASH_MODE_ON | torchState) {
            flashOff();
        } else {
            flashOn();
        }
    }


    private void handleFlashIconButton(Integer flashMode) {
        if (flashMode == ImageCapture.FLASH_MODE_ON) {
            imageButtonFlash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.flash_of_24));

        } else {
            imageButtonFlash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.flash_on_24));
        }

        imageButtonFlash.getDrawable().setColorFilter(colorFilterWhite);
    }

    private void handleTorchconButton(Boolean torchState) {
        if (torchState) {
            imageButtonFlash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.torch_off_24));

        } else {
            imageButtonFlash.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.torch_24));
        }

        imageButtonFlash.getDrawable().setColorFilter(colorFilterWhite);
    }

    private void flashOn() {
        if (scanBarCode){
            customCameraviewModel.setTorchState(true);
            return;
        }
        customCameraviewModel.setFlashMode(ImageCapture.FLASH_MODE_ON);
    }

    private void flashOff() {
        if (scanBarCode && camera.getCameraInfo().hasFlashUnit()){
            customCameraviewModel.setTorchState(false);
            return;
        }
        customCameraviewModel.setFlashMode(ImageCapture.FLASH_MODE_OFF);
    }




    public interface CustomCameraListener{
        abstract void receiveImg(Bitmap bitmapImg);

        abstract void receiveBarCodeData(String barCodeData);
    }


    public void setNewProductInCamera(Boolean isNewProductInCamera){
        customCameraviewModel.setNewProductInCamera(isNewProductInCamera);
    }





    //Getters and setters---------------------------------------------------------------------------

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PreviewView getPreviewView() {
        return previewView;
    }

    public void setPreviewView(PreviewView previewView) {
        this.previewView = previewView;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ImageCapture getImageCapture() {
        return imageCapture;
    }

    public void setImageCapture(ImageCapture imageCapture) {
        this.imageCapture = imageCapture;
    }

    public ListenableFuture<ProcessCameraProvider> getCameraProviderFuture() {
        return cameraProviderFuture;
    }

    public void setCameraProviderFuture(ListenableFuture<ProcessCameraProvider> cameraProviderFuture) {
        this.cameraProviderFuture = cameraProviderFuture;
    }

    public ExecutorService getCameraExecutor() {
        return cameraExecutor;
    }

    public void setCameraExecutor(ExecutorService cameraExecutor) {
        this.cameraExecutor = cameraExecutor;
    }

    public CustomCameraviewModel getCustomCameraviewModel() {
        return customCameraviewModel;
    }

    public void setCustomCameraviewModel(CustomCameraviewModel customCameraviewModel) {
        this.customCameraviewModel = customCameraviewModel;
    }

    public PorterDuffColorFilter getColorFilterWhite() {
        return colorFilterWhite;
    }

    public void setColorFilterWhite(PorterDuffColorFilter colorFilterWhite) {
        this.colorFilterWhite = colorFilterWhite;
    }

    public ImageButton getImageButtonFlash() {
        return imageButtonFlash;
    }

    public void setImageButtonFlash(ImageButton imageButtonFlash) {
        this.imageButtonFlash = imageButtonFlash;
    }

    public CustomCameraListener getCustomCameraListener() {
        return customCameraListener;
    }

    public void setCustomCameraListener(CustomCameraListener customCameraListener) {
        this.customCameraListener = customCameraListener;
    }
}
