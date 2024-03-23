package com.kunano.scansell_native.ui.profile.admin.account.delete_account;

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
import com.kunano.scansell_native.databinding.FragmentDeleteAccountBinding;
import com.kunano.scansell_native.ui.components.AskForActionDialog;
import com.kunano.scansell_native.ui.components.Utils;
import com.kunano.scansell_native.ui.components.ViewModelListener;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;

public class DeleteAccountFragment extends Fragment {
    private EditText editTextEmail;
    private Button deleteButton;
    private Toolbar deleteAccountToolbar;
    private FragmentDeleteAccountBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private AccountHelper accountHelper;
    private TextView emailWarnTextView;
    private DeleteAccountViewModel deleteAccountViewModel;


    public DeleteAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        accountHelper = new AccountHelper();
        deleteAccountViewModel = new ViewModelProvider(this).get(DeleteAccountViewModel.class);
        mainActivityViewModel.setHandleBackPress(this::handleBackPress);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false);

        deleteAccountToolbar = binding.confirmDeleteAccountToolbar;
        editTextEmail = binding.editTextTextEmailAddress;
        deleteButton = binding.deleteButton;
        emailWarnTextView = binding.warnEmailTextView;

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deleteAccountToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        deleteAccountToolbar.setNavigationOnClickListener(this::navigateBack);
        deleteAccountViewModel.getEmailWarnVisibilityMutableLiveData().observe(getViewLifecycleOwner(),
                emailWarnTextView::setVisibility);

        deleteButton.setOnClickListener(this::deleteAccountRequest);

    }

    private void navigateBack(View view){
        NavDirections navDirectionsToAccount = DeleteAccountFragmentDirections.actionDeleteAccountFragmentToAccountFragment();
        Navigation.findNavController(getView()).navigate(navDirectionsToAccount);
        mainActivityViewModel.setHandleBackPress(null);
    }

    private void handleBackPress(){
        navigateBack(getView());
    }

    AskForActionDialog askAskToDeleteAccount;
    private void deleteAccountRequest(View view){

        if (!validateEmail(editTextEmail.getText().toString()))return;

        askAskToDeleteAccount = new AskForActionDialog(getString(R.string.delete_account),
                getString(R.string.deleted_account_not_retrievable));
        askAskToDeleteAccount.show(getParentFragmentManager(), "delete account");
        askAskToDeleteAccount.setButtonListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object){
                    accountHelper.deleteAccount(DeleteAccountFragment.this::processRequest);
                }
            }
        });

    }

    private void processRequest(String result){
        String message;

        if (result.equals(AccountHelper.SUCCESS)){
            accountHelper.signOut(this);
            message = getString(R.string.account_deleted_success);

        }else if(result.contains(AccountHelper.SIG_IN_AGAIN)){
            message = getString(R.string.sign_in_again);

        }else if(result.contains(AccountHelper.NETWORK_ERROR)){
            message = getString(R.string.network_error);
        }else {
            message = getString(R.string.thera_has_been_an_error);
        }
        showResult(message);

    }

    private void showResult(String message){
        Utils.showToast(getActivity(), message, Toast.LENGTH_LONG);
    }

    private boolean validateEmail(String email){
        boolean emailValid = email.equals(accountHelper.getUserEmail());
        showWarn(emailValid);
        return emailValid;
    }

    private void showWarn(boolean result){
        deleteAccountViewModel.setEmailWarnVisibilityMutableLiveData(result?View.GONE:View.VISIBLE);

    }
}