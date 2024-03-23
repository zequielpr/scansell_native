package com.kunano.scansell_native.ui.login.sing_up;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.ui.login.LogInViewModel;
import com.kunano.scansell_native.ui.profile.admin.account.password.PasswordViewModel;


public class CollectPasswdFragment extends Fragment {

    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button continueButton;
    private CheckBox checkBoxShowOrHidePassword;
    private PasswordViewModel passwordViewModel;

    private MainActivityViewModel mainActivityViewModel;
    private TextView passwdNotMatchTextView;
    private TextView atLeastOneUpperAndLowerCaseLetterTxtView;
    private TextView atLeastOneDigitTxtView;
    private TextView atLeastEightCharacters;
    private Toolbar updatePasswordToolbar;
    private SignUpViewModel signUpViewModel;
    private LogInViewModel logInViewModel;


    public CollectPasswdFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
   /* public static CollectPasswdFragment newInstance(String param1, String param2) {
        CollectPasswdFragment fragment = new CollectPasswdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordViewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        signUpViewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        logInViewModel = new ViewModelProvider(requireActivity()).get(LogInViewModel.class);
        logInViewModel.setLogInViewModelListener(this::navigateBack);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_change_password, container, false);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        confirmPasswordEditText = view.findViewById(R.id.editTextConfirmPassword);

        checkBoxShowOrHidePassword = view.findViewById(R.id.checkBoxShowPassword);
        passwdNotMatchTextView = view.findViewById(R.id.matchTextView);
        atLeastOneUpperAndLowerCaseLetterTxtView = view.findViewById(R.id.upperAndLowerCaseTextView);
        atLeastOneDigitTxtView = view.findViewById(R.id.atLeastOneDigitTextView);
        atLeastEightCharacters = view.findViewById(R.id.atLeast8CharactersTextView);
        updatePasswordToolbar = view.findViewById(R.id.changePasswordToolbar);

        continueButton = view.findViewById(R.id.buttonSavePassword);


        return view;
    }

    private void navigateBack(){
        NavDirections navDirectionToCollectName = CollectPasswdFragmentDirections.
                actionCollectPasswdFragment2ToCollectNameAndSurnameFragment();
        Navigation.findNavController(getView()).navigate(navDirectionToCollectName);
        logInViewModel.setLogInViewModelListener(null);
    }

    public void onResume(){
        super.onResume();
        logInViewModel.setLogInViewModelListener(this::navigateBack);
    }

    public void onDestroy(){
        super.onDestroy();
        logInViewModel.setLogInViewModelListener(null);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        continueButton.setText(R.string.continue_action);


        passwordEditText.addTextChangedListener(passwordViewModel.getPasswordEditTextWatcher(getContext()));
        confirmPasswordEditText.addTextChangedListener(passwordViewModel.getConfirmPasswordEditTextWatcher(passwordEditText));
        continueButton.setOnClickListener(this::continueRequest);
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
                continueButton::setClickable);
        passwordViewModel.getPasswdNotMatchVisibility().observe(getViewLifecycleOwner(),
                passwdNotMatchTextView::setVisibility);

        updatePasswordToolbar.setVisibility(View.GONE);
    }

    public void navigateBack(View view){

    }

    public void continueRequest(View view){
        String passwd = passwordEditText.getText().toString();
        String passwdToConfirm = confirmPasswordEditText.getText().toString();

        if (!passwordViewModel.checkIfPasswdMatch(passwd, passwdToConfirm))return;

        signUpViewModel.setPasswd(passwd);
        NavDirections navDirectionToCollectEmail = CollectPasswdFragmentDirections.
                actionCollectPasswdFragment2ToCollectEmail();

        Navigation.findNavController(getView()).navigate(navDirectionToCollectEmail);
    }
}