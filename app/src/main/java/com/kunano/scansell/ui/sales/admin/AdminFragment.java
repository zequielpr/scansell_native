package com.kunano.scansell.ui.sales.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell.MainActivityViewModel;
import com.kunano.scansell.databinding.FragmentAdminBinding;
import com.kunano.scansell.ui.sales.SalesFragmentDirections;

public class AdminFragment extends BottomSheetDialogFragment {
    View settingSection;
    View backUpSection;
    View parentView;

    FragmentAdminBinding binding;

    MainActivityViewModel mainActivityViewModel;

    public AdminFragment() {
    }

    public AdminFragment(View parentView) {
        this.parentView = parentView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAdminBinding.inflate(inflater, container, false);


        settingSection = binding.settingSection;
        //accountSection = binding.accountSection;
        backUpSection = binding.backUpSection;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingSection.setOnClickListener(this::settingSectionAction);
        //accountSection.setOnClickListener(this::accountSectionAction);
        backUpSection.setOnClickListener(this::backUpSectionAction);
    }

    private void settingSectionAction(View view) {

        NavDirections navDirectionsSettings = SalesFragmentDirections.actionSalesFragmentToSettingsFragment2();
        Navigation.findNavController(parentView).navigate(navDirectionsSettings);
        dismiss();
    }

    private void backUpSectionAction(View view) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {

                NavDirections navDirectionsBackUp = SalesFragmentDirections.actionSalesFragmentToBackUpFragment();
                Navigation.findNavController(parentView).navigate(navDirectionsBackUp);
                dismiss();
            });
        }
    }


}