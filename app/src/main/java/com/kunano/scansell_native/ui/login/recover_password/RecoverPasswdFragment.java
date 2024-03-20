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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentRecoverPasswdBinding;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.login.sing_in.SignInViewModel;
import com.kunano.scansell_native.ui.profile.auth.Auth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecoverPasswdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecoverPasswdFragment extends Fragment {
    private EditText emailAddressEditText;
    private Button recoverPasswdButton;
    private TextView emailWarnTextView;
    private Auth auth;
    private FragmentRecoverPasswdBinding binding;
    private SignInViewModel signInViewModel;

    public RecoverPasswdFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = new Auth();
        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
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
        signInViewModel.getEmailWarn().observe(getViewLifecycleOwner(), emailWarnTextView::setText);
        recoverPasswdButton.setOnClickListener(this::recoverPasswdRequest);
    }


    private void recoverPasswdRequest(View view){

        String email = emailAddressEditText.getText().toString();

        if (!signInViewModel.validateEmail(email)) return;
        auth.recoverPassword(email, this::processRequest);

    }

    private void processRequest(Auth.RESET_PASSWORD_REQUEST result){
        String message;
        switch (result){
            case SUCCESS:
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
            Utils.showToast(getContext(), message, Toast.LENGTH_SHORT);
        });
    }
}