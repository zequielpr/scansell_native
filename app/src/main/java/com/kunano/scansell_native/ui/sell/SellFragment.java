package com.kunano.scansell_native.ui.sell;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.SellFragmentBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.ListenResponse;
import com.kunano.scansell_native.ui.components.custom_camera.CustomCamera;
import com.kunano.scansell_native.ui.sell.adapters.BusinessSpinnerAdapter;
import com.kunano.scansell_native.ui.sell.adapters.ProductToSellAdapter;
import com.kunano.scansell_native.ui.sell.collect_payment_method.CollectPaymentMethodFragment;

import java.util.ArrayList;

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


    // Invoked when the activity might be temporarily destroyed; save the instance state here.


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


        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setHasFixedSize(true);
        productToSellAdapter = new ProductToSellAdapter();
        recyclerViewProducts.setAdapter(productToSellAdapter);


        sellViewModel.getTotalToPay().observe(getViewLifecycleOwner(), (t)->{
            totalTextView.setText(String.valueOf(t));
        });

        productToSellAdapter.setActivityParent(getActivity());
        setCardListener();

        spinerAdapter = new BusinessSpinnerAdapter(getContext(),
                R.layout.custom_item_spinner, new ArrayList<>());


        //Set business in adapter and get the current business id
        sellViewModel.getBusinessesListLiveData().observe(getViewLifecycleOwner(), (listB)->{
            spinerAdapter.clear();
            spinerAdapter.addAll(listB);
            try {
                //sellViewModel.setCurrentBusinessId(spinerAdapter.getItem(0).getBusinessId());
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
                Business selectedBusiness = (Business) parent.getItemAtPosition(position);
                sellViewModel.setCurrentBusinessId(selectedBusiness.getBusinessId());
                sellViewModel.setSelectedIndexSpinner(position);

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

        //Buttons linkings
        imageButtonScan.setOnClickListener(this::scanNewProduct);
        finishButton.setOnClickListener(this::finish);
        sellViewModel.getFinishButtonState().observe(getViewLifecycleOwner(), finishButton::setClickable);
        sellViewModel.getFinishButtonState().observe(getViewLifecycleOwner(), (s)->spinner.setEnabled(!s));
        sellViewModel.getSelectedIndexSpinner().observe(getViewLifecycleOwner(), spinner::setSelection);



        mainActivityViewModel.setHandleBackPress(null);



        return root;
    }

    private void activateOrDesactFinishButton(boolean activate){
        finishButton.setClickable(activate);
    }


    private void navigateToReceipt(){
        NavDirections navDirections = SellFragmentDirections.actionSellFragmentToReceiptsFragment2();
        Navigation.findNavController(getView()).navigate(navDirections);
    }



    private void scanNewProduct(View v){
        customCamera.setNewProductInCamera(true);
    }


    private void finish(View view){
        CollectPaymentMethodFragment collectPaymentMethodFragment;
        collectPaymentMethodFragment = new CollectPaymentMethodFragment(sellViewModel);
        collectPaymentMethodFragment.show(getParentFragmentManager(), "collect_payment_method");
    }


    public void processProductRequest(Object result, String barcode){
        if(result == null && sellViewModel.getCurrentBusinessId() != null){
            //Ask to add product
           getActivity().runOnUiThread(()->askCreateNewProdOrTryAgain(barcode));
            return;
        }else if(sellViewModel.getCurrentBusinessId() == null){
            return;
        }
        Product p = (Product) result;
        sellViewModel.addProductToSell(p);
        Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        // Vibrate for 500 milliseconds
        if (vibrator != null) {
            vibrator.vibrate(250);
        }

    }


    //Ask to create a new product or try again
    private void askCreateNewProdOrTryAgain(String barcode){
        AskForActionDialog askForActionDialog = new AskForActionDialog(getLayoutInflater(),
                getString(R.string.scanned_product_not_found), getString(R.string.tray_again),
                getString(R.string.create_new_product));
        askForActionDialog.setButtonListener(new ListenResponse() {
            @Override
            public void isSuccessfull(boolean resultado) {
                if (resultado){
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


    public void onViewCreated(  @NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}