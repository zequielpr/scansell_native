package com.kunano.scansell_native.ui.profile.admin.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.MainActivityViewModel;
import com.kunano.scansell_native.R;
import com.kunano.scansell_native.databinding.FragmentSettingsBinding;
import com.kunano.scansell_native.repository.share_preference.SettingRepository;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private Toolbar settingsToolBar;
    private View languageSetcion;
    private Switch soundAfterScanSectionSwitch;
    private TextView curretnSoundTextView;
    private ImageView soundStateImageView;
    private TextView currentLanguageTextView;
    private SettingViewModel settingViewModel;
    private SettingRepository settingRepository;
    private boolean soudState;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        settingRepository = new SettingRepository(getActivity(), Context.MODE_PRIVATE);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        settingsToolBar = binding.settingsToolbar;
        languageSetcion = binding.languageSection;
        soundAfterScanSectionSwitch = binding.soundSection;
        soundStateImageView = binding.soundStateImageView;
        currentLanguageTextView = binding.actualLanguageTextView;
        curretnSoundTextView = binding.currentSoundTextView;

        settingsToolBar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        settingsToolBar.setNavigationOnClickListener(this::navigateBack);

        mainActivityViewModel.setHandleBackPress(this::handlePressBack);
        settingViewModel.getSoundState().observe(getViewLifecycleOwner(), this::handleSoundState);
        settingViewModel.getCurrentSound().observe(getViewLifecycleOwner(), this::handleCurrentSound);

        languageSetcion.setOnClickListener(this::pressLanguageAction);
        soundAfterScanSectionSwitch.setOnCheckedChangeListener(this::adminSound);
        curretnSoundTextView.setOnClickListener(this::selectCurrentSound);

        return binding.getRoot();
    }



    Integer currentSound;
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedState){
        super.onViewCreated(view, savedState);
        soudState = settingRepository.isSoundActive();
        currentSound = settingRepository.getSound();

        soundAfterScanSectionSwitch.setChecked(soudState);
        settingViewModel.setSoundState(soudState);
        settingViewModel.setCurrentSound(currentSound);
    }



    private void handlePressBack(){
        navigateBack(getView());
    }

    private void handleSoundState(boolean state){
        if (state) {
            soundStateImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.sound));
        } else {
            soundStateImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.not_sound));
        }
    }

    private void handleCurrentSound(Integer currentSound){
        if (currentSound == SettingRepository.BEEP_SOUND){
            curretnSoundTextView.setText(getString(R.string.beep));
        }else {
            curretnSoundTextView.setText(getString(R.string.vibrate));
        }
    }


    private void navigateBack(View view){
        NavDirections profileNavDirections = SettingsFragmentDirections.actionSettingsFragment2ToProfileFragment();
        Navigation.findNavController(getView()).navigate(profileNavDirections);
        mainActivityViewModel.setHandleBackPress(null);
    }


    private void pressLanguageAction(View view){

    }

    private void selectCurrentSound(View view){
        SelectSoundFragment selectSoundFragment = new SelectSoundFragment(settingViewModel);
        selectSoundFragment.show(getChildFragmentManager(), "Select sound");
    }


    //Activate or desactivate sound
    private void adminSound(CompoundButton compoundButton, boolean b) {
        settingRepository.adminSoundStatus(b);
        settingViewModel.setSoundState(b);
    }






}