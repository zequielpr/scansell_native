package com.kunano.scansell_native.ui.home.business.create_product.bottom_sheet_image_source;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.palette.graphics.Palette;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentImagePreviewBinding;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.home.business.create_product.CreateProductViewModel;

public class ImagePreviewFragment extends Fragment {



    private FragmentImagePreviewBinding binding;
    private CreateProductViewModel createProductViewModel;

    private ImageView imageView;

    private Button trayAgainButton;
    private Button saveButton;
    private MainActivityViewModel mainActivityViewModel;
    private View imagePreviewLayout;

    private Palette colorPalette;
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
        imagePreviewLayout = binding.imagePreviewLayout;

        imageView.setImageBitmap(createProductViewModel.getBitmapImg());
        colorPalette = Utils.getColorPaletteFromImage(createProductViewModel.getBitmapImg());
        Integer imageVibrantColor = Utils.getLightVibrantColor(colorPalette);
        if (imageVibrantColor != null){
            imagePreviewLayout.setBackgroundColor(imageVibrantColor);
            Utils. setActionBarColor(getActivity(), imageVibrantColor);
        }

        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handlePackPress();
                    }
                });
        trayAgainButton.setOnClickListener(this::navigateBack);
        saveButton.setOnClickListener(this::saveImage);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    private void handlePackPress(){
        navigateBack(getView());
    }


    private void navigateBack(View view){
        NavDirections navDirections = ImagePreviewFragmentDirections.actionImagePreviewFragment2ToCaptureImageFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);
    }

    private void saveImage(View view){

        int navDirections = R.id.createProductFragment2;
        Navigation.findNavController(getView()).navigate(navDirections);


        createProductViewModel.setBitmapImgMutableLiveData(createProductViewModel.getBitmapImg() );
        createProductViewModel.setCancelImageButtonVisibility(View.VISIBLE);
    }

}