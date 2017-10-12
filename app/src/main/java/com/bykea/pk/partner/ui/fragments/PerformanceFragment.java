package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DriverStatsData;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PerformanceFragment extends Fragment {


    @Bind(R.id.loader)
    ProgressBar loader;
    @Bind(R.id.data_layout)
    LinearLayout data_layout;

    @Bind(R.id.tvRideCounts)
    FontTextView tvRideCounts;

    @Bind(R.id.tvTime)
    FontTextView tvTime;

    @Bind(R.id.tvAcceptanceRate)
    FontTextView tvAcceptanceRate;

    @Bind(R.id.tvRating)
    FontTextView tvRating;

    @Bind(R.id.tvDate)
    FontTextView tvDate;


    private UserRepository repository;
    private HomeActivity mCurrentActivity;
    private final static String REQUIRED_DATE_FORMAT = "dd MMM yyyy";
    private final static String CURRENT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_performance, container, false);
        ButterKnife.bind(this, view);
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


    private void getDriverStats() {
        if (loader != null) {
            loader.setVisibility(View.VISIBLE);
        }
        repository.requestDriverStats(mCurrentActivity, callbackHandler);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
                            data_layout.setVisibility(View.VISIBLE);
                            PilotData userData = AppPreferences.getPilotData();
                            DriverStatsData responseData = response.getData();
                            if (StringUtils.isNotBlank(responseData.getDate())) {
                                tvDate.setText(Utils.getFormattedDate(responseData.getDate(),
                                        CURRENT_DATE_FORMAT, REQUIRED_DATE_FORMAT));
                            }
                            if (StringUtils.isNotBlank(responseData.getAcceptanceRate())) {
                                userData.setAcceptance_rate(Math.round(Double.parseDouble(responseData.getAcceptanceRate())) + "");
                                tvAcceptanceRate.setText(Math.round(Double.parseDouble(responseData.getAcceptanceRate())) + "%");
                            }
                            if (StringUtils.isNotBlank(responseData.getTrips())) {
                                userData.setTripCount(responseData.getTrips());
                                tvRating.setText(responseData.getTrips());
                            }
                            if (StringUtils.isNotBlank(responseData.getTime())) {
                                userData.setTimeCount(responseData.getTime());
                                tvTime.setText(responseData.getTime());
                            }
                            if (StringUtils.isNotBlank(responseData.getDailyRating())) {
                                String rating = Utils.formatDecimalPlaces(responseData.getDailyRating());
                                if (rating.equalsIgnoreCase("0")) {
                                    tvRating.setText("N/A");
                                } else {
                                    tvRating.setText(rating);
                                }
                            }
                            if (StringUtils.isNotBlank(responseData.getRating())) {
                                userData.setRating(responseData.getRating());
                            }

                            AppPreferences.setPilotData(userData);
                            AppPreferences.setStatsApiCallRequired(false);
                            AppPreferences.setLastStatsApiCallTime(System.currentTimeMillis());
                        } else {
                            Utils.appToast(mCurrentActivity, response.getMessage());
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
