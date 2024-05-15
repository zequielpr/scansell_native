package com.kunano.scansell_native.ui.home.business.create_product;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
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

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.palette.graphics.Palette;

import com.google.android.material.textfield.TextInputLayout;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentCreateProductBinding;
import com.kunano.scansell_native.components.AdminPermissions;
import com.kunano.scansell_native.components.AskForActionDialog;
import com.kunano.scansell_native.components.ImageProcessor;
import com.kunano.scansell_native.components.Utils;
import com.kunano.scansell_native.components.ViewModelListener;
import com.kunano.scansell_native.components.media_picker.CustomMediaPicker;
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
    private AskForActionDialog askWhetherDeleteDialog;
    private CreateProductViewModel createProductViewModel;
    private ImageProcessor imageProcessor;
    private CreateProductFragment createProductFragment;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Bitmap bitmapImg;
    private MainActivityViewModel mainActivityViewModel;
    private ImageButton cancelImageUploadButton;
    private  ImageSourceFragment imageSourceFragment;
    NavDirections takePictureFragmenttNavDirections;
    private NavController navController;

    private Long businessKey;
    private String productId;
    private CustomMediaPicker customMediaPicker;
    private View imageButtonFrame;
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout buyingPriceTextInputLayout;
    private TextInputLayout sellingPriceTextInputLayout;
    private TextInputLayout stockTextInputLayout;

    private  AdminPermissions adminPermissions;

    public CreateProductFragment() {
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminPermissions = new AdminPermissions(this);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        createProductViewModel = new ViewModelProvider(requireActivity()).get(CreateProductViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        if (getArguments() != null) {
            businessKey = getArguments().getLong("business_key");
            productId = getArguments().getString("product_key");


            if(businessKey != null && productId != null){
                createProductViewModel.setBusinessId(businessKey);
                createProductViewModel.setProductId(productId);
                createProductViewModel.checkIfProductExists(productId);
            }
        }





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
        imageButtonFrame = binding.imageButtonFrame;
        nameTextInputLayout = binding.nameFilledTextField;
        buyingPriceTextInputLayout = binding.buyPriceFilledTextField;
        sellingPriceTextInputLayout = binding.sellPriceFilledTextField;
        stockTextInputLayout = binding.stockFilledTextField;




        createProductFragment = this;
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::loadImageFromFilePath);
        customMediaPicker = new CustomMediaPicker(pickMedia);


        cancelImageUploadButton.setOnClickListener(this::cancelImageUpload);


        createProductViewModel.getBitmapImgMutableLiveData().observe(getViewLifecycleOwner(),
                imageViewAddImage::setImageBitmap);

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


        // This callback will only be called when MyFragment is at least Started.


        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                       navigateBack();
                    }
                });
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createProductViewModel.getProductNameLiveData().observe(getViewLifecycleOwner(), createProductToolbar::setTitle);
        createProductToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        createProductToolbar.setNavigationOnClickListener((v)->navigateBack());
        createProductViewModel.getProductNameLiveData().observe(getViewLifecycleOwner(),this::inflateToolbar);
        createProductViewModel.getBarSubtitle().observe(getViewLifecycleOwner(), createProductToolbar::setSubtitle);
        createProductViewModel.getBitmapImgMutableLiveData().observe(getViewLifecycleOwner(),
                this::setToolBarColor);

    }

    private void setToolBarColor(Bitmap bitmapImg){
        if (getActivity() == null || bitmapImg == null) return;
        Palette palette = Utils.getColorPaletteFromImage(bitmapImg);
        Integer brightColor = Utils.getVibrantColor(palette);
        if (brightColor == null) return;
        int [] colors = {brightColor, Color.TRANSPARENT};

        GradientDrawable gradientColor = Utils.getGradientColor(colors, GradientDrawable.RECTANGLE,
                GradientDrawable.Orientation.TOP_BOTTOM, 0f, GradientDrawable.LINEAR_GRADIENT);


        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.rounded_button_background);
        drawable.setColorFilter(brightColor, PorterDuff.Mode.SRC_IN);

        imageButtonFrame.setBackground(drawable);
        createProductToolbar.setBackground(gradientColor);
        Utils.setActionBarColor(getActivity(), brightColor);
        saveButton.setBackgroundColor(brightColor);

        nameTextInputLayout.setBoxStrokeColor(brightColor);
        buyingPriceTextInputLayout.setBoxStrokeColor(brightColor);
        sellingPriceTextInputLayout.setBoxStrokeColor(brightColor);
        stockTextInputLayout.setBoxStrokeColor(brightColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ColorStateList cursorColor = ColorStateList.valueOf(brightColor);
            nameTextInputLayout.setCursorColor(cursorColor);
            buyingPriceTextInputLayout.setCursorColor(cursorColor);
            sellingPriceTextInputLayout.setCursorColor(cursorColor);
            stockTextInputLayout.setCursorColor(cursorColor);

            nameTextInputLayout.setHintTextColor(cursorColor);
            buyingPriceTextInputLayout.setHintTextColor(cursorColor);
            sellingPriceTextInputLayout.setHintTextColor(cursorColor);
            stockTextInputLayout.setHintTextColor(cursorColor);
        }

    }


    public void navigateBack(){
      navController = Navigation.findNavController(getView());
      NavDirections navDirections = CreateProductFragmentDirections.
              actionCreateProductFragment2ToBusinessFragment2(createProductViewModel.getBusinessId());

      navController.navigate(navDirections);
    }



    //Show option to pick image
    public void showOptionPickImage(View view){
        imageSourceFragment.setImageSoucerLisner(new ImageSourceFragment.imageSoucerLisner() {
            @Override
            public void fromFiles(View view) {
                adminPermissions.setResultListener(new ViewModelListener<Boolean>() {
                    @Override
                    public void result(Boolean object) {
                        if (object){
                            lunchImagePicker();
                        }else {
                            imageSourceFragment.dismiss();
                        }
                    }
                });
                adminPermissions.checkMediaPermission();
            }

            @Override
            public void fromCamera(View view) {

                adminPermissions.setResultListener(new ViewModelListener<Boolean>() {
                    @Override
                    public void result(Boolean object) {
                        if (object){
                            captureImage();
                        }else {
                            imageSourceFragment.dismiss();
                        }
                    }
                });
                adminPermissions.checkCameraPermission();

            }
        });
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

        if (createProductViewModel.isProductToUpdate()){
            createProductViewModel.updateProduct(createProductViewModel.getProductId(),
                    name, bPrice, sPrice, stck, "", img, this::recibirRespuesta);
        }else {
            createProductViewModel.createProduct(createProductViewModel.getProductId(),
                    name, bPrice, sPrice, stck, "", img, this::recibirRespuesta);
        }

    }

    private void recibirRespuesta(boolean result){
        if (result){
            getActivity().runOnUiThread(this::navigateBack);
            return;
        }

        System.out.println("An error has occured");
    }






    public void lunchImagePicker(){
        customMediaPicker.lunchImagePicker(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE);
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
            createProductViewModel.setBitmapImgMutableLiveData(bitmapImg);
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
            createProductViewModel.setWarningName(getString(R.string.introduce_name));
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
        createProductViewModel.setBitmapImgMutableLiveData(ImageProcessor.parseDrawbleToBitmap(imgAdd));
        createProductViewModel.setBitmapImg(null);
        createProductViewModel.setCancelImageButtonVisibility(View.GONE);
    }



    //Capture image from camera


    public void captureImage(){
        imageSourceFragment.dismiss();



        //Navigate to camera fragment
        takePictureFragmenttNavDirections = CreateProductFragmentDirections.actionCreateProductFragment2ToCaptureImageFragment2();
        Navigation.findNavController(getView()).navigate(takePictureFragmenttNavDirections);
    }


    public void resultCameraPermission(boolean result){
        if(!result) return;

        //Navigate to camera fragment
        Navigation.findNavController(getView()).navigate(takePictureFragmenttNavDirections);
    }




    private void inflateToolbar(String productName){
        if(!productName.isBlank()){
            createProductToolbar.getMenu().clear();
            createProductToolbar.inflateMenu(R.menu.product_details_tool_bar);
            createProductToolbar.getMenu().findItem(R.id.delete_action).
                    setOnMenuItemClickListener(this::askTosndBin);
        }else {
            createProductToolbar.getMenu().clear();
        }
    }


    private boolean askTosndBin(MenuItem menuItem){
        askWhetherDeleteDialog = new
                AskForActionDialog(getString(R.string.send_bin_product));
        askWhetherDeleteDialog.setButtonListener(this::deleteOrCancel);
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
                Toast.makeText(getContext(), getString(R.string.products_sent_to_bin_successfully),
                        Toast.LENGTH_LONG).show();
                navigateBack();
                return;
            }
            Toast.makeText(getContext(), getString(R.string.error_to_send_bin_product),
                    Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("On resume");
        setToolBarColor(createProductViewModel.getBitmapImgMutableLiveData().getValue());
        if (createProductViewModel == null)return;
        productName.setText(createProductViewModel.getProductName());
        buyingPrice.setText(createProductViewModel.getBuyPrice());
        sellingPrice.setText(createProductViewModel.getSellPrice());
        stock.setText(createProductViewModel.getStock());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (createProductViewModel == null)return;
        createProductViewModel.setProductName(productName.getText().toString());
        createProductViewModel.setSellPrice(sellingPrice.getText().toString());
        createProductViewModel.setBuyPrice(buyingPrice.getText().toString());
        createProductViewModel.setStock(stock.getText().toString());
        if (getActivity() == null) return;
        Utils.setActionBarColor(getActivity(), Color.TRANSPARENT);
        createProductToolbar.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (createProductViewModel == null)return;
        createProductViewModel.shotDownExecutors();
        System.out.println("on destroy");
    }



}