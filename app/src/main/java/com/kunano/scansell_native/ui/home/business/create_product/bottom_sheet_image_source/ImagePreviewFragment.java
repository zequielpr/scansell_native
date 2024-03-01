package com.kunano.scansell_native.ui.home.business.create_product.bottom_sheet_image_source;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentImagePreviewBinding;
import com.kunano.scansell_native.ui.home.business.create_product.CreateProductViewModel;

public class ImagePreviewFragment extends Fragment {



    private FragmentImagePreviewBinding binding;
    private CreateProductViewModel createProductViewModel;

    private ImageView imageView;

    private Button trayAgainButton;
    private Button saveButton;
    private MainActivityViewModel mainActivityViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImagePreviewBinding.inflate(inflater, container, false);
        createProductViewModel = new ViewModelProvider(requireActivity()).get(CreateProductViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        imageView = binding.imageView;
        trayAgainButton = binding.trayAgainButton;
        saveButton = binding.saveButton;

        imageView.setImageBitmap(createProductViewModel.getBitmapImg());

        mainActivityViewModel.setHandleBackPress(this::handlePackPress);
        trayAgainButton.setOnClickListener(this::navigateBack);
        saveButton.setOnClickListener(this::saveImage);

        return binding.getRoot();
    }


    private void handlePackPress(){
        navigateBack(getView());
    }


    private void navigateBack(View view){
        NavDirections navDirections = ImagePreviewFragmentDirections.actionImagePreviewFragment2ToCaptureImageFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);

        mainActivityViewModel.setHandleBackPress(null);
    }

    private void saveImage(View view){

        int navDirections = R.id.createProductFragment2;
        Navigation.findNavController(getView()).navigate(navDirections);

        createProductViewModel.setDrawableImgMutableLiveData(new BitmapDrawable(getResources(),
                createProductViewModel.getBitmapImg() ));
        createProductViewModel.setCancelImageButtonVisibility(View.VISIBLE);
        mainActivityViewModel.setHandleBackPress(null);
    }

}