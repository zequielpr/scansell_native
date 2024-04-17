package com.kunano.scansell_native.ui.home.business.create_product.scan_product;

import android.graphics.Bitmap;
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
import com.kunano.scansell_native.databinding.FragmentScannProductCreateBinding;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.custom_camera.CustomCamera;
import com.kunano.scansell_native.ui.home.business.BusinessViewModel;
import com.kunano.scansell_native.ui.home.business.create_product.CreateProductViewModel;


public class ScannProductCreateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match

    PreviewView previewView;
    Button cancelButton;
    ImageButton imageButtonFlash;
    FragmentScannProductCreateBinding binding;
    CustomCamera customCamera;
    private CreateProductViewModel createProductViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private BusinessViewModel businessViewModel;
    private Long  businessKey;
    private View scanningLine;
    private View scanningLineParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            businessKey = getArguments().getLong("business_key");
        }
        // Inflate the layout for this fragment
        binding = FragmentScannProductCreateBinding.inflate(inflater, container, false);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        cancelButton = binding.buttonCancel;
        imageButtonFlash = binding.flashButton;
        previewView = binding.viewFinder;
        scanningLine = binding.scannerLayout.sCanningLine;
        scanningLineParent = binding.scannerLayout.transparentSpot;

        Utils.startAnimationOfScanningLine(this, scanningLine, scanningLineParent);

        customCamera = new CustomCamera(previewView,this, imageButtonFlash);

        customCamera.startCamera(true);
        customCamera.setNewProductInCamera(true);

        customCamera.setCustomCameraListener(new CustomCamera.CustomCameraListener() {
            @Override
            public void receiveImg(Bitmap bitmapImg) {

            }

            @Override
            public void receiveBarCodeData(String barCodeData) {
                receiveBarCodedata(barCodeData);
            }
        });


        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handleBackPress();
                    }
                });
        cancelButton.setOnClickListener(this::navigateBack);






        return binding.getRoot();
    }


    public void handleBackPress(){
        navigateBack(getView());
    }

    public void navigateBack(View view){
        NavDirections navDirections = ScannProductCreateFragmentDirections.
                actionScannProductCreateFragment2ToBusinessFragment2(businessKey);
        Navigation.findNavController(getView()).navigate(navDirections );
    }

    private void receiveBarCodedata(String data){
        try {

           if(!data.isBlank()){
               System.out.println("Result at scan: " + data);
               NavDirections navDirections = ScannProductCreateFragmentDirections.
                       actionScannProductCreateFragment2ToCreateProductFragment2(businessKey, data);

               Navigation.findNavController(getView()).navigate(navDirections);
           }
        }catch (Exception e){

        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();

        if (customCamera != null){
            customCamera.setCustomCameraListener(null);
            customCamera.shutDwonThread();
        }

    }
}