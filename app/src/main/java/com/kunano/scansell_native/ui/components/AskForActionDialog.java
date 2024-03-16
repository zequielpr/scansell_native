package com.kunano.scansell_native.ui.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kunano.scansell_native.databinding.AskForActionDialogBinding;

public class AskForActionDialog extends DialogFragment {

    private ViewModelListener<Boolean> buttonListener;
    private String title;
    private String buttonLeftText;
    private String buttonRightText;
    private  String content;
    private AskForActionDialogBinding binding;


    TextView customDialogTitle;
    TextView customDialogMessage;
    Button customDialogOkButton;
    Button customDialogCancelButton;

    public AskForActionDialog(String title) {
        super();
        this.title = title;
    }
    public AskForActionDialog(String title,
                              String buttonLeftText, String buttonRightText) {
        super();
        this.title = title;
        this.buttonLeftText = buttonLeftText;
        this.buttonRightText = buttonRightText;
    }

    public AskForActionDialog(String title, String content,
                              String buttonLeftText, String buttonRightText) {
        super();
        this.title = title;
        this.content = content;
        this.buttonLeftText = buttonLeftText;
        this.buttonRightText = buttonRightText;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AskForActionDialogBinding.inflate(inflater, container, false);



        customDialogTitle = binding.customDialogTitle;
        customDialogMessage = binding.customDialogMesage;
        customDialogOkButton = binding.customDialogOkButton;
        customDialogCancelButton = binding.customDialogCancelButton;



        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        customDialogTitle.setText(title);
        customDialogMessage.setText(content!=null?content:"");
        if(buttonRightText != null & buttonRightText != null){
            customDialogOkButton.setText(buttonRightText);
            customDialogCancelButton.setText(buttonLeftText);
        }


        //Button ok action
        customDialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonListener != null){
                    buttonListener.result(true);
                }
                dismiss();
            }
        });

        //Button cancel action
        customDialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonListener != null){
                    buttonListener.result(false);
                }
                dismiss();
            }
        });
    }

    public void setButtonListener(ViewModelListener<Boolean> buttonListener) {
        this.buttonListener = buttonListener;
    }
}
