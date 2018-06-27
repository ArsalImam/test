package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.HomeActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragmentTesting extends Fragment {
    private Unbinder unbinder;
    private HomeActivity mCurrentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_testing, container, false);
        unbinder = ButterKnife.bind(this, view);
        mCurrentActivity = ((HomeActivity) getActivity());
        //mCurrentActivity.hideToolbarTitle();
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.setToolbarLogoBismilla();
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.GONE);
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return view;
    }
}
