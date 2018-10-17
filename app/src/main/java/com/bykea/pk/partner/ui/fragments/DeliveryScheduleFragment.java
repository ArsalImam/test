package com.bykea.pk.partner.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DeliveryScheduleModel;
import com.bykea.pk.partner.models.response.LoadBoardResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.DeliveryScheduleAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DeliveryScheduleFragment extends Fragment implements DeliveryScheduleAdapter.onClickListener {

    private HomeActivity mCurrentActivity;
    private Unbinder unbinder;
    private UserRepository mRepository;
    private ArrayList<DeliveryScheduleModel> list;
    private DeliveryScheduleAdapter adapter;

    @BindView(R.id.deliverySchedulerv)
    RecyclerView mRecyclerView;

    @BindView((R.id.ivNoBookingAvailable))
    AppCompatImageView ivNoBookingAvailable;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dilevery_schedule, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentActivity = ((HomeActivity) getActivity());
        mRepository = new UserRepository();

        setupToolbarHeader();
        setupRecyclerView();
        setupAdapter();
        requestLoadBoardData();


    }

    //region General Helper methods

    /***
     * Setup toolbar header
     */
    private void setupToolbarHeader() {
        mCurrentActivity.hideStatusCompletely();
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.VISIBLE);
        //==mCurrentActivity.hideStatusLayout();
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.makeDemandSpaceAvailableOnUI();
        mCurrentActivity.setToolbarTitle(AppPreferences.getPilotData().getCity().getName(),
                StringUtils.EMPTY);
    }


    /***
     * Display No booking screen if we receive no data from server.
     * If we get zero records we are to hide recycle view and show booking image.
     *
     * @param shouldShow should display no booking.
     */
    private void displayNoBooking(boolean shouldShow) {
        if (shouldShow) {
            mRecyclerView.setVisibility(View.GONE);
            ivNoBookingAvailable.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            ivNoBookingAvailable.setVisibility(View.GONE);
        }

    }
    //endregion

    //region Helper method for Delivery Schedule Adapter

    /**
     * Setup configuration for schedule adapter for recycle view
     */
    private void setupAdapter() {
        list = new ArrayList<>();
        adapter = new DeliveryScheduleAdapter(list);
        adapter.setOnClickListener(this);
        mRecyclerView.setAdapter(adapter);
    }


    /***
     * Setup configuration for Recycle view
     */
    private void setupRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        DividerItemDecoration horizontalDecoration = new
                DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        horizontalDecoration.setDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.divider_rv));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(horizontalDecoration);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /***
     * This method handles Response of Load Board API and will notify recycler view's
     * adapter for data change
     *
     * @param response response which is return from API
     *
     * @see LoadBoardResponse
     */
    private void setupLoadBoardDataModel(LoadBoardResponse response) {
        if (response != null
                && response.getLoadBoardBody() != null
                && response.getLoadBoardBody().size() > 0) {
            displayNoBooking(false);
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat(Constants.TimeFormat.ISO_FORMAT, Locale.getDefault());
            String timeDuration;
            double distance;
            try {
                for (DeliveryScheduleModel loadBoardBody : response.getLoadBoardBody()) {

                    Date date = simpleDateFormat.parse(loadBoardBody.getDateTime());

                    timeDuration = DateUtils.getRelativeTimeSpanString(
                            date.getTime(), System.currentTimeMillis(),
                            DateUtils.DAY_IN_MILLIS).toString();

                    //Converts meter to KM
                    distance = Double.valueOf(loadBoardBody.getDistance()) / 1000;

                    loadBoardBody.setDuration(timeDuration);
                    loadBoardBody.setDistance(String.format(getResources()
                                    .getString(R.string.lord_board_order_distance),
                            Math.round(distance * 10.0) / 10.0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.addAll(response.getLoadBoardBody());
            adapter.notifyDataSetChanged();

        } else {
            displayNoBooking(true);
        }
        Dialogs.INSTANCE.dismissDialog();

    }

    //endregion

    //region Life Cycle methods
    @Override
    public void onDestroyView() {
        mCurrentActivity.hideUrduTitle();
        super.onDestroyView();
        unbinder.unbind();
    }

    //endregion

    //region Adapter Click listeners

    @Override
    public void directionClick(DeliveryScheduleModel item) {
        if (AppPreferences.getAvailableStatus()) {
            Utils.startGoogleDirectionsApp(mCurrentActivity, item.getLatlng().get(0) + "," + item.getLatlng().get(1));
        }else{
            Dialogs.INSTANCE.showNegativeAlertDialogForDemand(mCurrentActivity,
                    getString(R.string.driver_demand_offline_error_ur),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialogs.INSTANCE.dismissDialog();
                        }
                    });
        }
    }

    @Override
    public void callClick(DeliveryScheduleModel item) {
        if (AppPreferences.getAvailableStatus()) {
            Utils.callingIntent(mCurrentActivity, item.getCustomer().getMobileNumber());
        }else{
            Dialogs.INSTANCE.showNegativeAlertDialogForDemand(mCurrentActivity,
                    getString(R.string.driver_demand_offline_error_ur),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialogs.INSTANCE.dismissDialog();
                        }
                    });
        }
    }

    @Override
    public void assignClick(DeliveryScheduleModel item) {
        if (AppPreferences.getAvailableStatus()) {
            ActivityStackManager.getInstance().startDeliveryScheduleDetailActivity(mCurrentActivity, item);
        } else {
            Dialogs.INSTANCE.showNegativeAlertDialogForDemand(mCurrentActivity,
                    getString(R.string.driver_demand_offline_error_ur),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialogs.INSTANCE.dismissDialog();
                        }
                    });
        }
    }

    //endregion

    //region API helper methods

    /***
     * Request latest Load board data from API Server.
     *
     */
    private void requestLoadBoardData() {
        try {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            mRepository.requestLoadBoard(mCurrentActivity, mCallBack,
                    String.valueOf(AppPreferences.getLatitude()),
                    String.valueOf(AppPreferences.getLongitude()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onLoadBoardResponse(LoadBoardResponse response) {
            setupLoadBoardDataModel(response);
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            if (mCurrentActivity != null) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }
    };

    //endregion


}
