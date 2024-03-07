package com.kunano.scansell_native.ui.profile.admin.settings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kunano.scansell_native.repository.share_preference.SettingRepository;

public class SettingViewModel extends ViewModel {
    private MutableLiveData<Boolean> soundState;
    private MutableLiveData<Integer> currentSound;

    public SettingViewModel(){
        super();
        soundState = new MutableLiveData<>(true);
        currentSound = new MutableLiveData<>(SettingRepository.BEEP_SOUND);
    }

    public MutableLiveData<Boolean> getSoundState() {
        return soundState;
    }

    public void setSoundState(Boolean soundState) {
        this.soundState.postValue(soundState);
    }

    public MutableLiveData<Integer> getCurrentSound() {
        return currentSound;
    }

    public void setCurrentSound(Integer currentSound) {
        this.currentSound.postValue(currentSound);
    }
}
