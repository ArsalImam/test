package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ZoneData;
import com.bykea.pk.partner.models.response.GetZonesResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.Callback;
import com.bykea.pk.partner.ui.helpers.adapters.ZoneAdapter;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Screen for Pickup and Dropoff zone for loadboard jobs
 */
public class LoadboardZoneFragment extends Fragment {

    @BindView(R.id.spCities)
    FontTextView spCities;

    @BindView(R.id.backFromLoadboardZoneIV)
    AppCompatImageView backFromLoadboardZoneIV;

    @BindView(R.id.rvZones)
    RecyclerView rvZones;

    //list adapter
    private ZoneAdapter mZoneAdapter;
    //zone data list
    private ArrayList<ZoneData> mZoneList = new ArrayList<>();
    //previously selected zone data
    private ZoneData selectedZoneData;
    private Callback<ZoneData> callback;

    /**
     * Screen instance that will accept previously selected pickup/dropoff zone and callback to return back selected zone data
     * @param selectedZoneData previously selected zone data
     * @param callback Callback for currently selected zone data
     * @return fragment object
     */
    public static LoadboardZoneFragment newInstance(ZoneData selectedZoneData, Callback<ZoneData> callback) {
        LoadboardZoneFragment fragment = new LoadboardZoneFragment();
        fragment.selectedZoneData = selectedZoneData;
        fragment.callback = callback;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loadboard_zone, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //show previously selected zone name
        if(selectedZoneData != null && selectedZoneData.getEnglishName() != null){
            spCities.setText(selectedZoneData.getEnglishName());
        }
        //initialize zone list adapter
        initZoneAdapter();
        //get zones from API/storage
        getZones();
        backFromLoadboardZoneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity() != null){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    /**
     * Initialize zone list adapter with empty data
     */
    private void initZoneAdapter() {
        mZoneAdapter = new ZoneAdapter(getActivity(), mZoneList, new ZoneAdapter.ItemClickListener() {
            @Override
            public void onClick(ZoneData item) {
                if(callback != null){
                    //return current selected zone data back to the screen from where this fragment is initiated.
                    callback.invoke(item);
                }
            }
        });
        rvZones.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvZones.setHasFixedSize(true);
        rvZones.setAdapter(mZoneAdapter);
    }

    /**
     * Get zones data from storage or API
     */
    private void getZones() {
        GetZonesResponse response = (GetZonesResponse) AppPreferences.getObjectFromSharedPref(GetZonesResponse.class);
        if (response != null && response.getData() != null
                && response.getData().size() > 0 && Utils.isTimeWithInNDay(response.getTimeStamp(), 1)) {
            onApiResponse(response);
        } else {
            Dialogs.INSTANCE.showLoader(getActivity());
            //request zones data
            new UserRepository().requestZones(getActivity(), new UserDataHandler() {

                @Override
                public void onZonesResponse(GetZonesResponse response) {
                    response.setTimeStamp(System.currentTimeMillis());
                    AppPreferences.setObjectToSharedPref(response);
                    onApiResponse(response);
                }

                @Override
                public void onError(int code, String errorMessage) {
                    Dialogs.INSTANCE.dismissDialog();
                    if (getActivity() != null && getView() != null) {
                        Utils.appToast(getActivity(), errorMessage);
                    }
                }
            });
        }
    }

    /**
     * Process zone response data and update list
     * @param response ZoneResponse data
     */
    private void onApiResponse(GetZonesResponse response) {
        Dialogs.INSTANCE.dismissDialog();
        if (getActivity() != null && getView() != null) {
            mZoneList.clear();
            if (response.getData() != null && response.getData().size() > 0) {
                mZoneList.addAll(response.getData());
            }
            mZoneAdapter.notifyDataSetChanged();
        }
    }
}
