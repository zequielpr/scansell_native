package com.kunano.scansell_native.ui.profile.admin.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.kunano.scansell_native.databinding.FragmentAccountBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {


    private MainActivityViewModel mainActivityViewModel;
    private FragmentAccountBinding binding;
    private Toolbar accountToolbar;
    private View nameSectionView;
    private View emailAddressSectionView;
    private View changePasswordSectionView;
    private View deleteAccountSectionView;
    private AccountHelper accountHelper;
    private TextView nameTextView;
    private TextView emailTextView;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mainActivityViewModel.setHandleBackPress(this::handlePressBack);
        accountHelper = new AccountHelper();


        nameSectionView = binding.nameSection;
        emailAddressSectionView = binding.emailAddressSection;
        changePasswordSectionView = binding.passwordSection;
        deleteAccountSectionView = binding.deleteAccountSection;
        nameTextView = binding.nameTextView;
        emailTextView = binding.emailTextView;

        accountToolbar = binding.accountToolbar;
        accountToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        accountToolbar.setNavigationOnClickListener(this::navigateBack);

        nameSectionView.setOnClickListener(this::setNameSectionViewAction);
        emailAddressSectionView.setOnClickListener(this::emailAddressSectionAction);
        changePasswordSectionView.setOnClickListener(this::setChangePasswordSectionViewAction);
        deleteAccountSectionView.setOnClickListener(this::setDeleteAccountSectionViewAction);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameTextView.setText(accountHelper.getUserName());
        emailTextView.setText(accountHelper.getUserEmail());
    }

    private void handlePressBack(){
        navigateBack(getView());
    }


    private void navigateBack(View view){
        NavDirections profileNavDirections = AccountFragmentDirections.actionAccountFragmentToProfileFragment();
        Navigation.findNavController(getView()).navigate(profileNavDirections);
        mainActivityViewModel.setHandleBackPress(null);
    }



    private void emailAddressSectionAction(View view){
        NavDirections navDirectionToEmail = AccountFragmentDirections.actionAccountFragmentToChangeEmailAddressFragment();
        Navigation.findNavController(getView()).navigate(navDirectionToEmail);

    }

    private void setNameSectionViewAction(View view){
        NavDirections navDirectionToName = AccountFragmentDirections.actionAccountFragmentToChangeNameFragment();
        Navigation.findNavController(getView()).navigate(navDirectionToName);
    }

    private void setChangePasswordSectionViewAction(View view){
        NavDirections navDirectionToPasswd = AccountFragmentDirections.actionAccountFragmentToChangePasswordFragment();
        Navigation.findNavController(getView()).navigate(navDirectionToPasswd);
    }

    private void setDeleteAccountSectionViewAction(View view){

    }
}