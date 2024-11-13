package com.kunano.scansell.ui.introduction;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;


public class ViewPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> fragmentList;

    public ViewPagerAdapter(ArrayList<Fragment> list, FragmentManager fm, Lifecycle lifecycle) {
        super(fm, lifecycle);
        this.fragmentList = list;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }


}
