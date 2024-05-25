package com.kunano.scansell_native.ui.sales.admin.account.password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import com.kunano.scansell_native.databinding.FragmentChangePasswordBinding;
import com.kunano.scansell_native.components.SpinningWheel;
import com.kunano.scansell_native.components.Utils;
import com.kunano.scansell_native.ui.sales.auth.AccountHelper;


public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button saveButton;
    private CheckBox checkBoxShowOrHidePassword;
    private PasswordViewModel passwordViewModel;

    private MainActivityViewModel mainActivityViewModel;
    private TextView passwdNotMatchTextView;
    private TextView atLeastOneUpperAndLowerCaseLetterTxtView;
    private TextView atLeastOneDigitTxtView;
    private TextView atLeastEightCharacters;
    private AccountHelper accountHelper;
    private SpinningWheel spinningWheel;
    private Toolbar updatePasswordToolbar;


    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordViewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        accountHelper = new AccountHelper();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);

        passwordEditText = binding.editTextPassword;
        confirmPasswordEditText = binding.editTextConfirmPassword;
        checkBoxShowOrHidePassword = binding.checkBoxShowPassword;
        passwdNotMatchTextView = binding.matchTextView;
        atLeastOneUpperAndLowerCaseLetterTxtView = binding.upperAndLowerCaseTextView;
        atLeastOneDigitTxtView = binding.atLeastOneDigitTextView;
        atLeastEightCharacters = binding.atLeast8CharactersTextView;
        updatePasswordToolbar = binding.changePasswordToolbar;

        saveButton = binding.buttonSavePassword;

        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handleBackPress();
                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        passwordEditText.addTextChangedListener(passwordViewModel.getPasswordEditTextWatcher(getContext()));
        confirmPasswordEditText.addTextChangedListener(passwordViewModel.getConfirmPasswordEditTextWatcher(passwordEditText));
        saveButton.setOnClickListener(this::savePasswordRequest);
        checkBoxShowOrHidePassword.setOnCheckedChangeListener(passwordViewModel.getShowOrHidePasswordListener());

        passwordViewModel.getHideOrShowPasswordMutableData().observe(getViewLifecycleOwner(),
                passwordEditText::setInputType);

        passwordViewModel.getHideOrShowPasswordMutableData().observe(getViewLifecycleOwner(),
                confirmPasswordEditText::setInputType);

        passwordViewModel.getUpperAndLowerCaseMutableData().observe(getViewLifecycleOwner(),
                atLeastOneUpperAndLowerCaseLetterTxtView::setText);
        passwordViewModel.getAtLeastOneDigit().observe(getViewLifecycleOwner(),
                atLeastOneDigitTxtView::setText);
        passwordViewModel.getAtLeastEightCharacters().observe(getViewLifecycleOwner(),
                atLeastEightCharacters::setText);

        passwordViewModel.getIsPasswordValid().observe(getViewLifecycleOwner(),
                saveButton::setClickable);
        passwordViewModel.getPasswdNotMatchVisibility().observe(getViewLifecycleOwner(),
                passwdNotMatchTextView::setVisibility);

        updatePasswordToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        updatePasswordToolbar.setNavigationOnClickListener(this::navigateBack);
    }


    private void handleBackPress(){
        navigateBack(getView());
    }


    private void navigateBack(View view){
        NavDirections navDirectionsToAccount = ChangePasswordFragmentDirections.actionChangePasswordFragmentToAccountFragment();
        Navigation.findNavController(getView()).navigate(navDirectionsToAccount);
    }



    protected void savePasswordRequest(View view){
        String passwd = passwordEditText.getText().toString().trim();
        String passwdConfirm = confirmPasswordEditText.getText().toString().trim();
        if (!passwordViewModel.checkIfPasswdMatch(passwdConfirm, passwd))return;

        if (accountHelper != null) accountHelper.setPassword(passwdConfirm, this::processRequest);
        spinningWheel = new SpinningWheel();
        spinningWheel.show(getChildFragmentManager(), SpinningWheel.TAG);

    }

    public void processRequest(String result){
        if (spinningWheel!= null)spinningWheel.dismiss();
        System.out.println("Result of changing password: " + result);
        if (spinningWheel != null) spinningWheel.dismiss();
        String message;

        if (result.equalsIgnoreCase(AccountHelper.SUCCESS)){
            message = getString(R.string.passwd_updated_success);
            navigateBack(getView());

        }else if (result.contains(AccountHelper.NETWORK_ERROR)){
            message = getString(R.string.network_error);
        }else if (result.contains(AccountHelper.SIG_IN_AGAIN)){
            message = getString(R.string.sign_in_again);

        }
        else {
            message = getString(R.string.there_has_been_an_error);
        }
        showResult(message);

    }
    private void showResult(String message){
        Utils.showToast(getActivity(),message , Toast.LENGTH_SHORT);
    }
}