package com.kunano.scansell_native.ui.login.sing_up;

import android.content.Intent;
import android.net.Uri;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.CollectEmailAndSignedUpBinding;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.Utils;
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

    private TextInputLayout emailTextInputLayout;
    private TextInputLayout emailToConfirmTextInputLayout;

    public CollectEmailAndSignUpFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emailViewModel = new ViewModelProvider(this).get(EmailViewModel.class);
        userData = new UserData();
        signUpViewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

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
       emailTextInputLayout = binding.collectEmailView.emailFilledTextField;
       emailToConfirmTextInputLayout = binding.collectEmailView.confirmEmailFilledTextField;


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();

            }
        });


       return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            }
        }

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

        emailTextInputLayout.setHint(getText(R.string.introduce_an_email));
        emailToConfirmTextInputLayout.setHint(getText(R.string.introduce_an_email_to_confirm));
    }

    private void navigateBack(){
        NavDirections navDirectionToCollectPasswd = CollectEmailAndSignUpFragmentDirections.
                actionCollectEmailToCollectPasswdFragment2();

        Navigation.findNavController(getView()).navigate(navDirectionToCollectPasswd);
    }

    private String email;
    private void continueAction(View view){
        email = emailEditText.getText().toString().trim();
        String emailToConfirm = confirmEmailEditText.getText().toString().trim();

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
                message = getString(R.string.there_has_been_an_error);
                showResult(message);
                break;
            default:
                break;
        }
    }

    private void showResult(String message){
        getActivity().runOnUiThread(()->{
            Utils.showToast(getActivity(), message, Toast.LENGTH_LONG);
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
        String url = getString(R.string.terms_of_service_link);

        // Create an Intent with ACTION_VIEW and the URI of the webpage
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            getActivity().startActivity(intent);
        }catch (Exception e){

        }
    }
    private void showPrivacyPolicy(View view){
        String url = getString(R.string.privacy_policy_link);

        // Create an Intent with ACTION_VIEW and the URI of the webpage
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            getActivity().startActivity(intent);
        }catch (Exception e){

        }
    }







}