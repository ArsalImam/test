package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity;

import butterknife.ButterKnife;

public class PlacesAreaFragment extends Fragment {

    private SelectPlaceActivity mCurrentActivity;

    private UserRepository mRepository;

    public PlacesAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_places_area, container, false);
        ButterKnife.bind(this, view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (SelectPlaceActivity) getActivity();
        addFragment(new ZoneFragment());
    }

    public void addFragment(Fragment fragment) {
        fragment.setRetainInstance(true);
        getChildFragmentManager().beginTransaction()
                .add(R.id.container, fragment, fragment.getClass().getName()).commit();
    }

    public void replaceFragment(Fragment fragment) {
        fragment.setRetainInstance(true);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();
    }
}

