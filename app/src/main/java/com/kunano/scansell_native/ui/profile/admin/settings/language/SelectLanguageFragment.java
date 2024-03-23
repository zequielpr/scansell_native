package com.kunano.scansell_native.ui.profile.admin.settings.language;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kunano.scansell_native.databinding.FragmentSelectLanguageBinding;
import com.kunano.scansell_native.model.db.SharePreferenceHelper;
import com.kunano.scansell_native.ui.components.ViewModelListener;


public class SelectLanguageFragment extends DialogFragment {
    public static String TAG = "Select language";
    RadioGroup radioGroupButton;
    FragmentSelectLanguageBinding binding;
    private ViewModelListener<String> languageListener;
    private SharePreferenceHelper sharePreferenceHelper;


    public SelectLanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharePreferenceHelper = new SharePreferenceHelper(getActivity(), MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentSelectLanguageBinding.inflate(inflater, container, false);

        radioGroupButton = binding.radioGroupOptions;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (sharePreferenceHelper != null){
            RadioButton checkedRadioButton =  radioGroupButton.findViewWithTag(sharePreferenceHelper.getLanguage());
            checkedRadioButton.setChecked(true);
        }
        radioGroupButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = radioGroup.findViewById(i);
                if (checkedRadioButton != null & languageListener != null){
                    languageListener.result(checkedRadioButton.getTag().toString());
                }
                dismiss();

            }
        });
    }


    public void setLanguageListener(ViewModelListener<String> languageListener) {
        this.languageListener = languageListener;
    }
}