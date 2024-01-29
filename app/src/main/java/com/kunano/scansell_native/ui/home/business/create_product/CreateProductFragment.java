package com.kunano.scansell_native.ui.home.business.create_product;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentCreateProductBinding;
import com.kunano.scansell_native.ui.ImageProcessor;
import com.kunano.scansell_native.ui.home.business.BusinessViewModel;


public class CreateProductFragment extends Fragment {

    FragmentCreateProductBinding binding;
    private CreateProductViewModel mViewModel;
    private EditText productName;
    private EditText buyingPrice;
    private EditText sellingPrice;
    private EditText stock;
    private ImageView imageViewAddImage;
    private Button saveButton;

    BusinessViewModel businessViewModel;
    CreateProductViewModel createProductViewModel;
    ImageProcessor imageProcessor;
    CreateProductFragment createProductFragment;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Bitmap bitmapImg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);
        createProductViewModel = new ViewModelProvider(this).get(CreateProductViewModel.class);



        binding = FragmentCreateProductBinding.inflate(inflater, container, false);

        productName = binding.editTextTextNombreProduct;
        buyingPrice = binding.editTextNumberDcmBuyingPrice;
        sellingPrice = binding.editTextNumberDcmSellingPrice;
        stock = binding.editTextNumberStock;
        imageViewAddImage = binding.imageButton;
        saveButton = binding.saveButton;
        createProductFragment = this;
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::loadImageFromFilePath);



        createProductViewModel.getBitmapImgMutableLiveData().observe(getViewLifecycleOwner(),
                imageViewAddImage::setImageBitmap);

        imageViewAddImage.setOnClickListener(this::lunchImagePicker);
        saveButton.setOnClickListener(this::createProduct);



        return binding.getRoot();
    }


    void createProduct(View view){
        imageProcessor = new ImageProcessor(this);
        String name = productName.getText().toString();
        String bPrice = buyingPrice.getText().toString();
        String sPrice = sellingPrice.getText().toString();
        String stck = stock.getText().toString();
        byte[] img = imageProcessor.bitmapToBytes(createProductViewModel.getBitmapImgMutableLiveData().getValue());

        businessViewModel.createProduct(name, bPrice, sPrice, stck, "", img, this::recibirRespuesta);
    }

    private void recibirRespuesta(boolean result){
        System.out.println("Se ha creado correctamente: " +result);
    }






    public void lunchImagePicker(View view){
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }



    private void loadImageFromFilePath(Uri uri) {
        if(uri == null){
            imageViewAddImage.setImageResource(R.drawable.cancel_24);
            return;
        }

        imageProcessor = new ImageProcessor(this);

        int imageOrientation = imageProcessor.getOrientation(uri);
        bitmapImg = imageProcessor.decodeUri(uri);
        bitmapImg = imageProcessor.handleOrientation(bitmapImg, imageOrientation);



        if ( bitmapImg != null) {
            // If the decoding was successful, set the Bitmap to the ImageView
            createProductViewModel.setBitmapImgMutableLiveData(bitmapImg);
        } else {
            // Handle the case where decoding fails, e.g., show an error image
            imageViewAddImage.setImageResource(R.drawable.cancel_24);
        }
    }


}