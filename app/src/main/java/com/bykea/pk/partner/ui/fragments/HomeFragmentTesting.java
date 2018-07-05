package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragmentTesting extends Fragment {
    private Unbinder unbinder;
    private HomeActivity mCurrentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_testing, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = ((HomeActivity) getActivity());
        //mCurrentActivity.hideToolbarTitle();
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.setToolbarLogoBismilla();
        mCurrentActivity.setStatusButtonForBismilla("ڈیمانڈ");
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.GONE);
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @OnClick ( { R.id.shahkarBtn, R.id.statsBtn } )
    public void onClick(View view){
        switch (view.getId()){

            case R.id.shahkarBtn:{
                //view.startAnimation(AnimationUtils.loadAnimation(mCurrentActivity, R.anim.fade_in));
                ActivityStackManager.getInstance().startShahkarActivity(mCurrentActivity);
                break;
            }

            case R.id.statsBtn:{
                //view.startAnimation(AnimationUtils.loadAnimation(mCurrentActivity, R.anim.fade_in));
                ActivityStackManager.getInstance().startStatsActivity(mCurrentActivity);
                break;
            }
        }
    }
}
