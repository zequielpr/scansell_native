package com.kunano.scansell_native.model.db;

import android.app.Activity;
import android.content.SharedPreferences;

import com.kunano.scansell_native.repository.share_preference.SettingRepository;

public class SharePreferenceHelper {

    private   SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private static String FOLDER_ID_KEY = "FOLDER_ID_KEY";
    public SharePreferenceHelper(Activity activity, Integer mode) {
        sharedPref = activity.getPreferences(mode);
    }


    public void setSound(Integer sound){
        editor = sharedPref.edit();
        editor.putInt(SettingRepository.SOUND_KEY, sound);
        editor.apply();
    }

    public Integer getSound(){
        Integer sound = sharedPref.getInt(SettingRepository.SOUND_KEY, SettingRepository.BEEP_SOUND);
        return sound;
    }

    public void adminSoundStatus(Boolean isActive){
        editor = sharedPref.edit();
        editor.putBoolean(SettingRepository.SOUND_STATUS_KEY, isActive);
        editor.apply();
    }

    public boolean isSoundactive(){
        return sharedPref.getBoolean(SettingRepository.SOUND_STATUS_KEY, true);
    }

    public void setDriveFolderId(String folderId){
        editor = sharedPref.edit();
        editor.putString(FOLDER_ID_KEY, folderId);
        editor.apply();
    }

    public String getDriveFolderId(){
        return sharedPref.getString(FOLDER_ID_KEY, null);
    }


}
