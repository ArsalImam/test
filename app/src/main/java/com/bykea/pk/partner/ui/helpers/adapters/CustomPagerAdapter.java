package com.bykea.pk.partner.ui.helpers.adapters;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import java.util.ArrayList;

public class CustomPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mList = new ArrayList<>();

    public CustomPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        mList = list;
    }

    @Override
    public Fragment getItem(int index) {
        return mList.get(index);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
