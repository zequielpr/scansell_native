package com.kunano.scansell_native.ui.sell;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.SellFragmentBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.SharePreferenceHelper;
import com.kunano.scansell_native.repository.share_preference.SettingRepository;
import com.kunano.scansell_native.ui.components.AdminPermissions;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.components.custom_camera.CustomCamera;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragmentCreateBusiness;
import com.kunano.scansell_native.ui.sell.adapters.BusinessSpinnerAdapter;
import com.kunano.scansell_native.ui.sell.adapters.ProductToSellAdapter;
import com.kunano.scansell_native.ui.sell.collect_payment_method.CollectPaymentMethodFragment;

import java.util.ArrayList;
import java.util.List;

public class SellFragment extends Fragment {


    private SellFragmentBinding binding;
    private Spinner spinner;
    private TextView totalTextView;
    private Toolbar toolbar;
    private PreviewView previewView;
    private ImageButton torchButton;
    private Button finishButton;
    private RecyclerView recyclerViewProducts;
    private CustomCamera customCamera;
    private SellViewModel sellViewModel;
    private ProductToSellAdapter productToSellAdapter;
    BusinessSpinnerAdapter spinerAdapter;
    private ImageButton imageButtonScan;
    private MainActivityViewModel mainActivityViewModel;
    private MediaPlayer mediaPlayer;
    private View sellProductView;
    private View createBusinessView;
    private ImageButton createNewBusinessImgButton;
    private ImageButton cancelSellButton;
    private HandleBootomSheetBehavior handleBootomSheetBehavior;
    private TextView itemsTotalTextView;

    private View showBottomSheetViewBar;
    private View topSide;
    private View scanningLine;
    private View scanLineParentContainer;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private  AdminPermissions adminPermissions;
    private ImageButton switchCamera;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminPermissions = new AdminPermissions(this);
        adminPermissions.setResultListener(this::navigateToHome);


    }

    public void onStart(){
        super.onStart();
        adminPermissions.checkCameraPermission();
    }

    private void navigateToHome(Boolean result){
       if (!result){
           PendingIntent pendingIntent = new NavDeepLinkBuilder(getContext())
                   .setGraph(R.navigation.mobile_navigation).
                   setDestination(R.id.home_navigation_graph)
                   .createPendingIntent();


           try {
               // This will execute the PendingIntent
               pendingIntent.send();
           } catch (PendingIntent.CanceledException e) {
               // Handle error if PendingIntent is canceled
               e.printStackTrace();
           }catch (Exception e){

           }
       }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



         sellViewModel =
                new ViewModelProvider(requireActivity()).get(SellViewModel.class);


         mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        binding = SellFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        spinner = binding.spinnerPickBusiness;
        totalTextView = binding.totalTextView;
        toolbar = binding.sellToolbar;
        previewView = binding.sellCameraPreview;
        finishButton = binding.finishButton;
        recyclerViewProducts = binding.recycleViewProductsToSell;
        torchButton = binding.torchButton;
        imageButtonScan = binding.imageButtonScanProduct;
        sellProductView = binding.sellProductsView;
        createBusinessView = binding.createNewBusinessView.createNewBusinessView;
        createNewBusinessImgButton = binding.createNewBusinessView.createNewBusinessImgButton;
        cancelSellButton = binding.cancelSellButton;
        showBottomSheetViewBar = binding.showBottomSheetView;
        itemsTotalTextView = binding.itemsTotalTextView;
        scanningLine = binding.scanLayout.sCanningLine;
        scanLineParentContainer = binding.scanLayout.transparentSpot;
        switchCamera = binding.switchCamera;
        topSide = binding.bottomSheetTopSide;
                handleBootomSheetBehavior = new HandleBootomSheetBehavior(binding.productToSellBottomSheet);
        handleBootomSheetBehavior.setupStandardBottomSheet(false);
        handleBootomSheetBehavior.setListener(getBottomSheetListener());



        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productToSellAdapter = new ProductToSellAdapter();
        recyclerViewProducts.setAdapter(productToSellAdapter);


        sellViewModel.getTotalToPay().observe(getViewLifecycleOwner(), (t)->{
            totalTextView.setText(String.valueOf(t));
        });

        productToSellAdapter.setActivityParent(getActivity());
        setCardListener();

        spinerAdapter = new BusinessSpinnerAdapter(getContext(),
                R.layout.custom_item_spinner, new ArrayList<>(), Color.WHITE);


        //Set business in adapter and get the current business id
        sellViewModel.getBusinessesListLiveData().observe(getViewLifecycleOwner(), (listB)->{
            spinerAdapter.clear();
            spinerAdapter.addAll(listB);
            try {
                sellViewModel.setCurrentBusinessId(spinerAdapter.getItem(0).getBusinessId());
            }catch (Exception e){
                System.out.println(e.fillInStackTrace());
                sellViewModel.setCurrentBusinessId(null);
            }

        });

        sellViewModel.getProductToSellMutableLiveData().observe(getViewLifecycleOwner(),productToSellAdapter::submitList);






        spinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Business selectedBusiness = (Business) parent.getItemAtPosition(position);
                    sellViewModel.setCurrentBusinessId(selectedBusiness.getBusinessId());
                    sellViewModel.setSelectedIndexSpinner(position);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });



        spinner.setAdapter(spinerAdapter);

        customCamera = new CustomCamera(previewView, this, torchButton);

        customCamera.startCamera(true);

        customCamera.setCustomCameraListener(new CustomCamera.CustomCameraListener() {
            @Override
            public void receiveImg(Bitmap bitmapImg) {

            }

            @Override
            public void receiveBarCodeData(String barCodeData) {
                if(!barCodeData.isBlank()){

                    sellViewModel.requestProduct(barCodeData, (r)->processProductRequest(r, barCodeData));
                }
            }
        });


        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                    }
                });



        return root;
    }

    public void onViewCreated(  @NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                System.out.println("holaaaa");
                Utils.askToLeaveApp(SellFragment.this);

            }
        });

        //Buttons linkings
        imageButtonScan.setOnClickListener(this::scanNewProduct);
        finishButton.setOnClickListener(this::finish);
        cancelSellButton.setOnClickListener(this::cancelSell);
        switchCamera.setOnClickListener(this::switchCamera);

        sellViewModel.getFinishButtonStateVisibility().observe(getViewLifecycleOwner(), finishButton::setVisibility);
        sellViewModel.getFinishButtonStateVisibility().observe(getViewLifecycleOwner(), cancelSellButton::setVisibility);
        sellViewModel.getFinishButtonStateVisibility().observe(getViewLifecycleOwner(), (v)->{
            spinner.setEnabled(v == View.VISIBLE?false:true);
        });
        sellViewModel.isScanActiveMutableLiveData().observe(getViewLifecycleOwner(), this::handleScanLineAnim);
        sellViewModel.getSelectedIndexSpinner().observe(getViewLifecycleOwner(), spinner::setSelection);
        sellViewModel.getTotalItemsSellMutableLIveData().observe(getViewLifecycleOwner(), itemsTotalTextView::setText);

        createNewBusinessImgButton.setOnClickListener(this::createNewBusiness);

        sellViewModel.getSellProductsVisibilityMD().observe(getViewLifecycleOwner(),
                sellProductView::setVisibility);
        sellViewModel.getSellProductsVisibilityMD().observe(getViewLifecycleOwner(),
                imageButtonScan::setVisibility);
        sellViewModel.getCreateNewBusinessVisibilityMD().observe(getViewLifecycleOwner(),
                createBusinessView::setVisibility);
        sellViewModel.getBusinessesListLiveData().observe(getViewLifecycleOwner(),this::handleViewsVisibilities);

        toolbar.inflateMenu(R.menu.sell_tool_bar);
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.got_to_receipt);
        menuItem.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.got_to_receipt){
                    if (sellViewModel.getCurrentBusinessId() != null)navigateToReceipt();
                    return true;
                }
                return false;
            }
        });
    }


    private void handleScanLineAnim(Boolean isActive){
       getActivity().runOnUiThread(()->{
           customCamera.setNewProductInCamera(isActive);
           if (isActive){
               Utils.startAnimationOfScanningLine(this, scanningLine, scanLineParentContainer);
           }else {
               Utils.finishScanningLineAnim(scanningLine);
           }
       });
    }


    private void switchCamera(View view){
        if (customCamera.getLenFace() == CameraSelector.LENS_FACING_FRONT){
            customCamera.setLenFace(CameraSelector.LENS_FACING_BACK);
        }else {
            customCamera.setLenFace(CameraSelector.LENS_FACING_FRONT);
        }
        customCamera.startCamera(true);
    }


    private void createNewBusiness(View view){
        BottomSheetFragmentCreateBusiness createBusiness = new BottomSheetFragmentCreateBusiness();
        createBusiness.setRequestResult(this::processCreateBusinessRequest);
        createBusiness.show(getParentFragmentManager(), BottomSheetFragmentCreateBusiness.TAG);
    }

    private void processCreateBusinessRequest(boolean result){

    }

    private void handleViewsVisibilities(List<Business> l){
        System.out.println("business list: " + l.size());
        if (l.size() > 0){
            sellViewModel.setSellProductsVisibilityMD(View.VISIBLE);
            sellViewModel.setCreateNewBusinessVisibilityMD(View.GONE);
        }else {
            sellViewModel.setSellProductsVisibilityMD(View.GONE);
            sellViewModel.setCreateNewBusinessVisibilityMD(View.VISIBLE);
        }
    }


    private void navigateToReceipt(){
        NavDirections navDirections = SellFragmentDirections.actionSellFragmentToReceiptsFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);
    }



    private void scanNewProduct(View v){
        sellViewModel.setIsScanActiveMutableLiveData(true);
    }


    private void finish(View view){
        CollectPaymentMethodFragment collectPaymentMethodFragment;
        collectPaymentMethodFragment = new CollectPaymentMethodFragment(sellViewModel, getView(), getActivity());
        collectPaymentMethodFragment.show(getParentFragmentManager(), "collect_payment_method");
    }

    private void cancelSell(View view){
        AskForActionDialog askToCancelSell = new AskForActionDialog(getString(R.string.cancel_sell));
        askToCancelSell.setButtonListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object){
                    sellViewModel.clearProductsToSell();
                    handleBootomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                };
            }
        });

        askToCancelSell.show(getParentFragmentManager(), getString(R.string.cancel));
    }


    public void processProductRequest(Product product, String barcode){
        sellViewModel.setIsScanActiveMutableLiveData(false);
        if(product == null && sellViewModel.getCurrentBusinessId() != null){
            //Ask to add product
           getActivity().runOnUiThread(()->askCreateNewProdOrTryAgain(barcode));
            return;
        }else if(!sellViewModel.isStockEnough(product)){
            getActivity().runOnUiThread(()->askToUpdateStock(barcode));
            return;
        }

        sellViewModel.addProductToSell(product);
        makeSound();
        expandBottomSheet();
        getActivity().runOnUiThread(()->recyclerViewProducts.scrollToPosition(0));



    }

    private void expandBottomSheet(){
        if (handleBootomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            handleBootomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }
    }

    public BottomSheetBehavior.BottomSheetCallback getBottomSheetListener(){
        return new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        System.out.println("Collapsed");
                        toolbar.setVisibility(View.VISIBLE);
                        showBottomSheetViewBar.setVisibility(View.VISIBLE);
                        topSide.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        System.out.println("half_Expanded");
                        toolbar.setVisibility(View.VISIBLE);
                        showBottomSheetViewBar.setVisibility(View.VISIBLE);
                        topSide.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        System.out.println("expanded");
                        toolbar.setVisibility(View.GONE);
                        topSide.setVisibility(View.GONE);
                        showBottomSheetViewBar.setVisibility(View.GONE);
                        break;
                    default:

                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };
    }

    private void makeSound(){

        SharePreferenceHelper sharePreferenceHelper =
                new SharePreferenceHelper(getActivity(), Context.MODE_PRIVATE);
        boolean isSoundActive = sharePreferenceHelper.isSoundactive();

        if (!isSoundActive)return;

        Integer sound = sharePreferenceHelper.getSound();

        if (sound == SettingRepository.VIBRATION_SOUND){
            vibrate();

        }else {
            beep();
        }

    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null) {
            vibrator.vibrate(250);
        }
    }


    private void beep(){
       mediaPlayer = MediaPlayer.create(getContext(), R.raw.beep);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    public void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void askToUpdateStock(String barcode){
        AskForActionDialog askForActionDialog = new AskForActionDialog(
                getString(R.string.out_of_stock), getString(R.string.tray_again),
                getString(R.string.update_stock));
        askForActionDialog.setButtonListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object){
                    navigateToCreateProduct(barcode);
                }else {
                    scanNewProduct(getView());
                }
            }
        });

        askForActionDialog.show(getParentFragmentManager(), getString(R.string.scanned_product_not_found));
    }



    //Ask to create a new product or try again
    private void askCreateNewProdOrTryAgain(String barcode){
        AskForActionDialog askForActionDialog = new AskForActionDialog(
                getString(R.string.scanned_product_not_found), getString(R.string.tray_again),
                getString(R.string.create_new_product));
        askForActionDialog.setButtonListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object){
                    navigateToCreateProduct(barcode);
                }else {
                    scanNewProduct(getView());
                }
            }
        });

        askForActionDialog.show(getParentFragmentManager(), getString(R.string.scanned_product_not_found));
    }



    //When navigating, the main activity gets destroyed and all the viewModel scooped to it
    private void navigateToCreateProduct(String barcode){
        Bundle args = new Bundle();
        args.putLong("business_key", sellViewModel.getCurrentBusinessId());
        args.putString("product_key", barcode);

        PendingIntent pendingIntent = new NavDeepLinkBuilder(getContext())
                .setGraph(R.navigation.mobile_navigation).
                setDestination(R.id.createProductFragment2)
                .setArguments(args)
                .createPendingIntent();

        try {
            // This will execute the PendingIntent
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            // Handle error if PendingIntent is canceled
            e.printStackTrace();
        }catch (Exception e){

        }
    }

    private void setCardListener(){
        productToSellAdapter.setListener(new ProductToSellAdapter.OnclickProductCardListener() {
            @Override
            public void onShortTap(Product product, View cardHolder) {

            }

            @Override
            public void onLongTap(Product product, View cardHolder) {

            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Product prod) {

            }

            @Override
            public void reciveCardHol(View cardHolder) {

            }

            @Override
            public void onCancel(Product product) {
                sellViewModel.deleteProductToSell(product);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}