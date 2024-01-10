package com.kunano.scansell_native;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Device extends MainActivity {
    Context context;
    public Device(){
        super();
    }

    public Device(Context context){
        this.context = context;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
