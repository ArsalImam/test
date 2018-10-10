package com.bykea.pk.partner.ui.fragments;

import android.graphics.Bitmap;
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
import com.bykea.pk.partner.models.data.MultiDeliveryCallData;
import com.bykea.pk.partner.models.data.MultiDeliveryDropOff;
import com.bykea.pk.partner.models.data.MultiDeliveryPickup;
import com.bykea.pk.partner.ui.helpers.adapters.CallAdapter;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MultiDeliveryCallFragment extends Fragment {

    @BindView(R.id.call_recycler_view)
    RecyclerView mRecyclerView;

    private Unbinder unbinder;
    private LinearLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multi_delivery_call_fragment,
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

        MultiDeliveryPickup pickup = new MultiDeliveryPickup("University Road",
                "Akhtar Feeder",
                "House # 1, Street 35, Block B3  House # 1, Street  35, Block B3");

        List<MultiDeliveryDropOff> list = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            MultiDeliveryDropOff dropOff = new MultiDeliveryDropOff("University Road",
                    "House # 1, Street 35, Block B3  House # 1, Street  35, Block B3",
                    String.valueOf(i));

            list.add(dropOff);
        }

        MultiDeliveryCallData data = new MultiDeliveryCallData(pickup, list);

        CallAdapter adapter = new CallAdapter(data);
        mRecyclerView.setAdapter(adapter);


    }

    public static MultiDeliveryCallFragment newInstance() {
        return new MultiDeliveryCallFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
