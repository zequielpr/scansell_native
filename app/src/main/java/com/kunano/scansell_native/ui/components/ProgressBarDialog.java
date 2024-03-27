package com.kunano.scansell_native.ui.components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.kunano.scansell_native.databinding.DeletingProgressBarBinding;

public class ProgressBarDialog extends DialogFragment {

    DeletingProgressBarBinding binding;



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


    public ProgressBarDialog( String title,  LifecycleOwner lifecycleOwner,
                             MutableLiveData<Integer> progress, MutableLiveData<String> deletedItems) {
        this.title = title;
        this.lifecycleOwner = lifecycleOwner;
        this.progress = progress;
        this.deletedItems = deletedItems;

    }

    public ProgressBarDialog( String title,  LifecycleOwner lifecycleOwner,
                              MutableLiveData<Integer> progress) {
        this.title = title;
        this.lifecycleOwner = lifecycleOwner;
        this.progress = progress;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DeletingProgressBarBinding.inflate(inflater, container, false);

        customDialogTitle = binding.progressBarTitle;
        progressBar = binding.progressBar;
        textViewItemsToDelete = binding.progressBarItems;
        percentage = binding.progressBarPercentage;
        cancelButton = binding.progressBarCancelButton;

        progress.observe(lifecycleOwner, (p)-> percentage.setText(Integer.toString(p).concat("%")));
        progress.observe(lifecycleOwner, progressBar::setProgress);


        if (deletedItems != null){
            deletedItems.observe(lifecycleOwner, textViewItemsToDelete::setText);
        }else {
            textViewItemsToDelete.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
        }


        customDialogTitle.setText(title);
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (action != null){
                    action.isSuccessfull(true);
                }
            }
        });


        return binding.getRoot();
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

    public void setAction(ListenResponse action) {
        this.action = action;
    }
}
