package com.kunano.scansell_native.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.R;

public class ProgressBarDialog{
    private LayoutInflater layoutInflater;
    private MutableLiveData<String> itemsData;
    private MutableLiveData<Integer> progress;

    private LifecycleOwner lifecycleOwner;

    private Context context;

    private TextView customDialogTitle;
    private ProgressBar progressBar;
    private TextView textViewItems;
    private TextView percentage;
    private Button cancelButton;
    private View dialogView;
    String title;

    public ProgressBarDialog(LayoutInflater layoutInflater, MutableLiveData<String> itemsData,
                             MutableLiveData<Integer> progress, LifecycleOwner lifecycleOwner,
                             Context context, String title) {
        this.layoutInflater = layoutInflater;
        this.itemsData = itemsData;
        this.progress = progress;
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.title = title;

    }


    public View getDialogView() {
        return dialogView;
    }

    public void setDialogView(View dialogView) {
        this.dialogView = dialogView;
    }

    public AlertDialog.Builder getProgressBarDeletingBusiness(){
        // Reference views in the custom layout
        dialogView = layoutInflater.inflate(R.layout.deleting_progress_bar, null);
        customDialogTitle = dialogView.findViewById(R.id.progress_bar_title);
        progressBar = dialogView.findViewById(R.id.progress_bar);
        textViewItems = dialogView.findViewById(R.id.progress_bar_items);
        percentage = dialogView.findViewById(R.id.progress_bar_percentage);
        cancelButton = dialogView.findViewById(R.id.progress_bar_cancel_button);


        customDialogTitle.setText(title);
        progress.observe(lifecycleOwner, progressBar::setProgress);
        itemsData.observe(lifecycleOwner, textViewItems::setText);
        progress.observe(lifecycleOwner, (Integer progress)-> percentage.setText(String.valueOf(progress).concat("%")));


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(dialogView);

        return builder;
    }


    public Button getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }
}
