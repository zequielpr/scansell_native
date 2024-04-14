package com.kunano.scansell_native.ui.profile.admin;

import android.app.Application;

import com.qonversion.android.sdk.Qonversion;

public class QonversionApp extends Application {

    public void onCreate() {
        super.onCreate();
        Qonversion.setDebugMode();
        Qonversion.launch(this, "", false);
    }
}