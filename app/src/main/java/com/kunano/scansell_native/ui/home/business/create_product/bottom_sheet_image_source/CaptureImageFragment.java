package com.kunano.scansell_native.ui.home.business.create_product.bottom_sheet_image_source;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.common.util.concurrent.ListenableFuture;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentCaptureImageBinding;
import com.kunano.scansell_native.ui.ImageProcessor;
import com.kunano.scansell_native.ui.home.business.create_product.CreateProductViewModel;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaptureImageFragment extends Fragment {



    private static final String FILENAME_FORMAT = "yyyyMMdd_HHmmss";
    private ImageCapture imageCapture = null;
    private File outputDirectory;
    private ExecutorService cameraExecutor;

    private FragmentCaptureImageBinding binding;

    private PreviewView previewView;

    private CreateProductViewModel createProductViewModel;

    private ImageProcessor imageProcessor;
    private ImageButton cameraCaptureButton;
    private ImageButton imageButtonFlash;
    private MainActivityViewModel mainActivityViewModel;
    private Button cancelButton;
    PorterDuffColorFilter colorFilterWhite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        colorFilterWhite = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        createProductViewModel = new ViewModelProvider(requireActivity()).get(CreateProductViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        imageProcessor = new ImageProcessor(this);
        binding = FragmentCaptureImageBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment




        cameraCaptureButton = binding.cameraCaptureButton;
        cameraCaptureButton.getDrawable().setColorFilter(colorFilterWhite);
        previewView = binding.viewFinder;
        imageButtonFlash = binding.flashButton;
        cancelButton = binding.buttonCancel;
        imageButtonFlash.getDrawable().setColorFilter(colorFilterWhite);

        cameraCaptureButton.setOnClickListener(this::takePhoto);

        cameraExecutor = Executors.newSingleThreadExecutor();


        mainActivityViewModel.setHandleBackPress(this::navigateBack);
        createProductViewModel.getFlashMode().observe(getViewLifecycleOwner(), this::handleFlashIconButton);
        cancelButton.setOnClickListener(this::navigateBack);
        imageButtonFlash.setOnClickListener(this::handleFlash);




        startCamera();

        return binding.getRoot();
    }

    public void navigateBack(){
        NavDirections navDirections = CaptureImageFragmentDirections.actionCaptureImageFragmentToCreateProductFragment();
        Navigation.findNavController(getView()).navigate(navDirections);
        mainActivityViewModel.setHandleBackPress(null);
    }

    public void navigateBack(View view){
        NavDirections navDirections = CaptureImageFragmentDirections.actionCaptureImageFragmentToCreateProductFragment();
        Navigation.findNavController(getView()).navigate(navDirections);
        mainActivityViewModel.setHandleBackPress(null);
    }

    public void handleFlash(View view){
       Integer flashMode = createProductViewModel.getFlashMode().getValue();
       if (flashMode == ImageCapture.FLASH_MODE_ON){
           flashOff();
       }else {
           flashOn();
       }
    }

    public void handleFlashIconButton(Integer flashMode){
        if (flashMode == ImageCapture.FLASH_MODE_ON){
            imageButtonFlash.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.flash_of_24));

        }else {
            imageButtonFlash.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.flash_on_24));
        }

        imageButtonFlash.getDrawable().setColorFilter(colorFilterWhite);
    }

    public void flashOn(){
        createProductViewModel.setFlashMode(ImageCapture.FLASH_MODE_ON);
        System.out.println("flash on");
    }

    public void flashOff(){
        createProductViewModel.setFlashMode(ImageCapture.FLASH_MODE_OFF);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

        if (cameraExecutor == null) return;
        cameraExecutor.shutdown();
    }






    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }

        }, ContextCompat.getMainExecutor(getContext()));
    }


    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        imageCapture = builder.build();
        createProductViewModel.getFlashMode().observe(getViewLifecycleOwner(),imageCapture::setFlashMode);
        //createProductViewModel.getFlashMode().observe(getViewLifecycleOwner(), imageCapture::setFlashMode);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);


    }


    private void takePhoto(View view) {
        String cacheDir = System.getProperty("java.io.tmpdir");
        File file = new File(cacheDir + "xxxxx.jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback () {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults      outputFileResults) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //do whatever you want with the image saved in <file>.

                        Uri uri = outputFileResults.getSavedUri();

                        Bitmap bitmapImg;
                        int imageOrientation = imageProcessor.getOrientation(uri);
                        bitmapImg = imageProcessor.decodeUri(uri);
                        bitmapImg = imageProcessor.handleOrientation(bitmapImg, imageOrientation);
                        createProductViewModel.setBitmapImg(bitmapImg);

                        NavDirections navDirections = CaptureImageFragmentDirections.actionCaptureImageFragmentToImagePreviewFragment();
                        Navigation.findNavController(getView()).navigate(navDirections);

                    }
                });



                System.out.println("Image saved: " + outputFileResults.getSavedUri());
            }
            @Override
            public void onError(@NonNull ImageCaptureException error) {
                System.out.println("Failed to save image");
                error.printStackTrace();
            }
        });
    }

}