package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
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
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CallingActivity extends BaseActivity {
    @BindView(R.id.counterTv)
    FontTextView counterTv;
    //    @BindView(R.id.callerNameTv)
//    FontTextView callerNameTv;
//    @BindView(R.id.startAddressTv)
//    FontTextView startAddressTv;
//    @BindView(R.id.rejectCallBtn)
//    FontTextView rejectCallBtn;
    @BindView(R.id.acceptCallBtn)
    ImageView acceptCallBtn;
    @BindView(R.id.donut_progress)
    DonutProgress donutProgress;
    //    @BindView(R.id.distanceTv)
//    FontTextView distanceTv;
//    @BindView(R.id.timeTv)
//    FontTextView timeTv;
    @BindView(R.id.ivCallType)
    ImageView ivCallType;
    @BindView(R.id.activity_calling)
    RelativeLayout activity_calling;

    @BindView(R.id.kharidariPriceLayout)
    RelativeLayout kharidariPriceLayout;

    @BindView(R.id.cashKiWasooliLayout)
    RelativeLayout cashKiWasooliLayout;

    @BindView(R.id.kraiKiKamaiLayout)
    RelativeLayout kraiKiKamaiLayout;

    @BindView(R.id.cashKiWasooliTv)
    FontTextView cashKiWasooliTv;

    @BindView(R.id.kraiKiKamaiTv)
    FontTextView kraiKiKamaiTv;

    @BindView(R.id.destinationTv)
    FontTextView destinationTv;

    @BindView(R.id.estimatedDistanceTv)
    FontTextView estimatedDistanceTv;

    @BindView(R.id.kharidariKiRaqamTv)
    FontTextView kharidariKiRaqamTv;

    @BindView(R.id.distanceAwayTv)
    FontTextView distanceAwayTv;

    @BindView(R.id.customerRatingTv)
    FontTextView customerRatingTv;

    @BindView(R.id.estimatedDistanceUnitTv)
    FontTextView estimatedDistaneUnitTv;

    @BindView(R.id.circle_distance_layout)
    LinearLayout circle_distance_layout;


    private UserRepository repository;
    private MediaPlayer _mpSound;
    private CallingActivity mCurrentActivity;
    private float progress = 0;

    private int counter = 0;
    private int total = 1;

    private boolean isFreeDriverApiCalled = false;

    public String TAG = CallingActivity.class.getSimpleName();

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
        repository.requestLocationUpdate(mCurrentActivity, handler,
                AppPreferences.getLatitude(),
                AppPreferences.getLongitude());

        donutProgress.setProgress(20);
        startAnimation();

        if (null != getIntent() && getIntent().getBooleanExtra(Constants.IS_FROM_GCM,
                false)) {
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
            //AppPreferences.setTripStatus(TripStatus.ON_FREE);
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
                        finishActivity();
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
                            AppPreferences.setDeliveryType(Constants.CallType.SINGLE);
                            ActivityStackManager.getInstance().startJobActivity(mCurrentActivity);
                            stopSound();
                            finishActivity();
                        } else {
                            Utils.setCallIncomingState();
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
                    finishActivity();
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
                    finishActivity();
                }
            });
        }
    };

    private void finishActivity() {
        repository.requestLocationUpdate(mCurrentActivity, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude());
        mCurrentActivity.finish();
    }

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
            data.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());

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

    private CountDownTimer timer = new CountDownTimer(Constants.RIDE_ACCEPTANCE_TIMEOUT, 100) {

        @Override
        public void onTick(long millisUntilFinished) {
            progress = (Constants.RIDE_ACCEPTANCE_TIMEOUT - millisUntilFinished) / 1000;
            Log.d("RIDE ACCEPT PROGRESS", millisUntilFinished + ":" + progress + ":" + counterTv.getText().toString());
            if (progress >= 20) {
                timer.onFinish();
            } else {
                if (!_mpSound.isPlaying()) _mpSound.start();
                //progress = progress + 0.1f;
                donutProgress.setProgress(progress);
                try {
                    counterTv.setText(String.valueOf((int) (millisUntilFinished / 1000)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
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
                Utils.setCallIncomingStateWithoutRestartingService();
                repository.freeDriverStatus(mCurrentActivity, handler);
                isFreeDriverApiCalled = true;
                ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                finishActivity();
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
        Utils.redLog(TAG, "Call Data: "+new Gson().toJson(callData));
        logMixpanelEvent(callData, false);
        counterTv.setText("20");

        String icon = StringUtils.EMPTY;
        //String icon = Utils.getServiceIcon(callData.getCallType());
        if (Utils.useServiceIconProvidedByAPI(callData.getCallType())) {
            icon = callData.getIcon();
        }
        if (StringUtils.isNotBlank(icon)) {
            Utils.redLog(mCurrentActivity.getClass().getSimpleName(), Utils.getCloudinaryLink(icon));
            Picasso.get().load(Utils.getCloudinaryLink(icon))
                    .placeholder(Utils.getServiceIcon(callData))
                    .into(ivCallType, new Callback() {
                        @Override
                        public void onSuccess() {
                            Utils.redLog(mCurrentActivity.getClass().getSimpleName(), "Icon OnSuccess");
                        }

                        @Override
                        public void onError(Exception e) {
                            Utils.redLog(mCurrentActivity.getClass().getSimpleName(), "Icon OnError");
                        }
                    });
        } else if (StringUtils.isNotBlank(callData.getCallType())) {
            ivCallType.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, Utils.getServiceIcon(callData)));
        } else {
            ivCallType.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.ride));
        }

        try {
            kraiKiKamaiTv.setText(String.valueOf(callData.getKraiKiKamai()));
            String cashKiWasoliValue = callData.getCashKiWasooli() < 0 ? "0" :
                    String.valueOf(callData.getCashKiWasooli());
            cashKiWasooliTv.setText(cashKiWasoliValue);
            customerRatingTv.setText(callData.getRating());
            if (Utils.isSkipDropOff(callData)) {
                estimatedDistanceTv.setText("?");
                destinationTv.setText("منتخب نہیں کی گئی");
                cashKiWasooliLayout.setVisibility(View.GONE);
                kraiKiKamaiLayout.setVisibility(View.GONE);
                kharidariPriceLayout.setVisibility(View.GONE);
                destinationTv.setAttr(mCurrentActivity, "jameel_noori_nastaleeq.ttf");
                destinationTv.setGravity(Gravity.CENTER);
                circle_distance_layout.setBackground(getResources().getDrawable(R.drawable.rating_circle_call));
                estimatedDistaneUnitTv.setVisibility(View.GONE);

            } else {
                cashKiWasooliLayout.setVisibility(View.VISIBLE);
                kraiKiKamaiLayout.setVisibility(View.VISIBLE);
                circle_distance_layout.setBackground(getResources().getDrawable(R.drawable.distance_green_circle_call));
                estimatedDistaneUnitTv.setVisibility(View.VISIBLE);
                estimatedDistanceTv.setText(String.valueOf(callData.getEstimatedDistance()));
                destinationTv.setText(callData.getEndAddress());
                distanceAwayTv.setText(callData.getDistance());
            }

            if (Utils.isPurchaseService(callData.getCallType())) {
                kharidariPriceLayout.setVisibility(View.VISIBLE);
                kharidariKiRaqamTv.setText(callData.getCodAmount());
            }

            if (Utils.isRideService(callData.getCallType())) {
                kharidariPriceLayout.setVisibility(View.GONE);
            }

            if (Utils.isDeliveryService(callData.getCallType())) {
                kharidariPriceLayout.setVisibility(View.GONE);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.redLog(TAG, e.getMessage(), e);
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
                            ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(false, mCurrentActivity);
                            finishActivity();
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