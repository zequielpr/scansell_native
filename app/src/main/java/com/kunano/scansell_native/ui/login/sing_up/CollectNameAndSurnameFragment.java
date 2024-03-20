package com.kunano.scansell_native.ui.login.sing_up;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell_native.R;
import com.kunano.scansell_native.ui.profile.admin.account.name.NameViewModel;


public class CollectNameAndSurnameFragment extends Fragment {
    private EditText editTextName;
    private TextView nameWarnTextView;
    private NameViewModel nameViewModel;
    private Toolbar nameToolbar;
    private Button continueButton;
    private SignUpViewModel signUpViewModel;


    public CollectNameAndSurnameFragment() {
        // Required empty public constructor
    }


/*    public static CollectNameAndSurnameFragment newInstance(String param1, String param2) {
        CollectNameAndSurnameFragment fragment = new CollectNameAndSurnameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nameViewModel = new ViewModelProvider(this).get(NameViewModel.class);
        signUpViewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_name, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        nameWarnTextView = view.findViewById(R.id.newNameWarnTextView);
        continueButton = view.findViewById(R.id.buttonSave);
        nameToolbar = view.findViewById(R.id.changeNameToolbar);

        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameToolbar.setVisibility(View.GONE);
        continueButton.setText(getText(R.string.continue_action));
        editTextName.setHint(getText(R.string.introduce_name));
        nameViewModel.getNewNameWarnMutableData().observe(getViewLifecycleOwner(),
                nameWarnTextView::setText);

        continueButton.setOnClickListener(this::navigateToCollectPasswd);

    }

    public void navigateToCollectPasswd(View view){
        String name = editTextName.getText().toString().trim();
        if (!nameViewModel.validateName(name))return;
        signUpViewModel.setName(name);

        NavDirections navDirectionsToCollectPasswd = CollectNameAndSurnameFragmentDirections.
                actionCollectNameAndSurnameFragmentToCollectPasswdFragment2();
        Navigation.findNavController(getView()).navigate(navDirectionsToCollectPasswd);
    }


}