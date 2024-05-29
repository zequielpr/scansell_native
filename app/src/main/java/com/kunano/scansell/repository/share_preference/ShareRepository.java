package com.kunano.scansell.repository.share_preference;

import android.app.Activity;

import com.kunano.scansell.model.db.SharePreferenceHelper;

public class ShareRepository {
    public static String IS_FIRST_START = "IS FIRST START";

    private SharePreferenceHelper sharePreferenceHelper;

    public ShareRepository(Activity activity, Integer mode){
        sharePreferenceHelper = new SharePreferenceHelper(activity, mode);
    }


    public void setIsFirstStart(boolean isFirstStart){
        sharePreferenceHelper.setIsFirstStart(isFirstStart);
    }

    public boolean isFirstStart(){
        return sharePreferenceHelper.isFirstStart();
    }
}
