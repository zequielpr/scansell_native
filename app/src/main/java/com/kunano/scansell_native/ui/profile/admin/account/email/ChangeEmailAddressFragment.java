package com.kunano.scansell_native.ui.profile.admin.account.email;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.kunano.scansell_native.databinding.FragmentChangeEmailAddressBinding;
import com.kunano.scansell_native.model.ValidateData;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;

public class ChangeEmailAddressFragment extends Fragment {


    private MainActivityViewModel mainActivityViewModel;
    private AccountHelper accountHelper;
    private EditText emailEditText;
    private EditText emailEditTextConfirm;
    private Button saveButton;
    private FragmentChangeEmailAddressBinding binding;
    private Toolbar changeEmailToolbar;
    private TextView emailWarnTextView;
    private TextView emailToConfirmWarnTextView;
    private ChangeEmailviewModel changeEmailviewModel;
    private SpinningWheel spinningWheel;


   private enum EmailWarns{
        EMAIL_VALID,
        EMAIL_NO_VALID,
        EMAIL_EMPTY,
        EMAIL_TO_CONFIRM_EMPTY,
        EMAILS_NOT_MATCHES
    }


    public ChangeEmailAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        changeEmailviewModel = new ViewModelProvider(this).get(ChangeEmailviewModel.class);

        mainActivityViewModel.setHandleBackPress(this::handleBackPress);

        accountHelper = new AccountHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChangeEmailAddressBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailEditText = binding.editTextEmail;
        emailEditTextConfirm = binding.editTextConfirmEmail;
        saveButton = binding.buttonSaveEmail;
        changeEmailToolbar = binding.changeEmailToolbar;
        saveButton.setOnClickListener(this::setSaveButtonAction);
        emailWarnTextView = binding.emailWarnTextView;
        emailToConfirmWarnTextView = binding.emailToConfirmTextView;

        changeEmailToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        changeEmailToolbar.setNavigationOnClickListener(this::navigateBack);

        changeEmailviewModel.getEmailWarnMessage().observe(getViewLifecycleOwner(), emailWarnTextView::setText);
        changeEmailviewModel.getEmailToConfirmWarnMessage().observe(getViewLifecycleOwner(), emailToConfirmWarnTextView::setText);


    }


    private void handleBackPress(){
        navigateBack(getView());
        mainActivityViewModel.setHandleBackPress(null);
    }


    private void navigateBack(View view){
        NavDirections navDirectionsBack = ChangeEmailAddressFragmentDirections.
                actionChangeEmailAddressFragmentToAccountFragment();
        Navigation.findNavController(getView()).navigate(navDirectionsBack);

    }


    private void setSaveButtonAction(View view){
        String email = emailEditText.getText().toString();
        String emailConfirm = emailEditTextConfirm.getText().toString();
        if (!validateEmail(email, emailConfirm)) return;
        shoWSpinningWheel();

        accountHelper.setUserEmail(email, this::processEmailRequest);
    }
    private void shoWSpinningWheel(){
       spinningWheel = new SpinningWheel();
       spinningWheel.show(getParentFragmentManager(), "wait");
    }



    private boolean validateEmail(String email,  String emailToConfirm ){

        EmailWarns emailWarns = EmailWarns.EMAIL_VALID;

        if (email.isEmpty()) emailWarns = EmailWarns.EMAIL_EMPTY;
        else if (!ValidateData.validateEmailAddress(email))emailWarns = EmailWarns.EMAIL_NO_VALID;
        else if (emailToConfirm.isEmpty()) emailWarns = EmailWarns.EMAIL_TO_CONFIRM_EMPTY;
        else if (!email.equals(emailToConfirm))emailWarns = EmailWarns.EMAILS_NOT_MATCHES;

        return isEmailValid(emailWarns);
    }

    private boolean isEmailValid(EmailWarns emailWarns){

        changeEmailviewModel.setEmailWarnMessage("");
        changeEmailviewModel.setEmailToConfirmWarnMessage("");

        String message;
        switch (emailWarns){
            case EMAIL_EMPTY:
                 message = getString(R.string.introduce_an_email);
                 changeEmailviewModel.setEmailWarnMessage(message);
                 return false;
            case EMAIL_NO_VALID:
                message = getString(R.string.email_not_valid);
                changeEmailviewModel.setEmailWarnMessage(message);
                return false;
            case EMAIL_TO_CONFIRM_EMPTY:
                message = getString(R.string.introduce_an_email_to_confirm);
                changeEmailviewModel.setEmailToConfirmWarnMessage(message);
                return false;
            case EMAILS_NOT_MATCHES:
                message = getString(R.string.emails_not_matches);
                changeEmailviewModel.setEmailToConfirmWarnMessage(message);
                return false;
            default:
                return true;

        }
    }

    private void processEmailRequest(String  result){
       spinningWheel.dismiss();
        String message  = "";

       if (result.contains(AccountHelper.SIG_IN_AGAIN)){
           message = getString(R.string.sign_in_again);

       } else if (result.contains(AccountHelper.NETWORK_ERROR)) {
           message = getString(R.string.network_error);
       }
       else if(result.equalsIgnoreCase(AccountHelper.SUCCESS)){
           showLinkSentLinkAlert();
           return;
       }
        showResult(message);

    }


    private void showResult(String message){
        getActivity().runOnUiThread(()->{
            Utils.showToast(getContext(),message , Toast.LENGTH_SHORT);
        });
    }


    private void showLinkSentLinkAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setTitle(getString(R.string.update_request_sent)).
                setMessage(getString(R.string.click_link_email_address)).
                setPositiveButton(getString(R.string.ok),this::dismissLinkSentLinkAlert);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void dismissLinkSentLinkAlert(DialogInterface dialogInterface, int i){
       dialogInterface.dismiss();
    }
}