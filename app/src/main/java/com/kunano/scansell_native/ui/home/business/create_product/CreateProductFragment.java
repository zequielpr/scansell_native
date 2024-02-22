package com.kunano.scansell_native.ui.home.business.create_product;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentCreateProductBinding;
import com.kunano.scansell_native.ui.AdminPermissions;
import com.kunano.scansell_native.ui.AskWhetherDeleteDialog;
import com.kunano.scansell_native.ui.ImageProcessor;
import com.kunano.scansell_native.ui.home.business.BusinessViewModel;
import com.kunano.scansell_native.ui.home.business.create_product.bottom_sheet_image_source.ImageSourceFragment;


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
    private Toolbar createProductToolbar;
    private AskWhetherDeleteDialog askWhetherDeleteDialog;

    private BusinessViewModel businessViewModel;
    private CreateProductViewModel createProductViewModel;
    private ImageProcessor imageProcessor;
    private CreateProductFragment createProductFragment;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Bitmap bitmapImg;
    private MainActivityViewModel mainActivityViewModel;
    private ImageButton cancelImageUploadButton;
    private  ImageSourceFragment imageSourceFragment;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    NavDirections takePictureFragmenttNavDirections;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);

        createProductViewModel = new ViewModelProvider(requireActivity()).get(CreateProductViewModel.class);
        createProductViewModel.setBusinessId(businessViewModel.getCurrentBusinessId());
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
        cancelImageUploadButton = binding.cancelImageUpload;
        createProductToolbar = binding.createProductToolbar;
        createProductFragment = this;
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::loadImageFromFilePath);
        takePictureFragmenttNavDirections = CreateProductFragmentDirections.actionCreateProductFragmentToCaptureImageFragment();

        requestCameraPermissionLauncher = registerForActivityResult(new
                ActivityResultContracts.RequestPermission(), this::resultCameraPermission);

        cancelImageUploadButton.setOnClickListener(this::cancelImageUpload);


        createProductViewModel.getBitmapImgMutableLiveData().observe(getViewLifecycleOwner(),
                imageViewAddImage::setImageDrawable);

        imageViewAddImage.setOnClickListener(this::showOptionPickImage);
        saveButton.setOnClickListener(this::createProduct);

        createProductViewModel.getHandleSaveButtonClickLiveData().observe(getViewLifecycleOwner(), saveButton::setClickable);
        createProductViewModel.getWarningName().observe(getViewLifecycleOwner(), warningName::setText);
        createProductViewModel.getWarningBuyinPrice().observe(getViewLifecycleOwner(), warningBuyinPrice::setText);
        createProductViewModel.getWarningSellingPrice().observe(getViewLifecycleOwner(), warningSellingPrice::setText);
        createProductViewModel.getWarningStock().observe(getViewLifecycleOwner(), warningStock::setText);
        createProductViewModel.getCancelImageButtonVisibility().observe(getViewLifecycleOwner(), cancelImageUploadButton::setVisibility);
        createProductViewModel.getProductNameLiveData().observe(getViewLifecycleOwner(), productName::setText);
        createProductViewModel.getBuyingPriceLivedata().observe(getViewLifecycleOwner(), buyingPrice::setText);
        createProductViewModel.getSellingPriceLiveData().observe(getViewLifecycleOwner(), sellingPrice::setText);
        createProductViewModel.getStockLiveData().observe(getViewLifecycleOwner(), stock::setText);
        createProductViewModel.getButtonSaveTitle().observe(getViewLifecycleOwner(), saveButton::setText);

        imageSourceFragment = new ImageSourceFragment();
        imageSourceFragment.setImageSoucerLisner(new ImageSourceFragment.imageSoucerLisner() {
            @Override
            public void fromFiles(View view) {
                lunchImagePicker();
            }

            @Override
            public void fromCamera(View view) {
                captureImage();
            }
        });


        mainActivityViewModel.setHandleBackPress(this::navigateBack);


        return binding.getRoot();
    }

    public void navigateBack(){
        NavDirections action = CreateProductFragmentDirections.actionCreateProductFragmentToBusinessFragment();
        Navigation.findNavController(getView()).navigate(action);
        mainActivityViewModel.setHandleBackPress(null);

    }


    //Show option to pick image
    public void showOptionPickImage(View view){
        imageSourceFragment.show(getParentFragmentManager(), "pick image options");
    }





    void createProduct(View view){
        if (!validateData()) return;
        imageProcessor = new ImageProcessor(this);
        String name = productName.getText().toString();
        String bPrice = buyingPrice.getText().toString();
        String sPrice = sellingPrice.getText().toString();
        String stck = stock.getText().toString();

        byte[] img = imageProcessor.bitmapToBytes(createProductViewModel.getBitmapImg());

        businessViewModel.createProduct(createProductViewModel.getProductId(),
                name, bPrice, sPrice, stck, "", img, this::recibirRespuesta);
    }

    private void recibirRespuesta(boolean result){
        if (result){
            getActivity().runOnUiThread(this::navigateBack);
            return;
        }

        System.out.println("An error has occured");
    }






    public void lunchImagePicker(){
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }



    private void loadImageFromFilePath(Uri uri) {
        imageSourceFragment.dismiss();
        if(uri == null){
            return;
        }

        imageProcessor = new ImageProcessor(this);

        int imageOrientation = imageProcessor.getOrientation(uri);
        bitmapImg = imageProcessor.decodeUri(uri);
        bitmapImg = imageProcessor.handleOrientation(bitmapImg, imageOrientation);



        if ( bitmapImg != null) {
            // If the decoding was successful, set the Bitmap to the ImageView
            createProductViewModel.setBitmapImg(bitmapImg);
            createProductViewModel.setDrawableImgMutableLiveData(new BitmapDrawable(getResources(), bitmapImg));
            //imageViewAddImage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_shape));
            createProductViewModel.setCancelImageButtonVisibility(View.VISIBLE);
        } else {
            // Handle the case where decoding fails, e.g., show an error image
            imageViewAddImage.setImageResource(R.drawable.close_24);
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


    public void cancelImageUpload(View view){
        Drawable imgAdd = ContextCompat.getDrawable(getContext(), R.drawable.add_image_ic_80dp);
        createProductViewModel.setDrawableImgMutableLiveData(imgAdd);
        createProductViewModel.setBitmapImg(null);
        createProductViewModel.setCancelImageButtonVisibility(View.GONE);
    }



    //Capture image from camera


    public void captureImage(){
        imageSourceFragment.dismiss();

        AdminPermissions adminPermissions = new AdminPermissions(this);
        adminPermissions.setRequestPermissionLauncher(requestCameraPermissionLauncher);
        if (!adminPermissions.verifyCameraPermission()) return;

        //Navigate to camera fragment
        NavDirections navDirections = CreateProductFragmentDirections.actionCreateProductFragmentToCaptureImageFragment();
        Navigation.findNavController(getView()).navigate(navDirections);
    }


    public void resultCameraPermission(boolean result){
        if(!result) return;

        //Navigate to camera fragment
        Navigation.findNavController(getView()).navigate(takePictureFragmenttNavDirections);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createProductViewModel.getProductNameLiveData().observe(getViewLifecycleOwner(), createProductToolbar::setTitle);
        createProductToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        createProductToolbar.setNavigationOnClickListener((v)->navigateBack());
        createProductViewModel.getProductNameLiveData().observe(getViewLifecycleOwner(),this::inflateToolbar);

    }

    private void inflateToolbar(String productName){
        if(!productName.isBlank()){
            createProductToolbar.getMenu().clear();
            createProductToolbar.inflateMenu(R.menu.product_details_tool_bar);
            createProductToolbar.getMenu().findItem(R.id.delete_action).
                    setOnMenuItemClickListener(this::askTosndBin);
        }
    }


    private boolean askTosndBin(MenuItem menuItem){
        askWhetherDeleteDialog = new
                AskWhetherDeleteDialog(getLayoutInflater(), this::deleteOrCancel,
                getString(R.string.send_bin_product));
        askWhetherDeleteDialog.show(getParentFragmentManager(), "Ask to send item to bin");
        return true;
    }

    private void deleteOrCancel(boolean result){
        if (result){
            createProductViewModel.sendProductToBin(this::processResult);


        }else if(askWhetherDeleteDialog != null){
            askWhetherDeleteDialog.dismiss();
        }

    }

    public void processResult(Object result){
        boolean r = (boolean)result;
        getActivity().runOnUiThread(()->{
            if (r){
                Toast.makeText(getContext(), getString(R.string.product_sent_to_bin_successfuly),
                        Toast.LENGTH_LONG).show();
                navigateBack();
                return;
            }
            Toast.makeText(getContext(), getString(R.string.error_to_send_bin_product),
                    Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        createProductViewModel.shotDownExecutors();
    }



}