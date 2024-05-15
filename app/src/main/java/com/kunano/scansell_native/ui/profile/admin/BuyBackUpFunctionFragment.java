package com.kunano.scansell_native.ui.profile.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.databinding.FragmentBuyBackUpFunctionBinding;
import com.kunano.scansell_native.components.ViewModelListener;


public class BuyBackUpFunctionFragment extends BottomSheetDialogFragment {

    private Button payButton;
    private FragmentBuyBackUpFunctionBinding binding;
    private ViewModelListener viewModelListener;

    public BuyBackUpFunctionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBuyBackUpFunctionBinding.inflate(inflater, container, false);

        payButton = binding.payButton;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        payButton.setOnClickListener(this::payFunction);
    }



    private void payFunction(View view){
        if (this.viewModelListener != null)this.viewModelListener.result(null);
    }

    public void setViewModelListener(ViewModelListener viewModelListener) {
        this.viewModelListener = viewModelListener;
    }
}