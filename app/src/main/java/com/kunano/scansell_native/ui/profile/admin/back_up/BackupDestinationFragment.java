package com.kunano.scansell_native.ui.profile.admin.back_up;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kunano.scansell_native.databinding.FragmentBackupDestinationBinding;


public class BackupDestinationFragment extends BottomSheetDialogFragment {
    private BackUpDestinationListener backUpDestinationListener;
    private FragmentBackupDestinationBinding binding;
    private ImageButton deviceImageButton;
    private ImageButton driveImageButton;
    private TextView optionsTitleTextView;
    private String title;

    public BackupDestinationFragment() {
    }

    public BackupDestinationFragment(String title) {
        // Required empty public constructor
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentBackupDestinationBinding.inflate(inflater, container, false);
        deviceImageButton = binding.deviceImageButton;
        driveImageButton = binding.driveImageButton;
        optionsTitleTextView = binding.optionsTitleTextView;

        optionsTitleTextView.setText(title);
        deviceImageButton.setOnClickListener(backUpDestinationListener::onDevice);
        driveImageButton.setOnClickListener(backUpDestinationListener::onDrive);

        return binding.getRoot();
    }


    public interface BackUpDestinationListener{
        abstract void onDevice(View view);
        abstract void onDrive(View view);
    }

    public BackUpDestinationListener getBackUpDestinationListener() {
        return backUpDestinationListener;
    }

    public void setBackUpDestinationListener(BackUpDestinationListener backUpDestinationListener) {
        this.backUpDestinationListener = backUpDestinationListener;
    }
}