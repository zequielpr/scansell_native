package com.kunano.scansell_native.repository.share_preference;

import android.app.Activity;

import com.kunano.scansell_native.model.db.SharePreferenceHelper;

public class SettingRepository {
    public static Integer BEEP_SOUND = 0;
    public static Integer VIBRATION_SOUND = 1;
    public static String SOUND_KEY = "SOUND_KEY";
    public static String SOUND_STATUS_KEY = "SOUND_STATUS_KEY";
    SharePreferenceHelper sharePreferenceHelper;

    public SettingRepository(Activity activity, Integer mode){
        sharePreferenceHelper = new SharePreferenceHelper(activity, mode);
    }


    public void setSound(Integer sound){
        sharePreferenceHelper.setSound(sound);
    }

    public Integer getSound(){
        return  sharePreferenceHelper.getSound();
    }


    public void adminSoundStatus(Boolean isActive){
        sharePreferenceHelper.adminSoundStatus(isActive);
    }

    public boolean isSoundActive(){
        return  sharePreferenceHelper.isSoundactive();
    }

}
