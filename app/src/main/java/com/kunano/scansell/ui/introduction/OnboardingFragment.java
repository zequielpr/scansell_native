package com.kunano.scansell.ui.introduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kunano.scansell.databinding.FragmentOnboardingBinding;
import com.kunano.scansell.ui.introduction.sections.SectionOneFragment;
import com.kunano.scansell.ui.introduction.sections.SectionThreeFragment;
import com.kunano.scansell.ui.introduction.sections.SectionTwoFragment;

import java.util.ArrayList;

public class OnboardingFragment extends Fragment {
    private FragmentOnboardingBinding binding;
    ArrayList<Fragment> fragmentList;


    public OnboardingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentOnboardingBinding.inflate(getLayoutInflater());


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentList = new ArrayList<>();
        fragmentList.add(new SectionOneFragment());
        fragmentList.add(new SectionTwoFragment());
        fragmentList.add(new SectionThreeFragment());

        ViewPagerAdapter adapter = new ViewPagerAdapter(
                fragmentList,
                requireActivity().getSupportFragmentManager(),
                requireActivity().getLifecycle()
        );

        binding.viewPager.setAdapter(adapter);
        binding.dotsIndicator.attachTo(binding.viewPager);
    }
}