package com.bykea.pk.partner.ui.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.Performance;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.adapters.PerformanceGridAdapter;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PerformanceFragment extends Fragment {


    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.data_layout)
    LinearLayout data_layout;
    private Unbinder unbinder;

    private GridLayoutManager layoutManager;

//    @BindView(R.id.tvRideCounts)
//    FontTextView tvRideCounts;
//
//    @BindView(R.id.tvTime)
//    FontTextView tvTime;
//
//    @BindView(R.id.tvAcceptanceRate)
//    FontTextView tvAcceptanceRate;

//    @BindView(R.id.tvRating)
//    FontTextView tvRating;

    @BindView(R.id.tvDate)
    FontTextView tvDate;

    @BindView(R.id.performanceList)
    RecyclerView mRecyclerVeiw;

    private UserRepository repository;
    private HomeActivity mCurrentActivity;
    private final static String REQUIRED_DATE_FORMAT = "dd MMM yyyy";
    private final static String CURRENT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private DriverStatsResponse driverStatsResponse;
    private ArrayList<Performance> mList;
    private PerformanceGridAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_performance, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (HomeActivity) getActivity();
        initViews();
    }

    private void initViews() {
        mCurrentActivity.setToolbarTitle("Performance");
        mCurrentActivity.hideToolbarLogo();
        repository = new UserRepository();
        getDriverStats();
    }

    private void initAdapter() {
        mRecyclerVeiw.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(mCurrentActivity, 2);
        mRecyclerVeiw.setLayoutManager(layoutManager);

        mAdapter = new PerformanceGridAdapter(mCurrentActivity, mList, new PerformanceGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(Performance data) {

            }
        });

        mRecyclerVeiw.addItemDecoration(new DividerItemDecoration(mCurrentActivity, 0));
        mRecyclerVeiw.addItemDecoration(new DividerItemDecoration(mCurrentActivity, 1));
//        setSpanSizeLookup();
        mRecyclerVeiw.setAdapter(mAdapter);
    }

    private void setSpanSizeLookup() {
        final int totalSize = mList.size();
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int span;
                span = totalSize % 2;
                if (totalSize < 2) {
                    return 1;
                } else if (span == 0 || (position <= ((totalSize - 1) - span))) {
                    return 1;
                } else if (span == 1) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
    }


    private void getDriverStats() {
        if (loader != null) {
            loader.setVisibility(View.VISIBLE);
        }
        repository.requestDriverStats(mCurrentActivity, callbackHandler, false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private IUserDataHandler callbackHandler = new UserDataHandler() {

        @Override
        public void onDriverStatsResponse(final DriverStatsResponse response) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                        if (response.isSuccess()) {

                            if (StringUtils.isNotBlank(response.getData().getDate())) {
                                tvDate.setText(response.getData().getDate());
                            }
                            driverStatsResponse = new DriverStatsResponse();
                            mList = new ArrayList<>();
                            driverStatsResponse = response;
                            if (response.getPerformance() != null && response.getPerformance().size() > 0) {
                                mList.addAll(response.getPerformance());
                                initAdapter();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                        if (errorCode == HTTPStatus.UNAUTHORIZED) {
                            Utils.logout(mCurrentActivity);
                        } else {
                            Utils.appToast(mCurrentActivity, errorMessage);
                        }
                    }
                });
            }
        }
    };


}
