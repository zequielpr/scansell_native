package com.kunano.scansell_native.ui.profile.admin.back_up;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentBackUpBinding;

public class BackUpFragment extends Fragment {
    private FragmentBackUpBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private Toolbar backupToolbar;
    private View createBackupSection;
    private View restoreBackUpSection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentBackUpBinding.inflate(inflater, container, false);

        backupToolbar = binding.backupToolbar;
        createBackupSection = binding.createBackupSection;
        restoreBackUpSection = binding.restoreBackupSection;

        backupToolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        backupToolbar.setNavigationOnClickListener(this::navigateBack);

        mainActivityViewModel.setHandleBackPress(this::handlePressBack);


        restoreBackUpSection.setOnClickListener(this::setRestoreBackUpSection);
        createBackupSection.setOnClickListener(this::setCreateBackupSectionAction);


        return binding.getRoot();
    }


    private void handlePressBack(){
        navigateBack(getView());
    }


    private void navigateBack(View view){
        NavDirections profileNavDirections = BackUpFragmentDirections.actionBackUpFragmentToProfileFragment();
        Navigation.findNavController(getView()).navigate(profileNavDirections);
        mainActivityViewModel.setHandleBackPress(null);
    }


    private void setCreateBackupSectionAction(View view){

    }

    private void setRestoreBackUpSection(View view){

    }
}