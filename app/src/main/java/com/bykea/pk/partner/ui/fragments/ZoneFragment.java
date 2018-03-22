package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.CitiesData;
import com.bykea.pk.partner.models.data.ZoneData;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.GetZonesResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.CityDataSpinnerAdapter;
import com.bykea.pk.partner.ui.helpers.adapters.ZoneAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ZoneFragment extends Fragment {

    @Bind(R.id.spCities)
    FontTextView spCities;

    @Bind(R.id.rvZones)
    RecyclerView rvZones;


    private ZoneAdapter mZoneAdapter;
    private ArrayList<ZoneData> mZoneList = new ArrayList<>();
    private ArrayList<CitiesData> mCities = new ArrayList<>();
    private CitiesData mSelectedCity;
    private SelectPlaceActivity mCurrentActivity;
    private CityDataSpinnerAdapter dataAdapter1;
    private UserRepository mRepository;

    public ZoneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zone, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (SelectPlaceActivity) getActivity();
        mRepository = new UserRepository();
        if (mZoneList.size() == 0) {
            initZoneAdapter();
            getZones();
        } else {
            initCityTextView();
            initZonesRv();
        }
        Utils.hideSoftKeyboard(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        ivRight0.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.polygon, R.color.blue_dark));
    }

    private void initZonesRv() {
        rvZones.setLayoutManager(new LinearLayoutManager(mCurrentActivity));
        rvZones.setHasFixedSize(true);
        rvZones.setAdapter(mZoneAdapter);
    }

    private void initCityTextView() {
        if (mSelectedCity != null) {
            spCities.setText(mSelectedCity.getName());
        }
        Utils.hideSoftKeyboard(ZoneFragment.this);
    }

    private void initZoneAdapter() {
        mZoneAdapter = new ZoneAdapter(mCurrentActivity, mZoneList, new ZoneAdapter.ItemClickListener() {
            @Override
            public void onClick(ZoneData item) {
                PlacesAreaFragment parentFragment = (PlacesAreaFragment) getParentFragment();
                Fragment fragment = new ZoneAreasFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.Extras.SELECTED_ITEM, item);
                bundle.putParcelable(Constants.Extras.SELECTED_CITY, mSelectedCity);
                bundle.putParcelableArrayList(Constants.Extras.LIST_ITEMS, mZoneList);
                fragment.setArguments(bundle);
                parentFragment.replaceFragment(fragment);
            }
        });

        initZonesRv();
    }

    private void getZones() {
        GetZonesResponse response = (GetZonesResponse) AppPreferences.getObjectFromSharedPref(GetZonesResponse.class);
        if (response != null) {
            mCallBack.onZonesResponse(response);
        } else {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            mRepository.requestZones(mCurrentActivity, mCallBack);
        }
    }


    private UserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onZonesResponse(GetZonesResponse response) {
            if (mCurrentActivity != null && getView() != null) {
                AppPreferences.setObjectToSharedPref(response);
                mSelectedCity = new CitiesData();
                mSelectedCity.setName(response.getCityName());
                initCityTextView();
                mZoneList.clear();
                if (response.getData() != null && response.getData().size() > 0) {
                    mZoneList.addAll(response.getData());
                }
                mZoneAdapter.notifyDataSetChanged();
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.hideSoftKeyboard(ZoneFragment.this);
                        Utils.hideKeyboard(mCurrentActivity);
                    }
                });
                Dialogs.INSTANCE.dismissDialog();
            }
        }

        @Override
        public void onError(int code, String errorMessage) {
            if (mCurrentActivity != null && getView() != null) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }

        /*@Override
        public void onCitiesResponse(GetCitiesResponse response) {
            if (mCurrentActivity != null && getView() != null) {
                setCitiesAdapter();
            }
        }*/
    };

}
