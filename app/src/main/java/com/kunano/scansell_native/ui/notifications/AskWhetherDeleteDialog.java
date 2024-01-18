package com.kunano.scansell_native.ui.notifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.kunano.scansell_native.ListenResponse;
import com.kunano.scansell_native.R;

public class AskWhetherDeleteDialog extends DialogFragment {

    LayoutInflater inflater;
    ListenResponse buttonAction;
    String title;

    public AskWhetherDeleteDialog(LayoutInflater inflater, ListenResponse buttonAction, String title) {
        super();
        this.inflater = inflater;
        this.buttonAction = buttonAction;
        this.title = title;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

        // Reference views in the custom layout
        TextView customDialogTitle = dialogView.findViewById(R.id.customDialogTitle);
        TextView customDialogMessage = dialogView.findViewById(R.id.customDialogMesage);
        Button customDialogOkButton = dialogView.findViewById(R.id.customDialogOkButton);
        Button customDialogCancelButton = dialogView.findViewById(R.id.customDialogCancelButton);

        customDialogTitle.setText(getString(R.string.delete_businesses_title));
        customDialogMessage.setText("");


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(dialogView);
        //Button ok action
        customDialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAction.isSuccessfull(true);
                dismiss();
            }
        });

        //Button cancel action
        customDialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAction.isSuccessfull(false);
               dismiss();
            }
        });

        Dialog customDialog = builder.create();

        return customDialog;
    }
}
