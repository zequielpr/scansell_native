package com.kunano.scansell_native.ui.home.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBusinessBinding;
import com.kunano.scansell_native.model.Home.product.Product;
import com.kunano.scansell_native.model.db.relationship.BusinessWithProduct;
import com.kunano.scansell_native.ui.home.HomeViewModel;

import java.util.List;


public class BusinessFragment extends Fragment {


    private HomeViewModel homeViewModel;
    private BusinessViewModel businessViewModel;
    FragmentBusinessBinding binding;

    private BusinessViewModel mViewModel;
    private Toolbar toolbar;

    private FloatingActionButton fButton;

    private RecyclerView recyclerViewProduct;
    private ProductCardAdapter productCardAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String businessKey = BusinessFragmentArgs.fromBundle(getArguments()).getBusinessKey();

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        businessViewModel = new ViewModelProvider(requireActivity()).get(BusinessViewModel.class);
        businessViewModel.setBusinessId(Long.parseLong(businessKey));
        homeViewModel.getCurrentBusiness().observe(getViewLifecycleOwner(), businessViewModel::updateCurrentBusiness);

        binding = FragmentBusinessBinding.inflate(inflater, container, false);

        toolbar = binding.toolbarProducts;
        toolbar.inflateMenu(R.menu.actions_toolbar_products);
        fButton = binding.floatingActionButtonCreateProduct;
        recyclerViewProduct = binding.recycleViewProducts;



        productCardAdapter= new ProductCardAdapter();
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setAdapter(productCardAdapter);


        businessViewModel.getBusinessListWithProductsList().observe(getViewLifecycleOwner(), this::printProdutcs);

        businessViewModel.getBusinessName().observe(getViewLifecycleOwner(), toolbar::setTitle);
        //businessViewModel.getAllProductLive().observe(getViewLifecycleOwner(),this::printBusiness);

        setFlButtonLisntener();
        setCardListener();
        return binding.getRoot();


    }

    public void printProdutcs(List<BusinessWithProduct> businessWithProducts) {
        List<Product> productList = businessViewModel.getProductsFromBusiness(businessWithProducts);
        productCardAdapter.submitList(productList);
    }


    private void  setCardListener(){
        productCardAdapter.setListener(new ProductCardAdapter.OnclickProductCardListener() {
            @Override
            public void onShortTap(Product business, View cardHolder) {

            }

            @Override
            public void onLongTap(Product business, View cardHolder) {

            }

            @Override
            public void getCardHolderOnBind(View cardHolder, Product business) {

            }

            @Override
            public void reciveCardHol(View cardHolder) {

            }
        });
    }


    private void setFlButtonLisntener() {
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("Current business " + businessViewModel.getBusinessId());
                //businessViewModel.createProduct();
                NavDirections action = BusinessFragmentDirections.actionProductsFragment2ToCreateProductFragment();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BusinessViewModel.class);
        // TODO: Use the ViewModel
    }




}