package com.kunano.scansell.components.media_picker;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

public class CustomMediaPicker {
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    ActivityResultLauncher<Intent> pickIntentLauncher;


    // Factory method for PickVisualMediaRequest launcher
    public static CustomMediaPicker fromPickVisualMediaLauncher(ActivityResultLauncher<PickVisualMediaRequest> pickMedia) {
        CustomMediaPicker picker = new CustomMediaPicker();
        picker.pickMedia = pickMedia;
        return picker;
    }

    // Factory method for Intent launcher
    public static CustomMediaPicker fromPickIntentLauncher(ActivityResultLauncher<Intent> pickMedia) {
        CustomMediaPicker picker = new CustomMediaPicker();
        picker.pickIntentLauncher = pickMedia;
        return picker;
    }



    public CustomMediaPicker() {
    }



    public void lunchImagePicker(Activity activity,
                                 ActivityResultContracts.PickVisualMedia.VisualMediaType type){

        if (pickMedia != null){
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(type)
                    .build());
        }
    }

    public void launchFilePickerIntent(Intent intent){
        if (pickIntentLauncher != null)pickIntentLauncher.launch(intent);
    }

    public static Intent getPickFilesIntent(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {
                "application/octet-stream"
        });
        return intent;
    }
}
