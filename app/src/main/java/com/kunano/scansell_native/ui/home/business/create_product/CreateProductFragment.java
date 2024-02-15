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
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentCreateProductBinding;
import com.kunano.scansell_native.ui.ImageProcessor;
import com.kunano.scansell_native.ui.home.business.BusinessViewModel;


public class CreateProductFragment extends Fragment {

    private FragmentCreateProductBinding binding;
    private CreateProductViewModel mViewModel;
    private EditText productName;
    private EditText buyingPrice;
    private EditText sellingPrice;
    private EditText stock;

    private TextView warningName;
    private TextView warningBuyinPrice;
    private TextView warningSellingPrice;
    private TextView warningStock;
    private ImageView imageViewAddImage;
    private Button saveButton;

    private BusinessViewModel businessViewModel;
    private CreateProductViewModel createProductViewModel;
    private ImageProcessor imageProcessor;
    private CreateProductFragment createProductFragment;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Bitmap bitmapImg;
    private MainActivityViewModel mainActivityViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

        createProductViewModel = new ViewModelProvider(this).get(CreateProductViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);



        binding = FragmentCreateProductBinding.inflate(inflater, container, false);

        productName = binding.editTextTextNombreProduct;
        buyingPrice = binding.editTextNumberDcmBuyingPrice;
        sellingPrice = binding.editTextNumberDcmSellingPrice;
        stock = binding.editTextNumberStock;
        imageViewAddImage = binding.imageButton;
        saveButton = binding.saveButton;
        warningName = binding.textViewNamePrdWarn;
        warningBuyinPrice = binding.textViewBuyingPriceWarn;
        warningSellingPrice = binding.textViewSellingPriceWarn;
        warningStock = binding.textViewStockWarn;
        createProductFragment = this;
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::loadImageFromFilePath);



        createProductViewModel.getBitmapImgMutableLiveData().observe(getViewLifecycleOwner(),
                imageViewAddImage::setImageBitmap);

        imageViewAddImage.setOnClickListener(this::lunchImagePicker);
        saveButton.setOnClickListener(this::createProduct);

        createProductViewModel.getHandleSaveButtonClickLiveData().observe(getViewLifecycleOwner(), saveButton::setClickable);
        createProductViewModel.getWarningName().observe(getViewLifecycleOwner(), warningName::setText);
        createProductViewModel.getWarningBuyinPrice().observe(getViewLifecycleOwner(), warningBuyinPrice::setText);
        createProductViewModel.getWarningSellingPrice().observe(getViewLifecycleOwner(), warningSellingPrice::setText);
        createProductViewModel.getWarningStock().observe(getViewLifecycleOwner(), warningStock::setText);






        mainActivityViewModel.setHandleBackPress(this::navigateBack);


        return binding.getRoot();
    }

    public void navigateBack(){
        NavDirections action = CreateProductFragmentDirections.actionCreateProductFragmentToBusinessFragment();
        Navigation.findNavController(getView()).navigate(action);
        mainActivityViewModel.setHandleBackPress(null);

    }



    void createProduct(View view){
        if (!validateData()) return;
        imageProcessor = new ImageProcessor(this);
        String name = productName.getText().toString();
        String bPrice = buyingPrice.getText().toString();
        String sPrice = sellingPrice.getText().toString();
        String stck = stock.getText().toString();

        byte[] img = imageProcessor.bitmapToBytes(createProductViewModel.getBitmapImgMutableLiveData().getValue());

        businessViewModel.createProduct(name, bPrice, sPrice, stck, "", img, this::recibirRespuesta);
    }

    private void recibirRespuesta(boolean result){
        if (result){
            getActivity().runOnUiThread(this::navigateBack);
            return;
        }

        System.out.println("An error has occured");
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


    //Validate
    private boolean validateData(){
        if (productName.getText().toString().isEmpty()){
            createProductViewModel.setWarningName(getString(R.string.advert_introduce_name));
            return false;
        }

        createProductViewModel.setWarningName(null);

        if(buyingPrice.getText().toString().isEmpty()){
            createProductViewModel.setWarningBuyinPrice(getString(R.string.set_buying_price));
            return false;
        }

        createProductViewModel.setWarningBuyinPrice(null);

        if (sellingPrice.getText().toString().isEmpty()){
            createProductViewModel.setWarningSellingPrice(getString(R.string.set_selling_price));
            return false;
        }

        createProductViewModel.setWarningSellingPrice(null);

        if (stock.getText().toString().isEmpty()){
            createProductViewModel.setWarningStock(getString(R.string.set_stock));
            return false;
        }
        createProductViewModel.setWarningStock(null);

        return true;

    }


}