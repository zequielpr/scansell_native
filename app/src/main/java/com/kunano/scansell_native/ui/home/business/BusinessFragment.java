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

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBusinessBinding;

public class BusinessFragment extends Fragment {

    FragmentBusinessBinding binding;

    private BusinessViewModel mViewModel;
    private Toolbar toolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String businessKey = BusinessFragmentArgs.fromBundle(getArguments()).getBusinessKey();



        binding = FragmentBusinessBinding.inflate(inflater, container, false);

        toolbar = binding.toolbarProducts;
        toolbar.inflateMenu(R.menu.actions_toolbar_products);
        toolbar.setTitle("Business name");

        return binding.getRoot();


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BusinessViewModel.class);
        // TODO: Use the ViewModel
    }

}