package io.zentechgh.dms.mobile.app.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapterHome extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments;
    ArrayList<String> titles;
    ArrayList<Integer> icons;

    public ViewPagerAdapterHome(FragmentManager fm){
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
        this.icons = new ArrayList<>();
    }

    // returns each position of fragment
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String title, int icon){
        fragments.add(fragment);
        titles.add(title);
        icons.add(icon);
    }

    // Ctrl + O
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
