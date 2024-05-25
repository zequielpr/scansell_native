package com.kunano.scansell_native.components;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.kunano.scansell_native.R;

public class AdminPermissions {
    private Context context;
    Fragment fragment;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private ViewModelListener<Boolean> resultListener;

    public AdminPermissions(Fragment fragment){
        super();
        this.fragment = fragment;
        this.context = fragment.getContext();
        requestPermissionLauncher =
                fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(), this::handlePermission);

    }

    public void handlePermission(boolean isGranted){
        resultListener.result(isGranted);
    }

    public void navigateToSettings(View view){
        Intent intent = new Intent( Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", fragment.getActivity().getPackageName(), null);
        intent.setData(uri);
        fragment.getActivity().startActivity(intent);
    }








    public void checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            resultListener.result(true);
            // You can use the API that requires the permission.

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                fragment.getActivity(), Manifest.permission.CAMERA)) {

            resultListener.result(false);

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA);
        }
    }



    public void checkMediaPermission(){
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.MANAGE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            resultListener.result(true);
            // You can use the API that requires the permission.

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                fragment.getActivity(), Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
            resultListener.result(false);

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    public void showDialogToGotoSettings(String title, String message){
        AskForActionDialog askForActionDialog;
        String cancel = fragment.getString(R.string.cancel);
        String ok = fragment.getString(R.string.settings);

        askForActionDialog = new AskForActionDialog(title,
                message, cancel, ok, false);

        askForActionDialog.setButtonListener(new ViewModelListener<Boolean>() {
            @Override
            public void result(Boolean object) {
                if (object){
                    navigateToSettings(fragment.getView());
                }else {
                    askForActionDialog.dismiss();
                }
            }
        });
        askForActionDialog.show(fragment.getParentFragmentManager(), "show option");
    }







    public void setResultListener(ViewModelListener<Boolean> resultListener) {
        this.resultListener = resultListener;
    }
}
