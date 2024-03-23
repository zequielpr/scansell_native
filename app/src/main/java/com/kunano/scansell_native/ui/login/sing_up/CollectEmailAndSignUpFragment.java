package com.kunano.scansell_native.ui.login.sing_up;

import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.CollectEmailAndSignedUpBinding;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.login.LogInViewModel;
import com.kunano.scansell_native.ui.profile.admin.account.email.EmailViewModel;
import com.kunano.scansell_native.ui.profile.auth.Auth;
import com.kunano.scansell_native.ui.profile.auth.UserData;


public class CollectEmailAndSignUpFragment extends Fragment {
    private Toolbar toolbar;
    private EditText emailEditText;
    private EditText confirmEmailEditText;
    private Button signUpButton;
    protected TextView emailWarnTextView;
    protected TextView emailToConfirmWarnTextView;
    private EmailViewModel emailViewModel;
    private UserData userData;
    private SignUpViewModel signUpViewModel;
    private CollectEmailAndSignedUpBinding binding;
    private TextView termOfServicesTextView;
    private TextView privacyPolicyTextView;
    private CheckBox acceptTermAndPolicies;
    private LogInViewModel logInViewModel;

    public CollectEmailAndSignUpFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emailViewModel = new ViewModelProvider(this).get(EmailViewModel.class);
        userData = new UserData();
        signUpViewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        logInViewModel = new ViewModelProvider(requireActivity()).get(LogInViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = CollectEmailAndSignedUpBinding.inflate(inflater, container, false);

       toolbar = binding.collectEmailView.changeEmailToolbar;
       emailEditText =  binding.collectEmailView.editTextEmail;
       confirmEmailEditText =  binding.collectEmailView.editTextConfirmEmail;
       signUpButton =  binding.collectEmailView.buttonSaveEmail;
       emailWarnTextView =  binding.collectEmailView.emailWarnTextView;
       emailToConfirmWarnTextView =  binding.collectEmailView.emailToConfirmTextView;
       termOfServicesTextView = binding.termsAndPrivacyView.termsOfServiceText;
       privacyPolicyTextView = binding.termsAndPrivacyView.privacyPolicyText;
       acceptTermAndPolicies = binding.termsAndPrivacyView.checkBox;




       return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setVisibility(View.GONE);
        signUpButton.setOnClickListener(this::continueAction);
        signUpButton.setText(getText(R.string.sign_up));

        emailViewModel.getEmailWarnMessage().observe(getViewLifecycleOwner(),
                emailWarnTextView::setText);
        emailViewModel.getEmailToConfirmWarnMessage().observe(getViewLifecycleOwner(),
                emailToConfirmWarnTextView::setText);

        acceptTermAndPolicies.setOnCheckedChangeListener(getTermsAndPoliciesCheckBox());
        termOfServicesTextView.setOnClickListener(this::showTermsOfService);
        privacyPolicyTextView.setOnClickListener(this::showPrivacyPolicy);
        acceptTermAndPolicies.setChecked(signUpViewModel.getTermsAndPoliciesStateCondition());

        emailEditText.setHint(getText(R.string.introduce_an_email));
        confirmEmailEditText.setHint(getText(R.string.introduce_an_email_to_confirm));
    }

    private void navigateBack(){
        NavDirections navDirectionToCollectPasswd = CollectEmailAndSignUpFragmentDirections.
                actionCollectEmailToCollectPasswdFragment2();

        Navigation.findNavController(getView()).navigate(navDirectionToCollectPasswd);
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


    private String email;
    private void continueAction(View view){
        email = emailEditText.getText().toString();
        String emailToConfirm = confirmEmailEditText.getText().toString();

        if (!validateEmail(email, emailToConfirm)) return;

        if (checkTermsAndPolicyState())signUp();


    }



    private boolean validateEmail(String email,  String emailToConfirm ){
        UserData.EmailWarns result = userData.validateEmail(email, emailToConfirm);
        return emailViewModel.isEmailValid(result);
    }

    private Boolean checkTermsAndPolicyState(){
        if (!signUpViewModel.getTermsAndPoliciesStateCondition()){
            String message = getString(R.string.accept_terms_and_policies);
            showResult(message);
        };
        return signUpViewModel.getTermsAndPoliciesStateCondition();
    }

    private SpinningWheel spinningWheel;
    private void signUp(){
        spinningWheel = new SpinningWheel();
        spinningWheel.show(getParentFragmentManager(), SpinningWheel.TAG);
        signUpViewModel.setEmail(email);
        UserData userData = signUpViewModel.getUserData();

        Auth auth = new Auth();
        auth.createUserWithEmailAndPassword(userData, this::processRequest);
    }


    public void processRequest(Auth.SIGN_UP_REQUEST_RESULT requestResult){
        if (spinningWheel != null) spinningWheel.dismiss();
        String message;
        switch (requestResult){
            case SUCCESS:
                navigateToVerifyEmailScreen();
                break;
            case NETWORK_ERROR:
                message = getString(R.string.network_error);
                showResult(message);
                break;
            case EMAIL_IN_USE:
                message = getString(R.string.email_in_use);
                showResult(message);
                break;
            case UNKNOWN_ERROR:
                message = getString(R.string.thera_has_been_an_error);
                showResult(message);
                break;
            default:
                break;
        }
    }

    private void showResult(String message){
        getActivity().runOnUiThread(()->{
            Utils.showToast(getContext(), message, Toast.LENGTH_LONG);
        });
    }

    private void navigateToVerifyEmailScreen(){
        NavDirections navDirectionToVerifyEmail =
                CollectEmailAndSignUpFragmentDirections.actionCollectEmailToVerifyEmailFragment();
        Navigation.findNavController(getView()).navigate(navDirectionToVerifyEmail);
    };





    private CompoundButton.OnCheckedChangeListener getTermsAndPoliciesCheckBox(){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean s) {
                signUpViewModel.setTermsAndPoliciesStateCondition(s);
            }
        };
    }



    private void showTermsOfService(View view){

    }
    private void showPrivacyPolicy(View view){

    }





}