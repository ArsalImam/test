package com.bykea.pk.partner.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.FreeDriverResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.DonutProgress;
import com.bykea.pk.partner.widgets.FontTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CallingActivity extends BaseActivity {
    @Bind(R.id.counterTv)
    FontTextView counterTv;
    //    @Bind(R.id.callerNameTv)
//    FontTextView callerNameTv;
//    @Bind(R.id.startAddressTv)
//    FontTextView startAddressTv;
//    @Bind(R.id.rejectCallBtn)
//    FontTextView rejectCallBtn;
    @Bind(R.id.acceptCallBtn)
    FontTextView acceptCallBtn;
    @Bind(R.id.donut_progress)
    DonutProgress donutProgress;
    //    @Bind(R.id.distanceTv)
//    FontTextView distanceTv;
//    @Bind(R.id.timeTv)
//    FontTextView timeTv;
    @Bind(R.id.ivCallType)
    ImageView ivCallType;
    @Bind(R.id.activity_calling)
    RelativeLayout activity_calling;

    private UserRepository repository;
    private MediaPlayer _mpSound;
    private CallingActivity mCurrentActivity;
    private float progress = 0;

    private int counter = 0;
    private int total = 1;

    private boolean isFreeDriverApiCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        repository = new UserRepository();
        Utils.unlockScreen(mCurrentActivity);
        AppPreferences.setStatsApiCallRequired(true);
        //To inactive driver during passenger calling state
        AppPreferences.setTripStatus(TripStatus.ON_IN_PROGRESS);
        repository.requestLocationUpdate(mCurrentActivity, handler);

        donutProgress.setProgress(20);
        startAnimation();

        if (null != getIntent() && getIntent().getBooleanExtra("isGcm", false)) {
            Utils.redLog("FCM", "Calling Activity");
            DriverApp.getApplication().connect();
//            WebIORequestHandler.getInstance().setContext(mCurrentActivity);
            DriverApp.startLocationService(mCurrentActivity);
        }
        ackCall();
        setInitialData();


    }

    @Override
    protected void onResume() {
        super.onResume();
//        WebIORequestHandler.getInstance().setContext(mCurrentActivity);
         /*SETTING SERVICE CONTEXT WITH ACTIVITY TO SEND BROADCASTS*/
//        LocationService.setContext(CallingActivity.this);
        AppPreferences.setCallingActivityOnForeground(true);
    }

    @Override
    protected void onDestroy() {
//        Utils.flushMixPanelEvent(mCurrentActivity);
        stopSound();
//        unregisterReceiver(cancelRideReceiver);
        if (AppPreferences.isOnTrip()) {
            AppPreferences.setIncomingCall(false);
        } else {
            AppPreferences.setTripStatus(TripStatus.ON_FREE);
            AppPreferences.setIncomingCall(true);
        }
        AppPreferences.setCallingActivityOnForeground(false);
        Utils.unbindDrawables(activity_calling);
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        stopSound();


    }

    @Override
    public void onBackPressed() {

    }

    private long mLastClickTime;
    private String acceptSeconds = "0";

    @OnClick({R.id.acceptCallBtn})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.rejectCallBtn:
////                Dialogs.INSTANCE.showToast(mCurrentActivity, "This feature will be in phase 2");
//                stopSound();
//                repository.requestRejectCall(mCurrentActivity, handler);
//                if (timer != null) {
//                    timer.cancel();
//                }
//                break;
            case R.id.acceptCallBtn:
                if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (!isFreeDriverApiCalled) {
                    stopSound();
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    acceptSeconds = counterTv.getText().toString();
                    repository.requestAcceptCall(mCurrentActivity, acceptSeconds, handler);
                    timer.cancel();
                }
                break;
        }
    }

    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onAck(final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.appToastDebug(mCurrentActivity, msg);
                }
            });
        }

        @Override
        public void onFreeDriver(FreeDriverResponse freeDriverResponse) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                        stopSound();
                        mCurrentActivity.finish();
                    }
                });
            }
        }

        @Override
        public void onAcceptCall(final AcceptCallResponse acceptCallResponse) {
            if (mCurrentActivity != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        Dialogs.INSTANCE.showTempToast(mCurrentActivity,
                                acceptCallResponse.getMessage());
                        if (acceptCallResponse.isSuccess()) {
                            AppPreferences.clearTripDistanceData();
                            AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL);

                            NormalCallData callData = AppPreferences.getCallData();
                            callData.setStatus(TripStatus.ON_ACCEPT_CALL);
                            AppPreferences.setCallData(callData);
                            logMixpanelEvent(callData, true);

                            AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude());

                            AppPreferences.setIsOnTrip(true);
                            ActivityStackManager.getInstance().startJobActivity(mCurrentActivity);
                            stopSound();
                            mCurrentActivity.finish();
                        } else {
                            AppPreferences.setTripStatus(TripStatus.ON_FREE);
                            AppPreferences.setIsOnTrip(false);
                            Dialogs.INSTANCE.showToast(mCurrentActivity
                                    , acceptCallResponse.getMessage());

                        }
                    }
                });
            }
        }

        @Override
        public void onRejectCall(final RejectCallResponse rejectCallResponse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.showToast(mCurrentActivity,
                            rejectCallResponse.getMessage());
                    Dialogs.INSTANCE.dismissDialog();
                    if (AppPreferences.isOnTrip()) {
                        AppPreferences.setIncomingCall(false);
                        AppPreferences.setTripStatus(TripStatus.ON_FREE);
                    } else {
                        AppPreferences.setIncomingCall(true);
                    }
                    ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                    stopSound();
                    mCurrentActivity.finish();
                }
            });
        }

        @Override
        public void onError(int errorCode, final String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage);
                    ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                    stopSound();
                    mCurrentActivity.finish();
                }
            });
        }
    };

    private void logMixpanelEvent(NormalCallData callData, boolean isOnAccept) {
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
            data.put("City", AppPreferences.getPilotData().getCity().getName());

            if (isOnAccept) {
                data.put("AcceptSeconds", acceptSeconds);
                Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.ON_ACCEPT.replace(
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data);
            } else {
                Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.ON_RECEIVE_NEW_JOB.replace(
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private CountDownTimer timer = new CountDownTimer(20800, 100) {

        @Override
        public void onTick(long millisUntilFinished) {
            if (progress >= 20) {
                timer.onFinish();
            } else {
                if (!_mpSound.isPlaying()) _mpSound.start();
                progress = progress + 0.1f;
                donutProgress.setProgress(progress);
                counter += 1;
                if (counter == 10) {
                    counter = 0;
                    counterTv.setText((20 - total++) + "");
                }
            }
        }

        @Override
        public void onFinish() {
            donutProgress.setProgress(20);
            counterTv.setText("0");
//            rejectCallBtn.setEnabled(false);
            acceptCallBtn.setEnabled(false);
            stopSound();
            if (!isFreeDriverApiCalled) {
                repository.freeDriverStatus(mCurrentActivity, handler);
                isFreeDriverApiCalled = true;
                ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                mCurrentActivity.finish();
            }
        }
    };

    private void startAnimation() {
        _mpSound = MediaPlayer.create(mCurrentActivity, R.raw.ringtone);
        _mpSound.start();
        timer.start();

    }

    private void stopSound() {
        if (null != _mpSound && _mpSound.isPlaying()) {
            _mpSound.stop();
        }
        if (null != timer) timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppPreferences.setCallingActivityOnForeground(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Keys.BROADCAST_CANCEL_RIDE);
//        registerReceiver(cancelRideReceiver, intentFilter);
    }

    private void ackCall() {
        repository.ackCall(mCurrentActivity, handler);
    }

    private void setInitialData() {
        NormalCallData callData = AppPreferences.getCallData();
        logMixpanelEvent(callData, false);
//        callerNameTv.setText(callData.getPassName());
//        startAddressTv.setText(callData.getStartAddress());
//        timeTv.setText(callData.getArivalTime() + " min");
//        distanceTv.setText(callData.getDistance() + " km");
        counterTv.setText("20");
        if (StringUtils.isNotBlank(callData.getIcon())) {
            Utils.redLog(mCurrentActivity.getClass().getSimpleName(), Utils.getCloudinaryLink(callData.getIcon(), mCurrentActivity));
            Picasso.with(mCurrentActivity).load(Utils.getCloudinaryLink(callData.getIcon(), mCurrentActivity))
                    .placeholder(Utils.getServiceIcon(callData))
                    .into(ivCallType, new Callback() {
                        @Override
                        public void onSuccess() {
                            Utils.redLog(mCurrentActivity.getClass().getSimpleName(), "Icon OnSuccess");
                        }

                        @Override
                        public void onError() {
                            Utils.redLog(mCurrentActivity.getClass().getSimpleName(), "Icon OnError");
                        }
                    });
        } else if (StringUtils.isNotBlank(callData.getCallType())) {
            ivCallType.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, Utils.getServiceIcon(callData)));
        } else {
            ivCallType.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.ride));
        }
    }


    @Subscribe
    public void onEvent(final Intent intent) {
        if (mCurrentActivity != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != intent && null != intent.getExtras()) {
                        if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_RIDE)
                                || intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_BY_ADMIN)) {
                            Utils.setCallIncomingState();
                            AppPreferences.setTripStatus(TripStatus.ON_FREE);
                            stopSound();
                            ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(false, "", mCurrentActivity);
                            mCurrentActivity.finish();
                        }
                    }
                }
            });
        }
    }

/*
    private BroadcastReceiver cancelRideReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != intent && null != intent.getExtras()) {
                            if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_RIDE)) {
                                Utils.setCallIncomingState();
                                AppPreferences.setTripStatus(TripStatus.ON_FREE);
                                stopSound();
                                ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(false, "", mCurrentActivity);
                                mCurrentActivity.finish();
                            }
                        }
                    }
                });
            }

        }
    };*/
}
