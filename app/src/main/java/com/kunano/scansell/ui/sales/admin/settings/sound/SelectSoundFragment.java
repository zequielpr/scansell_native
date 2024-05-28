package com.kunano.scansell.ui.sales.admin.settings.sound;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.kunano.scansell.databinding.FragmentSelectSoundBinding;
import com.kunano.scansell.repository.share_preference.SettingRepository;
import com.kunano.scansell.ui.sales.admin.settings.SettingViewModel;

public class SelectSoundFragment extends DialogFragment {

   SettingViewModel settingViewModel;
   SettingRepository settingRepository;
   private RadioGroup radioGroupSounds;
   private RadioButton radioButtonBeep;
   private RadioButton radioButtonVibrate;
   private FragmentSelectSoundBinding binding;

    public SelectSoundFragment() {
    }

    public SelectSoundFragment(SettingViewModel settingViewModel) {
        this.settingViewModel = settingViewModel;
    }

  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingRepository = new SettingRepository(getActivity(), Context.MODE_PRIVATE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSelectSoundBinding.inflate(inflater, container, false);


        radioGroupSounds = binding.radioGroupSounds;
        radioButtonBeep = binding.beepRadioButton;
        radioButtonVibrate = binding.vibrateRadioButton;


        radioButtonBeep.setOnCheckedChangeListener(this::setCurrentSound);
        radioButtonVibrate.setOnCheckedChangeListener(this::setCurrentSound);


        return binding.getRoot();
    }

    private void setCurrentSound( CompoundButton compoundButton, boolean b){
       if (b){
           Integer currentSound = Integer.parseInt(compoundButton.getTag().toString());
           settingRepository.setSound(currentSound);
           settingViewModel.setCurrentSound(currentSound);
       }
    }

    public void onViewCreated(@NonNull View view, @NonNull Bundle savedState){
        super.onViewCreated(view, savedState);

        Integer currentSound = settingRepository.getSound();
        RadioButton currentSoundRadioButton = (RadioButton) radioGroupSounds.getChildAt(currentSound);
        currentSoundRadioButton.setChecked(true);
    }
}