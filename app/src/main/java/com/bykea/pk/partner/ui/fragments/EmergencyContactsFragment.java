/*
package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EmergencyContactsFragment extends Fragment {
    @BindView(R.id.hospitalCall)
    ImageView hospitalCall;

    @BindView(R.id.policeStationCall)
    ImageView policeStationCall;

    @BindView(R.id.ambulanceCall)
    ImageView ambulanceCall;

    @BindView(R.id.fireBrigadeCall)
    ImageView fireBrigadeCall;

    private HomeActivity mCurrentActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = ((HomeActivity) getActivity());
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.setToolbarTitle("Emergency Contacts");
    }

    @OnClick({R.id.hospitalCall, R.id.policeStationCall, R.id.fireBrigadeCall, R.id.ambulanceCall})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hospitalCall:
                Utils.callingIntent(mCurrentActivity, AppPreferences.getSettings().getSettings().getHospital());
                break;
            case R.id.policeStationCall:
                Utils.callingIntent(mCurrentActivity, AppPreferences.getSettings().getSettings().getPolice());
                break;
            case R.id.fireBrigadeCall:
                Utils.callingIntent(mCurrentActivity, AppPreferences.getSettings().getSettings().getFire_brigade());
                break;
            case R.id.ambulanceCall:
                Utils.callingIntent(mCurrentActivity, AppPreferences.getSettings().getSettings().getAmbulance());
                break;
        }
    }
}
*/
