package com.kunano.scansell.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kunano.scansell.databinding.SpinningWheelBinding;


public class SpinningWheel extends DialogFragment {
    public static String TAG = "wait";

    private SpinningWheelBinding binding;
    public SpinningWheel() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = SpinningWheelBinding.inflate(inflater, container, false);

        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return binding.getRoot();
    }

}
