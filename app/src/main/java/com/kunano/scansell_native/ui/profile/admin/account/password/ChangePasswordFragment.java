package com.kunano.scansell_native.ui.profile.admin.account.password;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentChangePasswordBinding;
import com.kunano.scansell_native.model.ValidateData;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;


public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button saveButton;
    private CheckBox checkBoxShowOrHidePassword;
    private ChangePasswordViewModel changePasswordViewModel;

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
        changePasswordViewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.setHandleBackPress(this::handleBackPress);
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


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        passwordEditText.addTextChangedListener(setPasswordEditTextWatcher());
        confirmPasswordEditText.addTextChangedListener(setConfirmPasswordEditTextWatcher());
        saveButton.setOnClickListener(this::savePasswordRequest);
        checkBoxShowOrHidePassword.setOnCheckedChangeListener(showOrHidePassword());

        changePasswordViewModel.getHideOrShowPasswordMutableData().observe(getViewLifecycleOwner(),
                passwordEditText::setInputType);

        changePasswordViewModel.getHideOrShowPasswordMutableData().observe(getViewLifecycleOwner(),
                confirmPasswordEditText::setInputType);

        changePasswordViewModel.getUpperAndLowerCaseMutableData().observe(getViewLifecycleOwner(),
                atLeastOneUpperAndLowerCaseLetterTxtView::setText);
        changePasswordViewModel.getAtLeastOneDigit().observe(getViewLifecycleOwner(),
                atLeastOneDigitTxtView::setText);
        changePasswordViewModel.getAtLeastEightCharacters().observe(getViewLifecycleOwner(),
                atLeastEightCharacters::setText);

        changePasswordViewModel.getIsPasswordValid().observe(getViewLifecycleOwner(),
                saveButton::setClickable);
        changePasswordViewModel.getPasswdNotMatchVisibility().observe(getViewLifecycleOwner(),
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
        if (mainActivityViewModel != null) mainActivityViewModel.setHandleBackPress(null);
    }



    protected void savePasswordRequest(View view){
        String passwd = passwordEditText.getText().toString().trim();
        String passwdConfirm = confirmPasswordEditText.getText().toString().trim();
        if (!checkIfPasswdMatch(passwdConfirm, passwd))return;

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
            message = getString(R.string.thera_has_been_an_error);
        }
        showResult(message);

    }
    private void showResult(String message){
        getActivity().runOnUiThread(()->{
            Utils.showToast(getContext(),message , Toast.LENGTH_SHORT);
        });
    }


    private CompoundButton.OnCheckedChangeListener showOrHidePassword(){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isToHide) {
                if (isToHide){
                    changePasswordViewModel.setHideOrShowPasswordMutableData(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                }else {
                    changePasswordViewModel.setHideOrShowPasswordMutableData(
                            android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                }
            }
        };
    }




    private TextWatcher setPasswordEditTextWatcher(){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString().trim();
                changePasswordViewModel.setIsPasswordValid(ValidateData.validatePassword(password));
                boolean itContainsDigit = ValidateData.containsDigit(password);
                boolean itContainsUpperAndLowerLtt = ValidateData.containsUpperAndLowerCase(password);
                boolean atLeastEightCharacters = password.length() >= 8;

                checkIsContainsDigit(itContainsDigit);
                checkUpperAndLowerCase(itContainsUpperAndLowerLtt);
                checkEightCharacters(atLeastEightCharacters);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        return textWatcher;
    }

    private void checkIsContainsDigit(boolean itContainsDigit){
        if (itContainsDigit){
            changePasswordViewModel.setAtLeastOneDigit(
                    HtmlCompat.fromHtml(getString(R.string.at_least_one_digit),
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }else {
            changePasswordViewModel.setAtLeastOneDigit(
                    HtmlCompat.fromHtml(
                            "<strike>"+getString(R.string.at_least_one_digit)+"</strike>",
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }
    }

    private void checkUpperAndLowerCase(boolean itContainsUpperAndLowerLtt){
        if (itContainsUpperAndLowerLtt){
            changePasswordViewModel.setUpperAndLowerCaseMutableData(
                    HtmlCompat.fromHtml(getString(R.string.upper_and_lower_case),
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }else {
            changePasswordViewModel.setUpperAndLowerCaseMutableData(
                    HtmlCompat.fromHtml(
                            "<strike>"+getString(R.string.upper_and_lower_case)+"</strike>",
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }
    }

    private void checkEightCharacters(boolean atLeastEightCharacters){
        if (atLeastEightCharacters){
            changePasswordViewModel.setAtLeastEightCharacters(
                    HtmlCompat.fromHtml(getString(R.string.at_least_eight_characters),
                            HtmlCompat.FROM_HTML_MODE_LEGACY));

        }else {
            changePasswordViewModel.setAtLeastEightCharacters(
                    HtmlCompat.fromHtml(
                            "<strike>"+getString(R.string.at_least_eight_characters)+"</strike>",
                            HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
    }


    private TextWatcher setConfirmPasswordEditTextWatcher(){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String passwd = passwordEditText.getText().toString();
                String passwdToConfirm = charSequence.toString();

                checkIfPasswdMatch(passwdToConfirm, passwd);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        return textWatcher;
    }

    private boolean checkIfPasswdMatch(String passwdToConfirm, String passwd){
        boolean itMatches = passwdToConfirm.equals(passwd);
        if (itMatches){
            changePasswordViewModel.setPasswdNotMatchVisibility(View.GONE);
        }else {
            changePasswordViewModel.setPasswdNotMatchVisibility(View.VISIBLE);
        }
        return itMatches;
    }
}