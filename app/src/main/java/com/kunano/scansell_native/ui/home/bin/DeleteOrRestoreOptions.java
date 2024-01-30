package com.kunano.scansell_native.ui.home.bin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.R;

public class DeleteOrRestoreOptions extends BottomSheetDialogFragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Get the window of the dialog

        return inflater.inflate(R.layout.home_card_view_business, container, false);
    }

}
