package com.kunano.scansell_native.ui.home.bottom_sheet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.HomeBottomSheetCreateBusinessFragmentBinding;
import com.kunano.scansell_native.ui.home.HomeViewModel;

;


public class BottomSheetFragment extends BottomSheetDialogFragment{
    private EditText editTextBusinessName;
    private EditText editTextBusinessAddress;
    private TextView textViewAdvertName;
    private TextView textViewAdvertAddress;
    private Button saveBusinessButton;
    private ImageButton cancelBtn;
    private HomeBottomSheetCreateBusinessFragmentBinding binding;
    HomeViewModel viewModel;

    boolean isBusinessNameValid;
    boolean isBusinessAddressValid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = HomeBottomSheetCreateBusinessFragmentBinding.inflate(inflater, container, false);
        editTextBusinessName = binding.businessName;
        editTextBusinessAddress = binding.businessAddress;
        textViewAdvertName = binding.advertName;
        textViewAdvertAddress = binding.advertAddress;
        saveBusinessButton = binding.savingButton;
        cancelBtn = binding.cancelButton;
        isBusinessAddressValid = false;
        isBusinessNameValid = false;
        setLisnerSaveButton();
        setEditTExtListeners();
        setListenerCloseButton();

        return binding.getRoot();
    }


    public void setLisnerSaveButton(){
        saveBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Try introducir business: ");
                if (isBusinessNameValid && isBusinessAddressValid){

                    hideBottomSheet();
                    provideBusinessDataToHomeViewModel();
                    return;
                }
                showWarningName(getString(R.string.advert_introduce_address));
                showWarningAddress(getString(R.string.advert_introduce_name));

            }
        });
    }

    public void setListenerCloseButton(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideBottomSheet();
            }
        });
    }








    public void setEditTExtListeners(){
        editTextBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence name, int i, int i1, int i2) {

                if(name.length() == 0){
                    isBusinessNameValid = false;
                    showWarningName(getString(R.string.advert_introduce_name));
                    desactivateSaveButton();
                }

                else if(!viewModel.validateName(name.toString())){
                    desactivateSaveButton();
                    showWarningName(getString(R.string.advert_invalid_name));
                    isBusinessNameValid = false;

                }else {
                    isBusinessNameValid = true;
                    hideWarningName();
                }

                if (isBusinessNameValid && isBusinessAddressValid){
                    activateSaveButton();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        editTextBusinessAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence address, int i, int i1, int i2) {

                if(address.toString().isEmpty()){
                    isBusinessAddressValid = false;
                    showWarningAddress(getString(R.string.advert_introduce_address));
                    desactivateSaveButton();
                }



                else if(!viewModel.validateAddress(address.toString())){
                    desactivateSaveButton();
                    isBusinessAddressValid = false;
                    showWarningAddress(getString(R.string.advert_invalid_address));
                }else {
                    isBusinessAddressValid = true;
                    hideWarningAddress();
                }

                if (isBusinessNameValid && isBusinessAddressValid){
                    activateSaveButton();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }








    public void desactivateSaveButton(){
        saveBusinessButton.setClickable(false);
    }

    public void activateSaveButton(){
        saveBusinessButton.setClickable(true);
    }

    public void provideBusinessDataToHomeViewModel(){
        String name = editTextBusinessName.getText().toString();
        String address = editTextBusinessAddress.getText().toString();
        viewModel.reciveDataBusiness(name, address);

    }






    private void hideBottomSheet(){
        this.dismiss();
    }


    public void showWarningName(String message) {
        textViewAdvertName.setText(message);
    }


    public void showWarningAddress(String message) {

        textViewAdvertAddress.setText(message);
    }


    public void hideWarningName() {
        textViewAdvertName.setText("");
    }


    public void hideWarningAddress() {
        textViewAdvertAddress.setText("");
    }


}