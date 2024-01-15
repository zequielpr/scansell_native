package com.kunano.scansell_native.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.kunano.scansell_native.R;


public class SpinningWheel extends DialogFragment {

    private LayoutInflater inflater;
    public SpinningWheel(LayoutInflater inflater) {
        this.inflater = inflater;
    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View customView = inflater.inflate(R.layout.spinning_wheel, null);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());





        builder.setView(customView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Create the AlertDialog object and return it
        return dialog;
    }

}
