package com.kunano.scansell_native.ui.home.bottom_sheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.model.Home.Business;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    Business businessData;
    private ImageButton imageButtonCancel;
    private EditText editTextBusinessName;
    private EditText editTextBusinessAddress;
    private Button savingButton;

    AdminBottomSheet adminBottomSheet;

    public BottomSheetFragment(AdminBottomSheet adminBottomSheet){
     this.adminBottomSheet = adminBottomSheet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //System.out.println("objetos para colectar comps: "+ bottomSheetComponents.hashCode());
        View createBusinessView = inflater.inflate(R.layout.home_bottom_sheet_create_business_fragment, container, false);
        editTextBusinessName = createBusinessView.findViewById(R.id.business_name);
        editTextBusinessAddress = createBusinessView.findViewById(R.id.business_address);
        savingButton = createBusinessView.findViewById(R.id.saving_button);
        imageButtonCancel = createBusinessView.findViewById(R.id.cancel_button);
        setClicksEventsOnButtons();

        return createBusinessView;
    }


    public void setClicksEventsOnButtons(){
        setClickEventOnCancelButton();
        setClickEventOnSavingButton();
    }

    private void setClickEventOnCancelButton(){
        imageButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminBottomSheet.hideBottomSheet();
            }
        });
    }

    private void setClickEventOnSavingButton(){
        savingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextBusinessName.getText().toString();
                String address = editTextBusinessAddress.getText().toString();

                businessData = new Business(name, address);
                adminBottomSheet.crearBusiness(businessData);
            }
        });
    }

}