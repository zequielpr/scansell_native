package com.kunano.scansell.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kunano.scansell.databinding.AskForActionDialogBinding;

public class AskForActionDialog extends DialogFragment {

    private ViewModelListener<Boolean> buttonListener;
    private String title;
    private String buttonLeftText;
    private String buttonRightText;
    private  String content;
    boolean leftButtonVisibility;
    boolean rightButtonVisibility;
    private AskForActionDialogBinding binding;


    TextView customDialogTitle;
    TextView customDialogMessage;
    Button customDialogOkButton;
    Button customDialogCancelButton;
    private boolean cancelable;

    public AskForActionDialog(String title) {
        super();
        this.title = title;
        this.leftButtonVisibility = true;
        this.rightButtonVisibility = true;
        this.cancelable = true;
    }
    public AskForActionDialog(String title, boolean leftButtonVisibility, boolean rightButtonVisibility) {
        super();
        this.title = title;
        this.leftButtonVisibility = true;
        this.rightButtonVisibility = true;
        this.cancelable = true;
    }


    public AskForActionDialog(String title,
                              String buttonLeftText, String buttonRightText) {
        super();
        this.title = title;
        this.buttonLeftText = buttonLeftText;
        this.buttonRightText = buttonRightText;
        this.leftButtonVisibility = true;
        this.rightButtonVisibility = true;
        this.cancelable = true;
    }
    public AskForActionDialog(String title, String content) {
        super();
        this.title = title;
        this.content = content;
        this.leftButtonVisibility = true;
        this.rightButtonVisibility = true;
        this.cancelable = true;
    }


    public AskForActionDialog(String title, String content,
                              String buttonLeftText, String buttonRightText) {
        super();
        this.title = title;
        this.content = content;
        this.buttonLeftText = buttonLeftText;
        this.buttonRightText = buttonRightText;
        this.leftButtonVisibility = true;
        this.rightButtonVisibility = true;
        this.cancelable = true;
    }

    public AskForActionDialog(String title, String content,
                              String buttonLeftText, String buttonRightText, boolean dialogCancelable) {
        super();
        this.title = title;
        this.content = content;
        this.buttonLeftText = buttonLeftText;
        this.buttonRightText = buttonRightText;
        this.leftButtonVisibility = true;
        this.rightButtonVisibility = true;
        this.cancelable = dialogCancelable;
    }

    public AskForActionDialog(String title, String content, boolean leftButtonVisibility,
                              boolean rightButtonVisibility) {
        super();
        this.title = title;
        this.content = content;
        this.leftButtonVisibility = leftButtonVisibility;
        this.rightButtonVisibility = rightButtonVisibility;
        this.cancelable = true;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AskForActionDialogBinding.inflate(inflater, container, false);



        customDialogTitle = binding.customDialogTitle;
        customDialogMessage = binding.customDialogMesage;
        customDialogOkButton = binding.customDialogOkButton;
        customDialogCancelButton = binding.customDialogCancelButton;

        getDialog().setCancelable(cancelable);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        customDialogTitle.setText(title);
        customDialogMessage.setText(content!=null?content:"");
        customDialogMessage.setVisibility(content==null?View.GONE:View.VISIBLE);
        if(buttonRightText != null & buttonRightText != null){
            customDialogOkButton.setText(buttonRightText);
            customDialogCancelButton.setText(buttonLeftText);
        }
        customDialogOkButton.setVisibility(rightButtonVisibility?View.VISIBLE:View.GONE);
        customDialogCancelButton.setVisibility(leftButtonVisibility?View.VISIBLE:View.GONE);


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
