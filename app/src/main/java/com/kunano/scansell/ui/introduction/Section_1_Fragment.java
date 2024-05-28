package com.kunano.scansell.ui.introduction;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell.MainActivity;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.repository.share_preference.ShareRepository;
import com.kunano.scansell.R;
import com.kunano.scansell.databinding.FragmentSection1Binding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Section_1_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Section_1_Fragment extends Fragment {
    private Button getStartedButton;
    private CheckBox termAndPolicyCheckBox;
    private TextView termsOfServicesTextView;
    private TextView privacyPolicyTextView;

    private FragmentSection1Binding binding;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Section_1_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Section_1_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Section_1_Fragment newInstance(String param1, String param2) {
        Section_1_Fragment fragment = new Section_1_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentSection1Binding.inflate(getLayoutInflater());

        getStartedButton = binding.getStartedButton;
        termAndPolicyCheckBox = binding.termsAndPrivacyView.checkBox;
        termsOfServicesTextView = binding.termsAndPrivacyView.termsOfServiceText;
        privacyPolicyTextView = binding.termsAndPrivacyView.privacyPolicyText;


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getStartedButton.setOnClickListener(this::getStarted);
        privacyPolicyTextView.setOnClickListener(this::goToPrivacyPolicy);
        termsOfServicesTextView.setOnClickListener(this::goToTermsOfService);
    }


    private void goToTermsOfService(View view){
        NavDirections directions = Section_1_FragmentDirections.actionSection1FragmentToTermsOfUseFragment();

        Navigation.findNavController(getView()).navigate(directions);

    }
    private void goToPrivacyPolicy(View view){
        NavDirections directions = Section_1_FragmentDirections.actionSection1FragmentToPrivacyPolicyFragment() ;

        Navigation.findNavController(getView()).navigate(directions);
    }

    private void getStarted(View view){
        if (!termAndPolicyCheckBox.isChecked()){
            Utils.showToast(getActivity(), getString(R.string.accept_terms_and_policies), Toast.LENGTH_SHORT);
            return;
        }
        updateIsFirstTime();
        startMainActivity();
    }




    private void updateIsFirstTime(){
        ShareRepository shareRepository = new ShareRepository(getActivity(), MODE_PRIVATE);
        shareRepository.setIsFirstStart(false);
    }

    private void startMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}