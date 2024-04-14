package com.kunano.scansell_native.ui.profile.admin;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.qonversion.android.sdk.Qonversion;
import com.qonversion.android.sdk.QonversionError;
import com.qonversion.android.sdk.QonversionOfferingsCallback;
import com.qonversion.android.sdk.QonversionPermissionsCallback;
import com.qonversion.android.sdk.dto.QPermission;
import com.qonversion.android.sdk.dto.offerings.QOffering;
import com.qonversion.android.sdk.dto.offerings.QOfferings;

import java.util.List;
import java.util.Map;

public class AdminViewModel extends ViewModel{
    private List<QOffering> offerings;
    private boolean hasPremiumPermission = false;


    public AdminViewModel() {
        updatePermissions();
        loadOfferings();
    }



    public List<QOffering> getOfferings() {
        return offerings;
    }

    public boolean isHasPremiumPermission() {
        return hasPremiumPermission;
    }

    public void setHasPremiumPermission(boolean hasPremiumPermission) {
        this.hasPremiumPermission = hasPremiumPermission;
    }



    private void loadOfferings() {
        Qonversion.offerings(new QonversionOfferingsCallback() {
            @Override
            public void onError(QonversionError error) {
                // Handle error
                Log.d("error", "onError: " + error.getDescription());
            }

            @Override
            public void onSuccess(QOfferings offerings) {
                AdminViewModel.this.offerings = offerings.getAvailableOfferings();
            }
        });
    }

    public void updatePermissions() {
        Qonversion.checkPermissions(new QonversionPermissionsCallback() {
            @Override
            public void onError(QonversionError error) {
                Log.d("TAG", "onError: " + error.getDescription());
            }

            @Override
            public void onSuccess(Map<String, QPermission> permissions) {
                hasPremiumPermission = permissions.get("Premium").isActive();
                Log.d("TAG", "permissions: " + permissions.keySet());
            }
        });
    }
}
