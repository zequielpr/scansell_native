package com.kunano.scansell_native.ui.sell;

import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class HandleBootomSheetBehavior {

    View linearLayout;
    BottomSheetBehavior<View> standardBottomSheetBehavior;
    public HandleBootomSheetBehavior(View view){
        this.linearLayout = view;
        standardBottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
    }



    public void setupStandardBottomSheet(boolean setFitToContents) {
        standardBottomSheetBehavior.setPeekHeight(linearLayout.getResources().getDisplayMetrics().heightPixels / 12);
        standardBottomSheetBehavior.setFitToContents(setFitToContents);
        standardBottomSheetBehavior.setHalfExpandedRatio(0.5f);
        standardBottomSheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_ALL);
    }


    public void setState(Integer state){
        standardBottomSheetBehavior.setState(state);
    }

    public Integer getState(){
        return standardBottomSheetBehavior.getState();
    }

    public void setListener(BottomSheetBehavior.BottomSheetCallback callback){
        standardBottomSheetBehavior.addBottomSheetCallback(callback);
        standardBottomSheetBehavior.setSaveFlags(BottomSheetBehavior.SAVE_ALL);

    }
}
