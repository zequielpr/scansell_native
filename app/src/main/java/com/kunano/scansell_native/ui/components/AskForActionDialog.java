package com.kunano.scansell_native.ui.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.kunano.scansell_native.R;

public class AskForActionDialog extends DialogFragment {

    private LayoutInflater inflater;
    private ListenResponse buttonListener;
    private String title;

    private String buttonLeftText;
    private String buttonRightText;
    private  String content;

    public AskForActionDialog(LayoutInflater inflater, String title) {
        super();
        this.inflater = inflater;
        this.title = title;
    }
    public AskForActionDialog(LayoutInflater inflater, String title,
                              String buttonLeftText, String buttonRightText) {
        super();
        this.inflater = inflater;
        this.title = title;
        this.buttonLeftText = buttonLeftText;
        this.buttonRightText = buttonRightText;
    }

    public AskForActionDialog(LayoutInflater inflater, String title, String content,
                              String buttonLeftText, String buttonRightText) {
        super();
        this.inflater = inflater;
        this.title = title;
        this.content = content;
        this.buttonLeftText = buttonLeftText;
        this.buttonRightText = buttonRightText;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);

        // Reference views in the custom layout
        TextView customDialogTitle = dialogView.findViewById(R.id.customDialogTitle);
        TextView customDialogMessage = dialogView.findViewById(R.id.customDialogMesage);
        Button customDialogOkButton = dialogView.findViewById(R.id.customDialogOkButton);
        Button customDialogCancelButton = dialogView.findViewById(R.id.customDialogCancelButton);

        customDialogTitle.setText(title);
        customDialogMessage.setText(content!=null?content:"");
        if(buttonRightText != null & buttonRightText != null){
            customDialogOkButton.setText(buttonRightText);
            customDialogCancelButton.setText(buttonLeftText);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(dialogView);
        //Button ok action
        customDialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonListener.isSuccessfull(true);
                dismiss();
            }
        });

        //Button cancel action
        customDialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonListener.isSuccessfull(false);
               dismiss();
            }
        });

        Dialog customDialog = builder.create();

        return customDialog;
    }

    public ListenResponse getButtonListener() {
        return buttonListener;
    }

    public void setButtonListener(ListenResponse buttonListener) {
        this.buttonListener = buttonListener;
    }
}
