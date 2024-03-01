package com.kunano.scansell_native.ui.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;

public class ProgressBarDialog extends DialogFragment {
    private LayoutInflater layoutInflater;


    private TextView customDialogTitle;
    private ProgressBar progressBar;
    private TextView textViewItemsToDelete;
    private TextView percentage;
    private Button cancelButton;
    private View customView;
    MutableLiveData<Integer> progress;
    MutableLiveData<String> deletedItems;

    LifecycleOwner lifecycleOwner;
    String title;
    ListenResponse action;

    public ProgressBarDialog( ListenResponse action, LayoutInflater layoutInflater, String title,  LifecycleOwner lifecycleOwner,
                             MutableLiveData<Integer> progress, MutableLiveData<String> deletedItems) {
        this.action = action;
        this.layoutInflater = layoutInflater;
        this.title = title;
        this.lifecycleOwner = lifecycleOwner;
        this.progress = progress;
        this.deletedItems = deletedItems;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        customView = layoutInflater.inflate(R.layout.deleting_progress_bar, null);
        customDialogTitle = customView.findViewById(R.id.progress_bar_title);
        progressBar = customView.findViewById(R.id.progress_bar);
        textViewItemsToDelete = customView.findViewById(R.id.progress_bar_items);
        percentage = customView.findViewById(R.id.progress_bar_percentage);
        cancelButton = customView.findViewById(R.id.progress_bar_cancel_button);

        progress.observe(lifecycleOwner, (p)-> percentage.setText(Integer.toString(p).concat("%")));
        progress.observe(lifecycleOwner, progressBar::setProgress);
        deletedItems.observe(lifecycleOwner, textViewItemsToDelete::setText);

        customDialogTitle.setText(title);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(customView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.isSuccessfull(true
                );
            }
        });

        return dialog;



    }


    public View getCustomView() {
        return customView;
    }

    public void setCustomView(View customView) {
        this.customView = customView;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }
}
