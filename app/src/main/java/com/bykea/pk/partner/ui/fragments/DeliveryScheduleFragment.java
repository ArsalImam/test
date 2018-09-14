package com.bykea.pk.partner.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

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
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DeliveryScheduleFragment extends Fragment implements DeliveryScheduleAdapter.onClickListener {

    private HomeActivity mCurrentActivity;
    private Unbinder unbinder;

    @BindView(R.id.deliverySchedulerv)
    RecyclerView mRecyclerVeiw;
    private UserRepository mRepository;
    private ArrayList<DeliveryScheduleModel> list;
    private DeliveryScheduleAdapter adapter;

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

        mCurrentActivity.hideStatusCompletely();
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.VISIBLE);

        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.setToolbarTitle(AppPreferences.getPilotData().getCity().getName(), StringUtils.EMPTY);

        mRecyclerVeiw.setHasFixedSize(true);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerVeiw.getContext(),
                DividerItemDecoration.VERTICAL);

        horizontalDecoration.setDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.divider_rv));

        mRecyclerVeiw.addItemDecoration(horizontalDecoration);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mCurrentActivity);
        mRecyclerVeiw.setLayoutManager(mLayoutManager);


        setupRecyclerview();

        getLoadBoardData();


    }

    private void getLoadBoardData() {
        try {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            mRepository.requestLoadBoard(mCurrentActivity, mCallBack, String.valueOf(AppPreferences.getLatitude()),
                    String.valueOf(AppPreferences.getLongitude()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerview() {
        list = new ArrayList<>();

        /*list.add(new DeliveryScheduleModel("21 Street, Block 5", "2 hrs 45 mins", "2.5 km"));
        list.add(new DeliveryScheduleModel("21 Street, Block 5", "2 hrs 45 mins", "2.5 km"));
        list.add(new DeliveryScheduleModel("21 Street, Block 5", "2 hrs 45 mins", "2.5 km"));*/

        adapter = new DeliveryScheduleAdapter(list);
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
    public void directionClick(DeliveryScheduleModel item) {
        Utils.startGoogleDirectionsApp(mCurrentActivity, item.getLatlng().get(0) + "," + item.getLatlng().get(1));
    }

    @Override
    public void callClick(DeliveryScheduleModel item) {
        Utils.callingIntent(mCurrentActivity, item.getCustomer().getMobileNumber());
    }

    @Override
    public void assignClick(DeliveryScheduleModel item) {
        ActivityStackManager.getInstance().startDeliveryScheduleDetailActivity(mCurrentActivity, item);
    }

    private UserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onLoadBoardResponse(LoadBoardResponse response) {
            onApiResponse(response);
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            if (mCurrentActivity != null) {
                Dialogs.INSTANCE.dismissDialog();
            }
        }
    };

    /**
     * This method handles Response of Load Board API and will notify recycler view's adapter for data change
     */
    private void onApiResponse(LoadBoardResponse response) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.TimeFormat.ISO_FORMAT);
            for (DeliveryScheduleModel loadBoardBody : response.getLoadBoardBody()) {
                Date date = (Date) simpleDateFormat.parse(loadBoardBody.getDateTime());


                String timeDuration = DateUtils.getRelativeTimeSpanString(
                        date.getTime(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS).toString();

                double distance = Double.valueOf(loadBoardBody.getDistance()) / 1000; //meter to km

                String address = Utils.getLocationAddress(loadBoardBody.getLatlng().get(0),
                        loadBoardBody.getLatlng().get(1), mCurrentActivity);

                loadBoardBody.setAddress(Utils.formatAddress(address));
                loadBoardBody.setDuration(timeDuration);
                loadBoardBody.setDistance("" + (Math.round(distance * 10.0) / 10.0) + " km");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        list.addAll(response.getLoadBoardBody());
        adapter.notifyDataSetChanged();

        Dialogs.INSTANCE.dismissDialog();

    }


}
