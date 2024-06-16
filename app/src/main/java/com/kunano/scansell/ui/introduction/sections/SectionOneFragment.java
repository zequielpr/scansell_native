package com.kunano.scansell.ui.introduction.sections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.kunano.scansell.R;
import com.kunano.scansell.components.Utils;
import com.kunano.scansell.databinding.FragmentSectionOneBinding;


public class SectionOneFragment extends Fragment {
    public static int SECTION_NUMBER = 0;
    private View container;
    private View scanningLine;

    private FragmentSectionOneBinding binding;


    public SectionOneFragment() {
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
        binding = FragmentSectionOneBinding.inflate(getLayoutInflater());

        container = binding.contentOne;
        scanningLine = binding.scanningLine;

        binding.nextToPage2.setOnClickListener(this::navigateToSectionTwo);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Utils.startAnimationOfScanningLine(this, scanningLine, container);
    }


    @Override
    public void onPause() {
        super.onPause();
        finishAnimation();
    }

    @Override
    public void onStop() {
        super.onStop();
        finishAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       finishAnimation();
    }

    private void finishAnimation(){
        if (scanningLine == null) return;
        Utils.finishScanningLineAnim(scanningLine);
    }



    private void navigateToSectionTwo(View view){
        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
        if (viewPager != null)viewPager.setCurrentItem(SectionTwoFragment.SECTION_NUMBER);
    }
}