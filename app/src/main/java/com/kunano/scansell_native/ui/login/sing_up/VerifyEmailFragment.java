package com.kunano.scansell_native.ui.login.sing_up;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kunano.scansell_native.MainActivity;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentVerifyEmailBinding;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;

public class VerifyEmailFragment extends Fragment {
    private Button resendEmailVerificationButton;
    private FragmentVerifyEmailBinding binding;
    AccountHelper accountHelper = new AccountHelper();


    public VerifyEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountHelper = new AccountHelper();
        setResendEmailVerificationButtonAction(getView());
    }
    public void onResume(){
        super.onResume();
        System.out.println("Verify on resume");
        accountHelper = new AccountHelper();
        if (accountHelper == null)return;

        System.out.println("email verified" + accountHelper.isEmailVerified());
        if(accountHelper.isEmailVerified()){
            Intent intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    public void signOut(){
        accountHelper.signOut(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerifyEmailBinding.inflate(inflater, container, false);
        resendEmailVerificationButton = binding.buttonResend;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resendEmailVerificationButton.setOnClickListener(this::setResendEmailVerificationButtonAction);
    }

    public void setResendEmailVerificationButtonAction(View view){

        accountHelper.sendEmailVerification(this::processRequest);
    }

    private void processRequest(AccountHelper.SEND_EMAIL_VERIFY_RESULT result){
        System.out.println("Result: " + result);
        String message;
        switch (result){
            case SUCCESS:
                message = getString(R.string.verification_sent);
                break;
            case NETWORK_ERROR:
                message = getString(R.string.network_error);
                break;
            case UNUSUAL_ACTIVITY:
                message = getString(R.string.email_verification_sent_several_times);
                break;
            default:
                message = getString(R.string.thera_has_been_an_error);
                break;
        }
        showResult(message);

    }

    private void showResult(String message){
        getActivity().runOnUiThread(()->{
            Utils.showToast(getContext(), message, Toast.LENGTH_LONG);
        });
    }
}