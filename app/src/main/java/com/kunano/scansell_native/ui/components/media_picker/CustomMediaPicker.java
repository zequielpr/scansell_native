package com.kunano.scansell_native.ui.components.media_picker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

public class CustomMediaPicker {
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;


    public CustomMediaPicker(ActivityResultLauncher<PickVisualMediaRequest> pickMedia) {
        this.pickMedia = pickMedia;
    }

    public void lunchImagePicker( ActivityResultContracts.PickVisualMedia.VisualMediaType type){
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(type)
                .build());
    }
}
