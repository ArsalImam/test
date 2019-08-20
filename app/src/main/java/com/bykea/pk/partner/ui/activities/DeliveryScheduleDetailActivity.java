package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DeliveryScheduleModel;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveryScheduleDetailActivity extends BaseActivity {

    @BindView(R.id.nameTv)
    FontTextView nameTv;

    @BindView(R.id.distanceTv)
    FontTextView distanceTv;

    @BindView(R.id.addressTv)
    FontTextView addressTv;

    @BindView(R.id.durationTv)
    FontTextView durationTv;

    @BindView(R.id.closeBtn)
    ImageView closeBtn;

    private DeliveryScheduleDetailActivity mCurrentActivity;

    private DeliveryScheduleModel data;
    private UserRepository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_schedule_detail);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(mCurrentActivity);
        mRepository = new UserRepository();
        initViews();
    }

    /**
     * This method will initialize views
     */
    private void initViews() {
        try {
            data = getIntent().getParcelableExtra(Constants.Extras.SELECTED_ITEM);
            if (data != null) {
                setTitleCustomToolbarWithUrdu(data.getCustomer().getFullName(), "");
                nameTv.setText(data.getCustomer().getFullName());

                distanceTv.setText(data.getDistance());
                durationTv.setText(data.getDuration());
                addressTv.setText(data.getAddress());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.callbtn, R.id.closeBtn, R.id.directionBtn, R.id.assignBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.callbtn: {
                Utils.callingIntent(mCurrentActivity, data.getCustomer().getMobileNumber());
                break;
            }

            case R.id.directionBtn: {
                Utils.startGoogleDirectionsApp(mCurrentActivity, data.getLatlng().get(0) + "," + data.getLatlng().get(1));
                break;
            }

            case R.id.closeBtn: {
                onBackPressed();
                break;
            }

            case R.id.assignBtn: {
                callLoadBoardAssignApi();
                break;
            }
        }
    }

    /**
     * This method will call API to assign Schedule Job to driver
     */
    private void callLoadBoardAssignApi() {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        mRepository.requestAcceptScheduledCall(mCurrentActivity, data.getId(), mCallBack);
    }

    /**
     * To handle API call backs
     */
    private IUserDataHandler mCallBack = new UserDataHandler() {
        @Override
        public void onAcceptCall(final AcceptCallResponse acceptCallResponse) {
            if (mCurrentActivity != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        Dialogs.INSTANCE.showTempToast(acceptCallResponse.getMessage());
                        if (acceptCallResponse.isSuccess()) {
                            AppPreferences.clearTripDistanceData();
                            AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL);

                            NormalCallData callData = acceptCallResponse.getData();
                            callData.setStatus(TripStatus.ON_ACCEPT_CALL);
                            AppPreferences.setCallData(callData);
                            logMixPanelEvent(callData);

                            AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude());

                            AppPreferences.setIsOnTrip(true);
                            ActivityStackManager.getInstance().startJobActivity(mCurrentActivity);
                            finishActivity();
                        } else {
                            Utils.setCallIncomingState();
                            Dialogs.INSTANCE.showToast(acceptCallResponse.getMessage());

                        }
                    }
                });
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            Dialogs.INSTANCE.dismissDialog();
            Utils.appToast(mCurrentActivity, errorMessage);
        }
    };


    /**
     * This method updates driver location with updated trip status and finishes the activity
     */
    private void finishActivity() {
        if (Utils.isConnected(DeliveryScheduleDetailActivity.this, false))
            mRepository.requestLocationUpdate(mCurrentActivity, mCallBack, AppPreferences.getLatitude(), AppPreferences.getLongitude());
        mCurrentActivity.finish();
    }

    /***
     * Send logs to Mix Panel Events for Accepted Call.
     *
     * @param callData Call response data which was received from API server.
     */
    private void logMixPanelEvent(NormalCallData callData) {
        try {

            JSONObject data = new JSONObject();
            data.put("PassengerID", callData.getPassId());
            data.put("DriverID", AppPreferences.getPilotData().getId());
            data.put("TripID", callData.getTripId());
            data.put("TripNo", callData.getTripNo());
            data.put("PickUpLocation", callData.getStartLat() + "," + callData.getStartLng());
            data.put("timestamp", Utils.getIsoDate());
            if (StringUtils.isNotBlank(callData.getEndLat()) && StringUtils.isNotBlank(callData.getEndLng())) {
                data.put("DropOffLocation", callData.getEndLat() + "," + callData.getEndLng());
            }
            data.put("ETA", Utils.formatETA(callData.getArivalTime()));
            data.put("EstimatedDistance", AppPreferences.getEstimatedDistance());
            data.put("CurrentLocation", Utils.getCurrentLocation());
            data.put("PassengerName", callData.getPassName());
            data.put("DriverName", AppPreferences.getPilotData().getFullName());
            data.put("type", callData.getCallType());
            data.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());

            Utils.logEvent(mCurrentActivity, callData.getPassId(),
                    Constants.AnalyticsEvents.ON_ACCEPT.replace(
                            Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}