package com.kunano.scansell_native.ui.profile.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentAdminBinding;
import com.kunano.scansell_native.repository.firebase.PremiumRepository;
import com.kunano.scansell_native.ui.profile.ProfileFragmentDirections;
import com.kunano.scansell_native.ui.profile.admin.premium.PremiumViewModel;
import com.kunano.scansell_native.ui.profile.auth.AccountHelper;

public class AdminFragment extends BottomSheetDialogFragment {
    View settingSetcion;
    View accountSection;
    View backUpSection;
    View premiumSection;
    View parentView;
    ImageView premiumImage;

    FragmentAdminBinding binding;

    MainActivityViewModel mainActivityViewModel;
    private PremiumViewModel premiumViewModel;

    public AdminFragment() {
    }

    public AdminFragment(View parentView) {
        this.parentView = parentView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        premiumViewModel = new ViewModelProvider(this).get(PremiumViewModel.class) ;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAdminBinding.inflate(inflater, container, false);


        settingSetcion = binding.settingSection;
        accountSection = binding.accountSection;
        backUpSection = binding.backUpSection;
        premiumSection = binding.premiumSection;
        premiumImage = binding.premiumImage;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingSetcion.setOnClickListener(this::settingSectionAction);
        accountSection.setOnClickListener(this::accountSectionAction);
        backUpSection.setOnClickListener(this::backUpSectionAction);
        premiumSection.setOnClickListener(this::premiumSectionAction);
        premiumViewModel.getSubscriptionState().observe(getViewLifecycleOwner(), (s)->{
            premiumImage.setImageDrawable(ContextCompat.getDrawable(getContext(), s? R.drawable.premium_logo:R.drawable.non_premium_icon));
        });
    }

    private void settingSectionAction(View view) {
        NavDirections navDirectionsSetting = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment23();
        Navigation.findNavController(parentView).navigate(navDirectionsSetting);
        dismiss();

    }

    private void accountSectionAction(View view) {
        NavDirections navDirectionsAccount = ProfileFragmentDirections.actionProfileFragmentToAccountFragment();
        Navigation.findNavController(parentView).navigate(navDirectionsAccount);
        dismiss();
    }

    private AccountHelper accountHelper;
    private PremiumRepository premiumRepository;
    private void backUpSectionAction(View view) {
        if (getActivity() != null){
            getActivity().runOnUiThread(()->{
                NavDirections navDirectionsBackUp = ProfileFragmentDirections.actionProfileFragmentToBackUpFragment();
                Navigation.findNavController(parentView).navigate(navDirectionsBackUp);
                dismiss();
            });
        }
    }


    private void premiumSectionAction(View view){
        NavDirections navDirectionsPremium= ProfileFragmentDirections.actionProfileFragmentToPremiumFragment();
        Navigation.findNavController(parentView).navigate(navDirectionsPremium);
        dismiss();
    }


}