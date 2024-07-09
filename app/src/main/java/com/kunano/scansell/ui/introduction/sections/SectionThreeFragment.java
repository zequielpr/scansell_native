package com.kunano.scansell.ui.introduction.sections;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.kunano.scansell.MainActivity;
import com.kunano.scansell.R;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.databinding.FragmentSectionThreeBinding;
import com.kunano.scansell.model.db.AppDatabase;
import com.kunano.scansell.repository.share_preference.ShareRepository;
import com.kunano.scansell.ui.introduction.OnboardingFragmentDirections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SectionThreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectionThreeFragment extends Fragment {
    public static int SECTION_NUMBER = 2;
    private Button getStartedButton;
    private CheckBox termAndPolicyCheckBox;
    private TextView termsOfServicesTextView;
    private TextView privacyPolicyTextView;

    private Button finishOnboardButton;
    private ImageView sectionIcon;

    private FragmentSectionThreeBinding binding;
    public SectionThreeFragment() {
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
        binding = FragmentSectionThreeBinding.inflate(getLayoutInflater());


        sectionIcon = binding.sectionThreeBackUpIcon;

        finishOnboardButton = binding.finishOnboardButton;
        termAndPolicyCheckBox = binding.termsAndPrivacyView.checkBox;
        termsOfServicesTextView = binding.termsAndPrivacyView.termsOfServiceText;
        privacyPolicyTextView = binding.termsAndPrivacyView.privacyPolicyText;



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sectionIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.appPColor));
        finishOnboardButton.setOnClickListener(this::finishOnboarding);
        privacyPolicyTextView.setOnClickListener(this::goToPrivacyPolicy);
        termsOfServicesTextView.setOnClickListener(this::goToTermsOfService);
    }

    private void finishOnboarding(View view){
        if (!termAndPolicyCheckBox.isChecked()){
            Utils.showToast(getActivity(), getString(R.string.accept_terms_and_policies), Toast.LENGTH_SHORT);
            return;
        }
        updateIsFirstTime();
        populateDatabase();
        startMainActivity();
    }

    private void populateDatabase(){
        AppDatabase db = AppDatabase.getInstance(getContext());
        AppDatabase.populateDatabase(db);
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

    private void goToTermsOfService(View view){
        NavDirections directions = OnboardingFragmentDirections.actionOnboardingFragmentToTermsOfUseFragment();

        Navigation.findNavController(getView()).navigate(directions);

    }
    private void goToPrivacyPolicy(View view){
        NavDirections directions = OnboardingFragmentDirections.actionOnboardingFragmentToPrivacyPolicyFragment();

        Navigation.findNavController(getView()).navigate(directions);
    }
}