package com.kunano.scansell_native.ui.profile.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.databinding.FragmentAdminBinding;
import com.kunano.scansell_native.ui.profile.ProfileFragmentDirections;

public class AdminFragment extends BottomSheetDialogFragment {
    View settingSetcion;
    View accountSection;
    View backUpSection;
    View parentView;

    FragmentAdminBinding binding;

    public AdminFragment(View parentView){
        this.parentView = parentView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAdminBinding.inflate(inflater, container, false);


        settingSetcion = binding.settingSection;
        accountSection = binding.accountSection;
        backUpSection = binding.backUpSection;

        settingSetcion.setOnClickListener(this::settingSectionAction);
        accountSection.setOnClickListener(this::accountSectionAction);
        backUpSection.setOnClickListener(this::backUpSectionAction);



        return binding.getRoot();
    }


    private void settingSectionAction(View view){
        NavDirections navDirectionsSetting = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment23();
        Navigation.findNavController(parentView).navigate(navDirectionsSetting);
        dismiss();

    }

    private void accountSectionAction(View view){
        NavDirections navDirectionsAccount = ProfileFragmentDirections.actionProfileFragmentToAccountFragment();
        Navigation.findNavController(parentView).navigate(navDirectionsAccount);
        dismiss();
    }

    private void backUpSectionAction(View view){
        /*BuyBackUpFunctionFragment buyBackUpFunctionFragment = new BuyBackUpFunctionFragment();
        buyBackUpFunctionFragment.show(getParentFragmentManager(), "buy function");*/

       NavDirections navDirectionsBackUp = ProfileFragmentDirections.actionProfileFragmentToBackUpFragment();
        Navigation.findNavController(parentView).navigate(navDirectionsBackUp);
        dismiss();
    }
}