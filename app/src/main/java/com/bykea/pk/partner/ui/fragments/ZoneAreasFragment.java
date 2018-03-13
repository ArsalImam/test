package com.bykea.pk.partner.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;


import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.CitiesData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.ZoneAreaData;
import com.bykea.pk.partner.models.data.ZoneData;
import com.bykea.pk.partner.models.response.ZoneAreaResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.SelectPlaceActivity;
import com.bykea.pk.partner.ui.helpers.adapters.ZoneAreaAdapter;
import com.bykea.pk.partner.ui.helpers.adapters.ZoneDataSpinnerAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ZoneAreasFragment extends Fragment {

    @Bind(R.id.spCities)
    Spinner spCities;

    @Bind(R.id.rvZones)
    RecyclerView rvZoneAreas;

    @Bind(R.id.ivRight0)
    ImageView ivRight0;

    private ZoneAreaAdapter mZoneAreaAdapter;
    private ArrayList<ZoneAreaData> mZoneAreaList = new ArrayList<>();
    private ArrayList<ZoneData> mZones = new ArrayList<>();

    private ZoneData mSelectedZone;
    private SelectPlaceActivity mCurrentActivity;
    private ZoneDataSpinnerAdapter dataAdapter1;
    private ArrayList<ZoneData> zones = new ArrayList<>();

    private CitiesData mSelectedCity;
    private UserRepository mRepository;

    public ZoneAreasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zone_areas, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (SelectPlaceActivity) getActivity();
        mRepository = new UserRepository();
        if (zones.size() == 0) {
            setZonesAdapter();
            initZoneAreasAdapter();
        } else {
            initZonesAdapter();
            initZoneAreasRv();
        }
        Utils.hideSoftKeyboard(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ivRight0.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, R.drawable.polygon, R.color.blue_dark));
    }


    private void setZonesAdapter() {
        mSelectedCity = getArguments().getParcelable(Constants.Extras.SELECTED_CITY);
        zones = getArguments().getParcelableArrayList(Constants.Extras.LIST_ITEMS);
        if (zones != null && zones.size() > 0) {
            mZones.addAll(zones);
            dataAdapter1 = new ZoneDataSpinnerAdapter(mCurrentActivity, mZones);
            initZonesAdapter();
//            mSelectedZone = mZones.get(Utils.getCurrentCityIndex());
        }
    }

    private void initZonesAdapter() {
        spCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, final int position, long id) {
                if (view != null) {
                    view.findViewById(R.id.singleViewLine).setVisibility(View.INVISIBLE);
                    ((AutoFitFontTextView) view.findViewById(R.id.tv_item)).setText(mSelectedCity.getName() + " > " + mZones.get(position).getEnglishName());

                } else {
                    final ViewTreeObserver layoutObserver = spCities.getViewTreeObserver();
                    layoutObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (spCities != null) {
                                View selectedView = spCities.getSelectedView();
                                if (selectedView != null) {
                                    selectedView.findViewById(R.id.singleViewLine).setVisibility(View.INVISIBLE);
                                    ((AutoFitFontTextView) selectedView.findViewById(R.id.tv_item)).setText(mSelectedCity.getName() + " > " + mZones.get(position).getEnglishName());

                                }
                                spCities.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        }
                    });
                }
                if (mSelectedZone == null || !mSelectedZone.get_id().equalsIgnoreCase(mZones.get(position).get_id())) {
                    mSelectedZone = mZones.get(position);
                    getZoneAreas(mSelectedZone);
                }
                Utils.hideSoftKeyboard(ZoneAreasFragment.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCities.setAdapter(dataAdapter1);
        spCities.setSelection(getSelectedZoneIndex());
    }

    private int getSelectedZoneIndex() {
        int index = 0;
        ZoneData selectedZone = getArguments().getParcelable(Constants.Extras.SELECTED_ITEM);
        if (selectedZone != null) {
            for (int i = 0; i < zones.size(); i++) {
                if (zones.get(i).get_id().equalsIgnoreCase(selectedZone.get_id())) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private void initZoneAreasAdapter() {
        mZoneAreaAdapter = new ZoneAreaAdapter(mCurrentActivity, mZoneAreaList, new ZoneAreaAdapter.ItemClickListener() {
            @Override
            public void onClick(ZoneAreaData item) {
                PlacesAreaFragment parentFragment = (PlacesAreaFragment) getParentFragment();
                Fragment fragment = new PlacesSearchFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.Extras.HIDE_SEARCH, true);
                PlacesResult placesResult = new PlacesResult(item.getName() + ", " + mSelectedZone.getEnglishName() + ", " + mSelectedCity.getName(), item.getName() + ", " + mSelectedZone.getEnglishName() + ", " + mSelectedCity.getName(),
                        Double.parseDouble(item.getLoc().get(0)), Double.parseDouble(item.getLoc().get(1)));
                bundle.putParcelable(Constants.Extras.SELECTED_ITEM, placesResult);
                fragment.setArguments(bundle);
                parentFragment.replaceFragment(fragment);
            }
        });

        initZoneAreasRv();
    }

    private void initZoneAreasRv() {
        rvZoneAreas.setLayoutManager(new LinearLayoutManager(mCurrentActivity));
        rvZoneAreas.setHasFixedSize(true);
        rvZoneAreas.setAdapter(mZoneAreaAdapter);
    }

    private void getZoneAreas(ZoneData zone) {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        mRepository.requestZoneAreas(mCurrentActivity, zone, mCallBack);
    }

    private UserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onZoneAreasResponse(ZoneAreaResponse response) {
            if (mCurrentActivity != null && getView() != null) {
                Dialogs.INSTANCE.dismissDialog();
                mZoneAreaList.clear();
                if (response.getData() != null && response.getData().size() > 0
                        && response.getData().get(0).getAreas() != null
                        && response.getData().get(0).getAreas().size() > 0) {
                    mZoneAreaList.addAll(response.getData().get(0).getAreas());
                }
                mZoneAreaAdapter.notifyDataSetChanged();
                Utils.hideSoftKeyboard(ZoneAreasFragment.this);
            }
        }

        @Override
        public void onError(int code, String errorMessage) {
            if (mCurrentActivity != null && getView() != null) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }

    };

}
