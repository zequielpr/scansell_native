package com.kunano.scansell_native.ui.home.business.create_product.bottom_sheet_image_source;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentCaptureImageBinding;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.custom_camera.CustomCamera;
import com.kunano.scansell_native.ui.home.business.create_product.CreateProductViewModel;

public class CaptureImageFragment extends Fragment {

    private FragmentCaptureImageBinding binding;

    private PreviewView previewView;

    private CreateProductViewModel createProductViewModel;

    private ImageButton cameraCaptureButton;
    private ImageButton imageButtonFlash;
    private MainActivityViewModel mainActivityViewModel;
    private Button cancelButton;
    private PorterDuffColorFilter colorFilterWhite;
    private CustomCamera customCamera;

    public CaptureImageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        colorFilterWhite = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        createProductViewModel = new ViewModelProvider(requireActivity()).get(CreateProductViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        binding = FragmentCaptureImageBinding.inflate(inflater, container, false);


        cameraCaptureButton = binding.cameraCaptureButton;
        cameraCaptureButton.getDrawable().setColorFilter(colorFilterWhite);
        previewView = binding.viewFinder;
        imageButtonFlash = binding.flashButton;
        cancelButton = binding.buttonCancel;
        imageButtonFlash.getDrawable().setColorFilter(colorFilterWhite);

        //Take image process
        customCamera = new CustomCamera(previewView, this, imageButtonFlash);
        customCamera.startCamera(false);
        cameraCaptureButton.setOnClickListener(this::takePhoto);
        customCamera.setCustomCameraListener(new CustomCamera.CustomCameraListener() {
            @Override
            public void receiveImg(Bitmap bitmapImg) {
                receiveBitmapImg(bitmapImg);
            }

            @Override
            public void receiveBarCodeData(String barCodeData) {

            }
        });


        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        handleBackPress();
                    }
                });
        cancelButton.setOnClickListener(this::navigateBack);




        return binding.getRoot();
    }

    public void takePhoto(View view){
        cameraCaptureButton.setClickable(false);
        customCamera.takePhoto(getView());
    }

    public void handleBackPress(){
        navigateBack(getView());
    }

    public void navigateBack(View view){
        int navDirections = R.id.createProductFragment2;
        Navigation.findNavController(getView()).navigate(navDirections);
    }

    private void receiveBitmapImg(Bitmap bitmapImg){
        if (bitmapImg == null) return;

        createProductViewModel.setBitmapImg(bitmapImg);

        //navigate to create product screen
        getActivity().runOnUiThread(this::navigateToPreviewImage);
    }

    private void navigateToPreviewImage(){
        NavDirections navDirections = CaptureImageFragmentDirections.actionCaptureImageFragment2ToImagePreviewFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() == null) return;
        Utils.setActionBarColor(getActivity(), Color.TRANSPARENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(customCamera != null)customCamera.shutDwonThread();

    }

}