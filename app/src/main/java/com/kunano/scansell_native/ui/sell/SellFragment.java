package com.kunano.scansell_native.ui.sell;

import android.content.Context;
import android.graphics.Bitmap;
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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.SellFragmentBinding;
import com.kunano.scansell_native.model.Home.business.Business;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.ui.components.custom_camera.CustomCamera;
import com.kunano.scansell_native.ui.home.business.BusinessViewModel;
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
    private BusinessViewModel businessViewModel;
    private ProductToSellAdapter productToSellAdapter;
    BusinessSpinnerAdapter spinerAdapter;
    private ImageButton imageButtonScan;





    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);
         sellViewModel =
                new ViewModelProvider(requireActivity()).get(SellViewModel.class);
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

        sellViewModel.getProductToSellMutableLiveData().observe(getViewLifecycleOwner(), (l)->{
            productToSellAdapter.submitList(l);
            productToSellAdapter.notifyDataSetChanged();
        });
        sellViewModel.getTotalToPay().observe(getViewLifecycleOwner(), (t)->{
            totalTextView.setText(String.valueOf(t));
        });


        productToSellAdapter.setActivityParent(getActivity());
        setCardListener();





        spinerAdapter = new BusinessSpinnerAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());


        sellViewModel.getBusinessesListLiveData().observe(getViewLifecycleOwner(), (listB)->{
            spinerAdapter.clear();
            spinerAdapter.addAll(listB);
            sellViewModel.setCurrentBusinessId(spinerAdapter.getItem(0).getBusinessId());
        });

        spinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Business selectedBusiness = (Business) parent.getItemAtPosition(position);
                sellViewModel.setCurrentBusinessId(selectedBusiness.getBusinessId());
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
                    sellViewModel.requestProduct(barCodeData, SellFragment.this::processProductRequest);
                }
            }
        });

        //Buttons linkings
        imageButtonScan.setOnClickListener(this::scanNewProduct);
        finishButton.setOnClickListener(this::finish);

        return root;
    }


    private void navigateToReceipt(){
        NavDirections navDirections = SellFragmentDirections.actionNavigationDashboardToReceiptsFragment();
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


    public void processProductRequest(Object result){
        if(result == null){
            //Ask to add product
            businessViewModel.setCurrentBusinessId(sellViewModel.getCurrentBusinessId());

            getActivity().runOnUiThread(()->navigateToCreateProduct());
        }else {
            Product p = (Product) result;
            sellViewModel.addProductToSellMutableLiveData(p);
            Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

            // Vibrate for 500 milliseconds
            if (vibrator != null) {
                vibrator.vibrate(250);
            }
        }


    }

    private void navigateToCreateProduct(){
        NavDirections navigation = SellFragmentDirections.actionNavigationDashboardToCreateProductFragment();
        Navigation.findNavController(getView()).navigate(navigation);
    }

    public void onDestroy(){
        super.onDestroy();
        System.out.println("Activity on destroy");
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
                sellViewModel.deleteProductToSellMutableLiveData(product);
            }
        });
    }


    public void onViewCreated(  @NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        toolbar.inflateMenu(R.menu.sell_tool_bar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.got_to_receipt){
                    navigateToReceipt();
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