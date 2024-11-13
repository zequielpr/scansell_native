package com.kunano.scansell.ui.home.bottom_sheet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.components.ViewModelListener;
import com.kunano.scansell.model.Home.business.Business;
import com.kunano.scansell.R;
import com.kunano.scansell.databinding.HomeBottomSheetCreateBusinessFragmentBinding;

import java.time.LocalDateTime;


public class BottomSheetFragmentCreateBusiness extends BottomSheetDialogFragment{
    public static String TAG = "Create business";
    private EditText editTextBusinessName;
    private EditText editTextBusinessAddress;
    private TextView textViewAdvertName;
    private TextView textViewAdvertAddress;
    private Button saveBusinessButton;
    private HomeBottomSheetCreateBusinessFragmentBinding binding;
    private ViewModelListener<Boolean> requestRestult;
    BottomSheetCreateBusinessViewModel viewModel;

    boolean isBusinessNameValid;
    boolean isBusinessAddressValid;

    private String title;
    private String buttonTitle;

    private String businessName;
    private String businessAddress;
    boolean toUpdate;
    private Long currentBusinessId;



    public BottomSheetFragmentCreateBusiness(String businessName,
                                             String businessAddress,  boolean toUpdate, Long currentBusinessId) {
        this.businessName = businessName;
        this.businessAddress = businessAddress;
        this.toUpdate = toUpdate;
        this.currentBusinessId = currentBusinessId;
    }
    public BottomSheetFragmentCreateBusiness() {
        this.toUpdate = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(BottomSheetCreateBusinessViewModel.class);
        binding = HomeBottomSheetCreateBusinessFragmentBinding.inflate(inflater, container, false);
        editTextBusinessName = binding.businessName;
        editTextBusinessAddress = binding.businessAddress;
        textViewAdvertName = binding.advertName;
        textViewAdvertAddress = binding.advertAddress;
        saveBusinessButton = binding.savingButton;

        if (businessName != null & businessAddress != null){
            isBusinessAddressValid = true;
            isBusinessNameValid = true;
        }else {
            isBusinessAddressValid = false;
            isBusinessNameValid = false;
        }

        binding.createBusinessTitle.setText(toUpdate?
                getString(R.string.update):getString(R.string.create_new_business));
        saveBusinessButton.setText(toUpdate?
                getString(R.string.update):getString(R.string.save));
        editTextBusinessName.setText(businessName);
        editTextBusinessAddress.setText(businessAddress);

        setLisnerSaveButton();
        setEditTExtListeners();

        return binding.getRoot();
    }


    public void setLisnerSaveButton(){
        saveBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBusinessNameValid){
                    showWarningName(getString(R.string.introduce_name));
                    return;
                } else if (!isBusinessAddressValid) {
                    showWarningAddress(getString(R.string.advert_introduce_address));
                    return;
                }
                hideBottomSheet();
                if(toUpdate){
                    updateBusinessRequest();
                }else {
                    createBusinessRequest();

                }



            }
        });
    }



    public void createBusinessRequest(){
        LocalDateTime localDateTime = Utils.getCurrentDate(Utils.YYYY_MM_DD_HH_MM_SS);
        String name = editTextBusinessName.getText().toString().trim();
        String address = editTextBusinessAddress.getText().toString().trim();

        Business newBusiness = new Business(name, address, localDateTime);
        viewModel.createBusiness(newBusiness, requestRestult::result);

    }

    public void updateBusinessRequest(){
        LocalDateTime localDateTime = Utils.getCurrentDate(Utils.YYYY_MM_DD_HH_MM_SS);
        if (currentBusinessId == null){
            requestRestult.result(false);
            return;
        }
        String name = editTextBusinessName.getText().toString().trim();
        String address = editTextBusinessAddress.getText().toString().trim();

        Business business = new Business(name, address, localDateTime);
        business.setBusinessId(currentBusinessId);
        viewModel.updateBusiness(business, requestRestult::result);
    }









    public void setEditTExtListeners(){
        editTextBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence name, int i, int i1, int i2) {

                if(name.toString().trim().length() < 1){
                    isBusinessNameValid = false;
                    showWarningName(getString(R.string.introduce_name));
                    desactivateSaveButton();
                    return;
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

                if(address.toString().trim().isEmpty()){
                    isBusinessAddressValid = false;
                    showWarningAddress(getString(R.string.advert_introduce_address));
                    desactivateSaveButton();
                    return;
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

    public void setRequestResult(ViewModelListener<Boolean> requestRestult) {
        this.requestRestult = requestRestult;
    }

    public interface ButtomSheetFragmentListener{
        abstract void receiveData(String name, String address);
    }

}