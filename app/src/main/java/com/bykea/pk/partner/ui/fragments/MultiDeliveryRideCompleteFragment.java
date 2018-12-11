package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DirectionDropOffData;
import com.bykea.pk.partner.models.data.MultiDeliveryDirectionDetails;
import com.bykea.pk.partner.models.data.MultiDeliveryPickup;
import com.bykea.pk.partner.ui.helpers.adapters.DirectionAdapter;
import com.bykea.pk.partner.ui.helpers.adapters.MultiDeliveryCompleteRideAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MultiDeliveryRideCompleteFragment extends Fragment {
    @BindView(R.id.ride_recycler_view)
    RecyclerView mRecyclerView;

    private Unbinder unbinder;
    private LinearLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multidelivery_mukamal_fragment,
                container,
                false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Todo 1: Mock Data for testing need to be change when backend team provide an API

        List<DirectionDropOffData> list = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            DirectionDropOffData dropOff = new DirectionDropOffData.Builder()
                    .setmArea("University Road")
                    .setDriverName("Shafiq Khalid")
                    .setDropOffNumberText(String.valueOf(i))
                    .build();
            list.add(dropOff);
        }
        MultiDeliveryCompleteRideAdapter adapter = new MultiDeliveryCompleteRideAdapter(list);
        mRecyclerView.setAdapter(adapter);
    }

    public static MultiDeliveryRideCompleteFragment newInstance() {
        return new MultiDeliveryRideCompleteFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
