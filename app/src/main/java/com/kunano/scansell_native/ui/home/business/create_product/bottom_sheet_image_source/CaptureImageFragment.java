package com.kunano.scansell_native.ui.home.business.create_product.bottom_sheet_image_source;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.databinding.FragmentCaptureImageBinding;
import com.kunano.scansell_native.ui.components.CustomCamera;
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
        customCamera.startCamera();
        cameraCaptureButton.setOnClickListener(customCamera::takePhoto);
        customCamera.setCustomCameraListener(this::receiveBitmapImg);


        mainActivityViewModel.setHandleBackPress(this::navigateBack);
        cancelButton.setOnClickListener(this::navigateBack);




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

    private void receiveBitmapImg(Bitmap bitmapImg){
        createProductViewModel.setBitmapImg(bitmapImg);
        createProductViewModel.setDrawableImgMutableLiveData(new BitmapDrawable(getResources(), bitmapImg));

        //navigate to create product screen
        getActivity().runOnUiThread(this::navigateToPreviewImage);
    }

    private void navigateToPreviewImage(){
        NavDirections navDirections = CaptureImageFragmentDirections.actionCaptureImageFragmentToImagePreviewFragment();
        Navigation.findNavController(getView()).navigate(navDirections);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        customCamera.shutDwonThread();

    }

}