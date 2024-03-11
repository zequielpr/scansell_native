package com.kunano.scansell_native.ui.profile.admin.back_up.backups_in_drive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.kunano.scansell_native.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BackupsInDriveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BackupsInDriveFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backups_in_drive, container, false);
    }
}