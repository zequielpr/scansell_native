package com.kunano.scansell_native.ui.login.recover_password;

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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentRecoverPasswdBinding;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.SpinningWheel;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.login.LogInViewModel;
import com.kunano.scansell_native.ui.login.sing_in.SignInViewModel;
import com.kunano.scansell_native.ui.profile.auth.Auth;

public class RecoverPasswdFragment extends Fragment {
    private EditText emailAddressEditText;
    private Button recoverPasswdButton;
    private TextView emailWarnTextView;
    private Auth auth;
    private FragmentRecoverPasswdBinding binding;
    private SignInViewModel signInViewModel;
    private LogInViewModel logInViewModel;

    public RecoverPasswdFragment() {
        // Required empty public constructor
    }

    public void onResume(){
        super.onResume();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = new Auth();
        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        logInViewModel = new ViewModelProvider(requireActivity()).get(LogInViewModel.class);
        logInViewModel.setLogInViewModelListener(this::backPress);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecoverPasswdBinding.inflate(inflater, container, false);

        emailAddressEditText = binding.editTextTextEmailAddress2;
        recoverPasswdButton = binding.recoverPasswdButton;
        emailWarnTextView = binding.emailWarnTextViewRecover;

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

        signInViewModel.getEmailWarn().observe(getViewLifecycleOwner(), emailWarnTextView::setText);
        recoverPasswdButton.setOnClickListener(this::recoverPasswdRequest);

    }

    private void backPress(){
        NavDirections navDirectionsToLogIn = RecoverPasswdFragmentDirections.actionRecoverPasswdFragmentToLogInFragment();
        Navigation.findNavController(getView()).navigate(navDirectionsToLogIn);
        logInViewModel.setLogInViewModelListener(null);
    }
    public void onDestroy(){
        super.onDestroy();
        logInViewModel.setLogInViewModelListener(null);
    }


    private SpinningWheel wait = new SpinningWheel();
    private void recoverPasswdRequest(View view){
        String email = emailAddressEditText.getText().toString();

        if (!signInViewModel.validateEmail(email)) return;
        auth.recoverPassword(email, this::processRequest);
        wait.show(getChildFragmentManager(), "wait");

    }

    private void processRequest(Auth.RESET_PASSWORD_REQUEST result){
        wait.dismiss();
        String message;
        switch (result){
            case SUCCESS:
                linkToResetPasswdSent();
                break;
            case NETWORK_ERROR:
                message = getString(R.string.network_error);
                showError(message);
                break;
            case OPERATION_EXCEEDED:
                message = getString(R.string.try_again_later);
                showError(message);
                break;
            default:
                message = getString(R.string.thera_has_been_an_error);
                showError(message);
                break;
        }
    }

    private void showError(String message){
        getActivity().runOnUiThread(()->{
            Utils.showToast(getActivity(), message, Toast.LENGTH_SHORT);
        });
    }


    private void linkToResetPasswdSent(){
        AskForActionDialog linkHasBeenSent = new AskForActionDialog(getString(R.string.recover_password),
                getString(R.string.click_link_to_reset_passwd), false, true);
        linkHasBeenSent.show(getChildFragmentManager(), getString(R.string.recover_password));
    }

}