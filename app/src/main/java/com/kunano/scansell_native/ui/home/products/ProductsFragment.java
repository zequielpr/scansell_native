package com.kunano.scansell_native.ui.home.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kunano.scansell_native.databinding.FragmentProductsBinding;

public class ProductsFragment extends Fragment {

    FragmentProductsBinding binding;

    private ProductsViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String businessKey = ProductsFragmentArgs.fromBundle(getArguments()).getBusinessKey();



        binding = FragmentProductsBinding.inflate(inflater, container, false);

        binding.businessKey.setText(businessKey);

        return binding.getRoot();


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        // TODO: Use the ViewModel
    }

}