package com.kunano.scansell_native.ui.home.bottom_sheet;

import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;

import com.kunano.scansell_native.controllers.home.HomeController;
import com.kunano.scansell_native.model.Home.Business;

public class AdminBottomSheet{
    private FragmentActivity activityParent;
    private ImageButton addBusinessButton;

    private BottomSheetFragment bottomSheetFragment;
    HomeController businessesController;

    public AdminBottomSheet(ImageButton addBusinessButton, HomeController businessesController, FragmentActivity activity){
        this.addBusinessButton = addBusinessButton;
        this.businessesController = businessesController;
        this.activityParent = activity;
    };

    public void setClickEventShowBottomSheet(){
        addBusinessButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showInterfTocreatBusiness();
            }
        });

    }


    private void   showInterfTocreatBusiness(){
        bottomSheetFragment = new BottomSheetFragment(this);
        bottomSheetFragment.show(activityParent.getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void hideBottomSheet(){
        bottomSheetFragment.dismiss();
    }


    public void crearBusiness(Business businessesData){
        System.out.println("Business name " + businessesData.getName());
        System.out.println("Business name " + businessesData.getAddress());
    }
}