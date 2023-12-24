package com.kunano.scansell_native.controllers.home;

import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.Business;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetFragment;
import com.kunano.scansell_native.ui.home.bottom_sheet.BottomSheetViewModel;

public class BottomSheetCreateBusinessController {
    private FragmentActivity activityParent;
    private ImageButton addBusinessButton;

    private BottomSheetFragment bottomSheetFragment;
    private BusinessController businessesController;

    private BottomSheetViewModel bottomSheetViewModel;

    public BottomSheetViewModel getBottomSheetViewModel() {
        return bottomSheetViewModel;
    }

    public BottomSheetCreateBusinessController(ImageButton addBusinessButton, BusinessController businessesController,
                                               BottomSheetViewModel bottomSheetViewModel, FragmentActivity activity){
        this.addBusinessButton = addBusinessButton;
        this.businessesController = businessesController;
        this.bottomSheetViewModel = bottomSheetViewModel;
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
        setAdvertIcorrectName("");
        setAdvertIncorrectAddress("");
    }


    public void setAdvertIcorrectName(String advertIcorrectName){
        bottomSheetViewModel.setAdvertIncorrectName(advertIcorrectName);
    }

    public void setAdvertIncorrectAddress(String advertIncorrectAddress){
        bottomSheetViewModel.setAdvertIncorrectAddress(advertIncorrectAddress);
    }

    public MutableLiveData<String> getAdvertIncorrectName(){
        return bottomSheetViewModel.getAdvertIncorrectName();
    }

    public  MutableLiveData<String> getAdvertIncorrectAddress(){
        return bottomSheetViewModel.getAdvertIncorrectAddress();
    }


    public void setNewBusinessData(Business businessesData){
        businessesController.setBusinessesModel(businessesData);
        if (!validateBusinessData())return;

        //businessesController.showData();
        businessesController.addBusiness().thenAccept(addSuccessfully ->{
            if(addSuccessfully){
                businessesController.showData();
                hideBottomSheet();
                Toast.makeText(activityParent, activityParent.getString(R.string.business_created_succ), Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(activityParent, activityParent.getString(R.string.failure_to_create_business), Toast.LENGTH_LONG).show();
            }

        });


        System.out.println("Business name " +businessesController.getName());
        System.out.println("Business name " + businessesController.getAddress());
    }


    private boolean validateBusinessData(){

        if(isAnyAttributeNull())return false;


        boolean isNameValid = businessesController.verifyName();
        boolean isAddressValid = businessesController.verifyAddres();

        if(!isNameValid){
            setAdvertIcorrectName(activityParent.getString(R.string.advert_invalid_name));
            return false;
        }
        else if (!isAddressValid){
            setAdvertIcorrectName("");
            setAdvertIncorrectAddress(activityParent.getString(R.string.advert_invalid_address));
            return false;
        }
        setAdvertIcorrectName("");
        setAdvertIncorrectAddress("");
        return  true;
    }

    private boolean isAnyAttributeNull(){
        if(businessesController.getName().isEmpty()){
            setAdvertIcorrectName(activityParent.getString(R.string.advert_introduce_name));
            return true;

        }else if(businessesController.getAddress().isEmpty()){
            setAdvertIcorrectName("");
            setAdvertIncorrectAddress(activityParent.getString(R.string.advert_introduce_address));
            return true;
        }
        setAdvertIncorrectAddress("");
        setAdvertIcorrectName("");
        return false;

    }
}