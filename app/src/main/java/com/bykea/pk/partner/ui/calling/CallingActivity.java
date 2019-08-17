package com.bykea.pk.partner.ui.calling;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.FreeDriverResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.BaseActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.DonutProgress;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.FontUtils;
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

    private boolean isCancelledByPassenger;

    @BindView(R.id.counterTv)
    FontTextView counterTv;
    @BindView(R.id.donut_progress)
    DonutProgress donutProgress;
    @BindView(R.id.activity_calling)
    RelativeLayout activity_calling;

    @BindView(R.id.ivCallType)
    AppCompatImageView ivCallType;
    @BindView(R.id.estArrivalTimeTV)
    FontTextView estArrivalTimeTV;
    @BindView(R.id.estDistanceTV)
    FontTextView estDistanceTV;
    @BindView(R.id.dropZoneNameTV)
    AutoFitFontTextView dropZoneNameTV;

    @BindView(R.id.acceptCallBtn)
    RelativeLayout acceptCallBtn;


    private UserRepository repository;
    private MediaPlayer _mpSound;
    private CallingActivity mCurrentActivity;
    private float progress = 0;
    private String tripId;
    private Integer serviceCode;

    private boolean isFreeDriverApiCalled = false;

    public String TAG = CallingActivity.class.getSimpleName();
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
            onAcceptSuccess(acceptCallResponse.isSuccess(), acceptCallResponse.getMessage());
        }

        @Override
        public void onRejectCall(final RejectCallResponse rejectCallResponse) {
            onAcceptFailed(rejectCallResponse.getMessage());
        }

        @Override
        public void onError(int errorCode, final String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Dialogs.INSTANCE.showToast(errorMessage);
                    ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                    stopSound();
                    finishActivity();
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        AppPreferences.setCallingActivityOnForeground(true);
    }

    @Override
    protected void onDestroy() {
        stopSound();
        if (AppPreferences.isOnTrip()) {
            AppPreferences.setIncomingCall(false);
        } else {
            AppPreferences.setIncomingCall(true);
        }
        AppPreferences.setCallingActivityOnForeground(false);
        Utils.unbindDrawables(activity_calling);
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {

    }

    private long mLastClickTime;
    private String acceptSeconds = "0";

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

        if (Utils.isConnected(CallingActivity.this, false))
            repository.requestLocationUpdate(mCurrentActivity, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude());

        donutProgress.setProgress(20);
        startAnimation();

        if (null != getIntent() && getIntent().getBooleanExtra("isGcm", false)) {
            Utils.redLog("FCM", "Calling Activity");
            DriverApp.getApplication().connect();
            DriverApp.startLocationService(mCurrentActivity);
        }
        setInitialData();
        if (!Utils.isModernService(serviceCode)) ackCall();
    }

    @OnClick({R.id.acceptCallBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptCallBtn:
                if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (!isFreeDriverApiCalled) {
                    stopSound();
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    acceptSeconds = counterTv.getText().toString();
                    acceptJob();
                    timer.cancel();
                }
                break;
        }
    }

    private void finishActivity() {
        if (Utils.isConnected(CallingActivity.this, false))
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
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data, true);
            } else {
                Utils.logEvent(mCurrentActivity, callData.getPassId(), Constants.AnalyticsEvents.ON_RECEIVE_NEW_JOB.replace(
                        Constants.AnalyticsEvents.REPLACE, callData.getCallType()), data, true);
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
            acceptCallBtn.setEnabled(false);
            stopSound();
            if (!isFreeDriverApiCalled) {
                Utils.setCallIncomingStateWithoutRestartingService();
                if (!Utils.isModernService(serviceCode))
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
    }

    private void ackCall() {
        repository.ackCall(mCurrentActivity, handler);
    }

    private void setInitialData() {
        NormalCallData callData = AppPreferences.getCallData();
        if (callData == null)
            return;

        tripId = callData.getTripId();
        serviceCode = callData.getServiceCode();
        Utils.redLog(TAG, "Call Data: " + new Gson().toJson(callData));
        logMixpanelEvent(callData, false);
        counterTv.setText("20");

        String icon = StringUtils.EMPTY;
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

        if (estDistanceTV != null) {
            if ((callData.getDropoffZoneNameUrdu() == null || callData.getDropoffZoneNameUrdu().isEmpty())
                    && (callData.getEndAddress() == null || callData.getEndAddress().isEmpty()))
                estDistanceTV.setText("?");
            else if (callData.getEstimatedDistance() == 0)
                estDistanceTV.setText("1");
            else
                estDistanceTV.setText(String.valueOf(Double.valueOf(Math.ceil(callData.getEstimatedDistance())).intValue()));

        }

        if (dropZoneNameTV != null) {
            if (callData.getDropoffZoneNameUrdu() != null && !callData.getDropoffZoneNameUrdu().isEmpty())
                dropZoneNameTV.setText(callData.getDropoffZoneNameUrdu());
            else if (callData.getEndAddress() != null && !callData.getEndAddress().isEmpty())
                dropZoneNameTV.setText(FontUtils.getStyledTitle(mCurrentActivity, callData.getEndAddress(), Constants.FontNames.OPEN_SANS_BOLD));
            else
                dropZoneNameTV.setText(getString(R.string.customer_btayega));
        }

        if (StringUtils.isNotBlank(callData.getArivalTime())) {
            if (Integer.parseInt(callData.getArivalTime()) <= 0)
                estArrivalTimeTV.setText("1");
            else
                estArrivalTimeTV.setText(callData.getArivalTime());
        }
    }

    /**
     * Inform server to accept the job request
     */
    private void acceptJob() {
        if (Utils.isModernService(serviceCode)) {
            JobsRepository jobsRepo = Injection.INSTANCE.provideJobsRepository(getApplication().getApplicationContext());
            jobsRepo.acceptJob(tripId, Integer.valueOf(acceptSeconds), new JobsDataSource.AcceptJobCallback() {
                @Override
                public void onJobAccepted() {
                    if (!isCancelledByPassenger) {
                        onAcceptSuccess(true, "Job Accepted");
                    } else {
                        cancelRide();
                    }
                    if (BuildConfig.DEBUG)
                        Toast.makeText(getApplication().getApplicationContext(), "Job Accepted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onJobAcceptFailed() {
                    onAcceptFailed("Job Accept Failed");
                    if (BuildConfig.DEBUG)
                        Toast.makeText(getApplication().getApplicationContext(), "Job Accept Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Legacy accept call on socket
            repository.requestAcceptCall(mCurrentActivity, acceptSeconds, handler);
        }
    }

    /**
     * On success of job call accept
     *
     * @param success Success status
     * @param message Success message
     */
    private void onAcceptSuccess(Boolean success, String message) {

        if (mCurrentActivity != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Dialogs.INSTANCE.showTempToast(message);
                    if (!isCancelledByPassenger) {
                        if (success) {
                            AppPreferences.clearTripDistanceData();
                            AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL);

                            NormalCallData callData = AppPreferences.getCallData();
                            callData.setStatus(TripStatus.ON_ACCEPT_CALL);
                            AppPreferences.setCallData(callData);
                            AppPreferences.setTripAcceptTime(System.currentTimeMillis());
                            AppPreferences.setEstimatedFare(callData.getKraiKiKamai());
                            logMixpanelEvent(callData, true);

                            AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude());

                            AppPreferences.setIsOnTrip(true);
                            AppPreferences.setDeliveryType(Constants.CallType.SINGLE);
                            ActivityStackManager.getInstance().startJobActivity(mCurrentActivity);
                            stopSound();
                            finishActivity();
                        } else {
                            Utils.setCallIncomingState();
                            Dialogs.INSTANCE.showToast(message);
                        }
                    } else {
                        cancelRide();
                    }
                }
            });
        }
    }

    /**
     * On failure of accept call
     *
     * @param message Failure message
     */
    private void onAcceptFailed(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialogs.INSTANCE.showToast(message);
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

    @Subscribe
    public void onEvent(final Intent intent) {

        isCancelledByPassenger = true;

        if (mCurrentActivity != null) {
            mCurrentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != intent && null != intent.getExtras()) {
                        if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_RIDE)
                                || intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_BY_ADMIN)) {
                            cancelRide();
                        }
                    }
                }

            });
        }
    }

    private void cancelRide() {
        Utils.setCallIncomingState();
        AppPreferences.setTripStatus(TripStatus.ON_FREE);
        stopSound();
        ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(false, mCurrentActivity);
        finishActivity();
    }

    @Subscribe
    public void onEvent(final String action) {
        if (action.equalsIgnoreCase(Keys.MULTIDELIVERY_MISSED_EVENT)) {
            Utils.setCallIncomingState();
            AppPreferences.setTripStatus(TripStatus.ON_FREE);
            stopSound();
            ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(false, mCurrentActivity);
            finishActivity();
        }
    }
}
