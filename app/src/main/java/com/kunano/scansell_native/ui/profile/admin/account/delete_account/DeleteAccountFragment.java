package com.kunano.scansell_native.ui.profile.admin.account.delete_account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.kunano.scansell_native.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeleteAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteAccountFragment extends Fragment {



    public DeleteAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_account, container, false);
    }
}