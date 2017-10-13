package com.bykea.pk.partner.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.FreeDriverResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.services.LocationService;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.DonutProgress;
import com.bykea.pk.partner.widgets.FontTextView;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CallingActivity extends BaseActivity {
    @Bind(R.id.counterTv)
    FontTextView counterTv;
    @Bind(R.id.callerNameTv)
    FontTextView callerNameTv;
    @Bind(R.id.startAddressTv)
    FontTextView startAddressTv;
    @Bind(R.id.rejectCallBtn)
    FontTextView rejectCallBtn;
    @Bind(R.id.acceptCallBtn)
    FontTextView acceptCallBtn;
    @Bind(R.id.donut_progress)
    DonutProgress donutProgress;
    @Bind(R.id.distanceTv)
    FontTextView distanceTv;
    @Bind(R.id.timeTv)
    FontTextView timeTv;
    @Bind(R.id.ivCallType)
    ImageView ivCallType;

    private UserRepository repository;
    private MediaPlayer _mpSound;
    private CallingActivity mCurrentActivity;
    private float progress = 0;

    private int counter = 0;
    private int total = 1;

    private boolean isCalled = false;

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
            ((DriverApp) mCurrentActivity.getApplicationContext()).connect("Notification");
            WebIORequestHandler.getInstance().setContext(mCurrentActivity);
            DriverApp.startLocationService(mCurrentActivity);
        }
        ackCall();
        setInitialData();


    }

    @Override
    protected void onResume() {
        super.onResume();
        WebIORequestHandler.getInstance().setContext(mCurrentActivity);
         /*SETTING SERVICE CONTEXT WITH ACTIVITY TO SEND BROADCASTS*/
//        LocationService.setContext(CallingActivity.this);
        AppPreferences.setCallingActivityOnForeground(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSound();
        unregisterReceiver(cancelRideReceiver);
        if (AppPreferences.isOnTrip()) {
            AppPreferences.setIncomingCall(false);
        } else {
            AppPreferences.setTripStatus(TripStatus.ON_FREE);
            AppPreferences.setIncomingCall(true);
        }
        AppPreferences.setCallingActivityOnForeground(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        stopSound();


    }

    @Override
    public void onBackPressed() {

    }

    @OnClick({R.id.rejectCallBtn, R.id.acceptCallBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rejectCallBtn:
//                Dialogs.INSTANCE.showToast(mCurrentActivity, "This feature will be in phase 2");
                stopSound();
                repository.requestRejectCall(mCurrentActivity, handler);
                if (timer != null) {
                    timer.cancel();
                }
                break;
            case R.id.acceptCallBtn:
                stopSound();
                Dialogs.INSTANCE.showLoader(mCurrentActivity);
                repository.requestAcceptCall(mCurrentActivity, counterTv.getText().toString(), handler);
                timer.cancel();
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
                        ActivityStackManager.getInstance(mCurrentActivity).startHomeActivity(true);
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
                            AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL);
                            AppPreferences.setIsOnTrip(true);
                            ActivityStackManager.getInstance(mCurrentActivity).startJobActivity();
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
                    ActivityStackManager.getInstance(mCurrentActivity).startHomeActivity(true);
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
                    ActivityStackManager.getInstance(mCurrentActivity).startHomeActivity(true);
                    stopSound();
                    mCurrentActivity.finish();
                }
            });
        }
    };

    private CountDownTimer timer = new CountDownTimer(20800, 100) {

        @Override
        public void onTick(long millisUntilFinished) {
            if (progress >= 20) {
                donutProgress.setProgress(20);
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
            rejectCallBtn.setEnabled(false);
            acceptCallBtn.setEnabled(false);
            stopSound();
            if (!isCalled) {
                repository.freeDriverStatus(mCurrentActivity, handler);
                isCalled = true;
                ActivityStackManager.getInstance(mCurrentActivity).startHomeActivity(true);
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
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.BROADCAST_CANCEL_RIDE);
        registerReceiver(cancelRideReceiver, intentFilter);
    }

    private void ackCall() {
        repository.ackCall(mCurrentActivity, handler);
    }

    private void setInitialData() {
        NormalCallData callData = AppPreferences.getCallData();
        callerNameTv.setText(callData.getPassName());
        startAddressTv.setText(callData.getStartAddress());
        timeTv.setText(callData.getArivalTime() + " min");
        distanceTv.setText(callData.getDistance() + " km");
        counterTv.setText("20");
        if (StringUtils.isNotBlank(callData.getIcon())) {
            Picasso.with(mCurrentActivity).load(Utils.getCloudinaryLink(callData.getIcon(), mCurrentActivity))
                    .placeholder(getServiceIcon(callData))
                    .fit().centerInside()
                    .into(ivCallType);
        } else if (StringUtils.isNotBlank(callData.getCallType())) {
            ivCallType.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, getServiceIcon(callData)));
        } else {
            ivCallType.setImageDrawable(ContextCompat.getDrawable(mCurrentActivity, R.drawable.ride));
        }
    }


    private Integer getServiceIcon(NormalCallData callData) {
        String callType = callData.getCallType().replace(" ", StringUtils.EMPTY).toLowerCase();
        switch (callType) {
            case "parcel":
            case "send":
                return R.drawable.bhejdo;
            case "bring":
                return R.drawable.lay_ao;
            case "ride":
                return R.drawable.ride;
            case "top-up":
                return R.drawable.top_up;
            case "utilitybill":
                return R.drawable.bill;
            case "deposit":
                return R.drawable.lay_ao_copy_1;
            case "carryvan":
                return R.drawable.carry_van;
            default:
                return R.drawable.ride;
        }
    }

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
                                ActivityStackManager.getInstance(mCurrentActivity).startHomeActivityFromCancelTrip(false);
                                mCurrentActivity.finish();
                            }
                        }
                    }
                });
            }

        }
    };
}
