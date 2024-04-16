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
import com.kunano.scansell_native.databinding.FragmentChangeEmailAddressBinding;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;
import com.kunano.scansell_native.ui.profile.auth.UserData;

public class ChangeEmailAddressFragment extends Fragment {


    private MainActivityViewModel mainActivityViewModel;
    private AccountHelper accountHelper;
    protected EditText emailEditText;
    private EditText emailEditTextConfirm;
    protected Button saveButton;
    private FragmentChangeEmailAddressBinding binding;
    private Toolbar changeEmailToolbar;
    protected TextView emailWarnTextView;
    protected TextView emailToConfirmWarnTextView;
    private EmailViewModel emailViewModel;
    private SpinningWheel spinningWheel;



    public ChangeEmailAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        emailViewModel = new ViewModelProvider(this).get(EmailViewModel.class);

        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handleBackPress();
                    }
                });

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

        emailViewModel.getEmailWarnMessage().observe(getViewLifecycleOwner(), emailWarnTextView::setText);
        emailViewModel.getEmailToConfirmWarnMessage().observe(getViewLifecycleOwner(), emailToConfirmWarnTextView::setText);


    }


    private void handleBackPress(){
        navigateBack(getView());
    }


    private void navigateBack(View view){
        NavDirections navDirectionsBack = ChangeEmailAddressFragmentDirections.
                actionChangeEmailAddressFragmentToAccountFragment();
        Navigation.findNavController(getView()).navigate(navDirectionsBack);

    }


    protected void setSaveButtonAction(View view){
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
        UserData.EmailWarns result = accountHelper.validateEmail(email, emailToConfirm);
        return emailViewModel.isEmailValid(result);
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
            Utils.showToast(getActivity(),message , Toast.LENGTH_SHORT);
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