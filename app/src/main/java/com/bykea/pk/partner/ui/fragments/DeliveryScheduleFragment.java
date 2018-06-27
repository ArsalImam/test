package com.bykea.pk.partner.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DileveryScheduleModel;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.adapters.DeliveryScheduleAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DeliveryScheduleFragment extends Fragment implements DeliveryScheduleAdapter.onClickListener {

    private HomeActivity mCurrentActivity;
    private Unbinder unbinder;

    @BindView(R.id.deliverySchedulerv)
    RecyclerView mRecyclerVeiw;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dilevery_schedule, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentActivity = ((HomeActivity) getActivity());
        //mCurrentActivity.hideToolbarTitle();
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.setToolbarTitle("Karachi", "");
        mCurrentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRecyclerVeiw.setHasFixedSize(true);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerVeiw.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.divider_rv);

        horizontalDecoration.setDrawable(horizontalDivider);
        mRecyclerVeiw.addItemDecoration(horizontalDecoration);

        mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        mRecyclerVeiw.setLayoutManager(mLayoutManager);

        populateList();


    }

    private void populateList() {
        List<DileveryScheduleModel> list = new ArrayList<>();

        list.add(new DileveryScheduleModel("21 Street, Block 5", "2 hrs 45 mins", "2.5 km"));
        list.add(new DileveryScheduleModel("21 Street, Block 5", "2 hrs 45 mins", "2.5 km"));
        list.add(new DileveryScheduleModel("21 Street, Block 5", "2 hrs 45 mins", "2.5 km"));

        DeliveryScheduleAdapter adapter = new DeliveryScheduleAdapter(list);
        adapter.setOnClickListener(this);
        mRecyclerVeiw.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        mCurrentActivity.hideUrduTitle();
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void directionClick(int pos) {
        ActivityStackManager.getInstance().startDeliveryScheduleDetailActivity(mCurrentActivity);
    }

    @Override
    public void callClick(int pos) {

    }

    @Override
    public void confirmClick(int pos) {

    }
}
