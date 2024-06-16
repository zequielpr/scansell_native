package com.kunano.scansell.ui.introduction.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.kunano.scansell.R;
import com.kunano.scansell.databinding.FragmentSectionTwoBinding;

public class SectionTwoFragment extends Fragment {
    public static int SECTION_NUMBER = 1;

    private FragmentSectionTwoBinding binding;

    public SectionTwoFragment() {
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

        binding = FragmentSectionTwoBinding.inflate(getLayoutInflater());

        binding.nextToPage3.setOnClickListener(this::navigateToSectionThree);

        return binding.getRoot();
    }

    private void navigateToSectionThree(View view){
        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
        if (viewPager != null)viewPager.setCurrentItem(SectionThreeFragment.SECTION_NUMBER);
    }

}
