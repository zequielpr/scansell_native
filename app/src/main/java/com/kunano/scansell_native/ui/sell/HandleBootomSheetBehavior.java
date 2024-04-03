package com.kunano.scansell_native.ui.sell;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CustomLinearLayout {

    View linearLayout;
    BottomSheetBehavior<View> standardBottomSheetBehavior;
    public CustomLinearLayout(View view){
        this.linearLayout = view;
    }



    public void setupStandardBottomSheet() {
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setPeekHeight(1000);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
}
