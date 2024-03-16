package com.kunano.scansell_native.ui.profile.admin.account.name;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentChangeNameBinding;
import com.kunano.scansell_native.model.ValidateData;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;

public class ChangeNameFragment extends Fragment {

    private FragmentChangeNameBinding binding;
    private MainActivityViewModel mainActivityViewModel;

    private EditText editTextNewName;
    private SpinningWheel spinningWheel;
    private AccountHelper accountHelper;
    private TextView newNameWarnTextView;
    private Button saveButton;
    private ChangeNameViewModel changeNameViewModel;
    private Toolbar changeNameToolbar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        accountHelper = new AccountHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.setHandleBackPress(this::handleBackPress);
        changeNameViewModel = new ViewModelProvider(this).get(ChangeNameViewModel.class);

        binding = FragmentChangeNameBinding.inflate(inflater, container, false);

        editTextNewName = binding.editTextName;
        newNameWarnTextView = binding.newNameWarnTextView;
        saveButton = binding.buttonSave;
        changeNameToolbar = binding.changeNameToolbar;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton.setOnClickListener(this::changeNameRequest);
        changeNameViewModel.getNewNameWarnMutableData().observe(getViewLifecycleOwner(), newNameWarnTextView::setText);
        changeNameToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        changeNameToolbar.setNavigationOnClickListener(this::navigateBack);

    }

    private void handleBackPress(){
        navigateBack(getView());
        mainActivityViewModel.setHandleBackPress(null);
    }

    private void navigateBack(View view){
        NavDirections navDirectionsToAccount = ChangeNameFragmentDirections.actionChangeNameFragmentToAccountFragment();
        Navigation.findNavController(getView()).navigate(navDirectionsToAccount);
    }

    public void changeNameRequest(View view){
        if (accountHelper == null) return;
        String newName = editTextNewName.getText().toString().trim();

        if (!validateName(newName))return;

        spinningWheel = new SpinningWheel();
        spinningWheel.show(getParentFragmentManager(), "wait");
        accountHelper.setUserName(newName,this::processRequest);

    }

    private void processRequest(String result){
        System.out.println("result of request: " + result);
        if (spinningWheel != null) spinningWheel.dismiss();
        String message;

        if (result.equalsIgnoreCase(AccountHelper.SUCCESS)){
            message = getString(R.string.name_updated_success);
            navigateBack(getView());

        }else if (result.contains(AccountHelper.NETWORK_ERROR)){
            message = getString(R.string.network_error);
        }else {
            message = getString(R.string.thera_has_been_an_error);
        }
        showToast(message);

    }

    private void showToast(String message){
        getActivity().runOnUiThread(()->Utils.showToast(getContext(), message, Toast.LENGTH_SHORT));
    }

    private boolean validateName(String  newName){

         if (newName.isEmpty()){
             changeNameViewModel.setNewNameWarnMutableData(getString(R.string.advert_introduce_name));
             return false;
         }else if(!ValidateData.validateName(newName)) {
             changeNameViewModel.setNewNameWarnMutableData(getString(R.string.advert_invalid_name));
             return false;
         }else {
             changeNameViewModel.setNewNameWarnMutableData(null);
             return true;
         }
    }


}