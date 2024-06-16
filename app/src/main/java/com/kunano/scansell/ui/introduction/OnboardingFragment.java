package com.kunano.scansell.ui.introduction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.kunano.scansell.databinding.FragmentOnboardingBinding;
import com.kunano.scansell.ui.introduction.sections.SectionOneFragment;
import com.kunano.scansell.ui.introduction.sections.SectionThreeFragment;
import com.kunano.scansell.ui.introduction.sections.SectionTwoFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnboardingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnboardingFragment extends Fragment {
    private FragmentOnboardingBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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


        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SectionOneFragment());
        fragmentList.add(new SectionTwoFragment());
        fragmentList.add(new SectionThreeFragment());

        ViewPagerAdapter adapter = new ViewPagerAdapter(
                fragmentList,
                requireActivity().getSupportFragmentManager(),
                getLifecycle()
        );

        binding.viewPager.setAdapter(adapter);
        binding.dotsIndicator.attachTo(binding.viewPager);

        return binding.getRoot();
    }

}