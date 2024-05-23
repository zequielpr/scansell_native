package com.kunano.scansell_native.ui.profile.admin.settings;

import static com.kunano.scansell_native.repository.share_preference.SettingRepository.ENGLISH;
import static com.kunano.scansell_native.repository.share_preference.SettingRepository.LANGUAGE_AUTOMATIC;
import static com.kunano.scansell_native.repository.share_preference.SettingRepository.SPANISH;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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
import com.kunano.scansell_native.databinding.FragmentSettingsBinding;
import com.kunano.scansell_native.model.db.SharePreferenceHelper;
import com.kunano.scansell_native.repository.share_preference.SettingRepository;
import com.kunano.scansell_native.components.Utils;
import com.kunano.scansell_native.ui.profile.admin.settings.language.SelectLanguageFragment;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private Toolbar settingsToolBar;
    private View languageSetcion;
    private Switch soundAfterScanSectionSwitch;
    private ImageView soundStateImageView;
    private TextView currentLanguageTextView;
    private SettingViewModel settingViewModel;
    private SettingRepository settingRepository;
    private Spinner currentSoundSpinner;
    private ArrayAdapter<String> soundsArrayAdapter;
    private SharePreferenceHelper sharePreferenceHelper;
    private boolean soudState;
    private Integer currentSound;
    private View privacyPolicySection;
    private View termsOfUseSection;


    public SettingsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        settingRepository = new SettingRepository(getActivity(), Context.MODE_PRIVATE);
        sharePreferenceHelper = new SharePreferenceHelper(getActivity(), Context.MODE_PRIVATE);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        settingsToolBar = binding.settingsToolbar;
        languageSetcion = binding.languageSection;
        soundAfterScanSectionSwitch = binding.soundSection;
        soundStateImageView = binding.soundStateImageView;
        currentLanguageTextView = binding.actualLanguageTextView;
        currentSoundSpinner = binding.currentSoundSpinner;
        privacyPolicySection = binding.privacyPolicySection;
        termsOfUseSection = binding.termsOfUseSection;

        settingsToolBar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.back_arrow));
        settingsToolBar.setNavigationOnClickListener(this::navigateBack);

        settingViewModel.getSoundState().observe(getViewLifecycleOwner(), this::handleSoundState);
        settingViewModel.getCurrentSound().observe(getViewLifecycleOwner(), currentSoundSpinner::setSelection);

        languageSetcion.setOnClickListener(this::pressLanguageAction);
        soundAfterScanSectionSwitch.setOnCheckedChangeListener(this::adminSound);

        String [] soundsOption ={getString(R.string.beep), getString(R.string.vibrate)};

        soundsArrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, soundsOption);

        soundsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentSoundSpinner.setAdapter(soundsArrayAdapter);



        currentSoundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settingViewModel.setCurrentSound(i);
                sharePreferenceHelper.setSound(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        requireActivity().getOnBackPressedDispatcher().
                addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        System.out.println("back");
                        handleBackPressed();
                    }
                });


        return binding.getRoot();
    }




    public void onViewCreated(@NonNull View view, @NonNull Bundle savedState){
        super.onViewCreated(view, savedState);
        soudState = settingRepository.isSoundActive();
        currentSound = settingRepository.getSound();

        soundAfterScanSectionSwitch.setChecked(soudState);
        settingViewModel.setSoundState(soudState);
        settingViewModel.setCurrentSound(currentSound);
        privacyPolicySection.setOnClickListener(this::goToPrivacyPolicy);
        termsOfUseSection.setOnClickListener(this::goToTermOfService);

        String currentLanguage = sharePreferenceHelper.getLanguage();
        if (currentLanguage.equals(ENGLISH)){
            currentLanguageTextView.setText(getString(R.string.english));
        } else if (currentLanguage.equals(SPANISH)) {
            currentLanguageTextView.setText(getString(R.string.spanish));
        }else {
            currentLanguageTextView.setText(getString(R.string.automatic).concat(
                    " (" + Utils.getDeviceLanguage(getActivity()) + ")"
            ));
        }

    }



    private void handleBackPressed(){
        navigateBack(getView());
    }

    private void handleSoundState(boolean state){
        if (state) {
            soundStateImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.sound));
        } else {
            soundStateImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.not_sound));
        }
    }



    private void navigateBack(View view){
        NavDirections profileNavDirections = SettingsFragmentDirections.actionSettingsFragment2ToProfileFragment();
        Navigation.findNavController(getView()).navigate(profileNavDirections);
    }




    private void pressLanguageAction(View view){
        SelectLanguageFragment selectLanguageFragment = new SelectLanguageFragment();
        selectLanguageFragment.setLanguageListener(this::setLanguage);

        selectLanguageFragment.show(getChildFragmentManager(),SelectLanguageFragment.TAG);
    }

    private void setLanguage(String language){
        if (language.equals(LANGUAGE_AUTOMATIC)){
            Utils.setLanguageAutomatic(getActivity());
        }else {
            Utils.setLanguage(language, getActivity());
        }
        Utils.saveLanguage(language, getActivity());
        navigateBack(getView());
    }



    //Activate or desactivate sound
    private void adminSound(CompoundButton compoundButton, boolean b) {
        settingRepository.adminSoundStatus(b);
        settingViewModel.setSoundState(b);
    }


    private void goToPrivacyPolicy(View view){
        NavDirections directions = SettingsFragmentDirections.actionSettingsFragment2ToPrivacyPolicyFragment2();
        Navigation.findNavController(getView()).navigate(directions);
    }

    private void goToTermOfService(View view){
        NavDirections directions = SettingsFragmentDirections.actionSettingsFragment2ToTermsOfUseFragment2();
        Navigation.findNavController(getView()).navigate(directions);
    }






}