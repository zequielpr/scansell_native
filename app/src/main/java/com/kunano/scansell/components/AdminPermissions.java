package com.kunano.scansell.components;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.kunano.scansell.R;

import java.util.Map;

public class AdminPermissions {
    private Context context;
    Fragment fragment;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    private ViewModelListener<Boolean> resultListener;

    public AdminPermissions(Fragment fragment){
        super();
        this.fragment = fragment;
        this.context = fragment.getContext();
        requestPermissionLauncher =
                fragment.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::handlePermission);

    }

    public void handlePermission(Map<String, Boolean> result){
        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
            String permission = entry.getKey();
            Boolean isGranted = entry.getValue();
            if (isGranted) {
                // Permission granted
                Log.d("Permissions", permission + " granted.");
            } else {
                // Permission denied
                Log.d("Permissions", permission + " denied.");
                resultListener.result(false);
                break;
            }
        }
        //resultListener.result(isGranted);
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
            requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
        }
    }



    public void checkMediaPermission(){
        if (checkMediaPermissionList()) {
            resultListener.result(true);
            // You can use the API that requires the permission.

        } else if (shouldShowRequestPermissionRationale()) {
            resultListener.result(false);

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(permissions());
        }

    }

    private boolean shouldShowRequestPermissionRationale() {
        String[] permissions = permissions();

        boolean allPermissionsGranted = false;
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    fragment.getActivity(), permission)) {
                allPermissionsGranted = true;
                break;
            }
        }
        return allPermissionsGranted;
    }




    private boolean checkMediaPermissionList() {
        String[] permissions = permissions();

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(fragment.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        return allPermissionsGranted;
    }


    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
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
