package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.dal.Stop;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.source.socket.payload.JobCall;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.FreeDriverResponse;
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
import com.bykea.pk.partner.widgets.AutoFitFontTextView;
import com.bykea.pk.partner.widgets.DonutProgress;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bykea.pk.partner.utils.Constants.ServiceType.RIDE_CODE;
import static com.bykea.pk.partner.utils.Constants.ServiceType.SEND_CODE;
import static com.bykea.pk.partner.utils.Constants.ServiceType.SEND_COD_CODE;


public class JobCallActivity extends BaseActivity {

    public static String KEY_CALL_DATA = "KEY_CALL_DATA";
    public static String KEY_IS_FROM_PUSH = "isGcm";
    public String TAG = JobCallActivity.class.getSimpleName();
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
    private MediaPlayer _mpSound;
    private String tripId;
    private boolean isCancelledByPassenger;
    private long mLastClickTime;
    private int RIDE_ACCEPTANCE_TIMEOUT = 20;
    int timerDuration = RIDE_ACCEPTANCE_TIMEOUT;
    private String acceptSeconds = "0";
    private JobCall jobCall;
    private UserRepository userRepo;

    private CountDownTimer timer;
    /**
     * Response handler for rest calls
     */
    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onAck(final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.appToastDebug(JobCallActivity.this, msg);
                }
            });
        }

        @Override
        public void onFreeDriver(FreeDriverResponse freeDriverResponse) {
            JobCallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActivityStackManager.getInstance().startHomeActivity(true, JobCallActivity.this);
                    stopSound();
                    finishActivity();
                }
            });

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
                    ActivityStackManager.getInstance().startHomeActivity(true, JobCallActivity.this);
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
    protected void onStop() {
        super.onStop();
        AppPreferences.setCallingActivityOnForeground(false);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            jobCall = (JobCall) getIntent().getSerializableExtra(KEY_CALL_DATA);
            timerDuration = jobCall.getTimer();

            if (getIntent().getBooleanExtra("isGcm", false)) {
                Utils.redLog("FCM", "Calling Activity");
                DriverApp.getApplication().connect();
                DriverApp.startLocationService(this);
            }
        }

        userRepo = new UserRepository();
        Utils.unlockScreen(this);
        AppPreferences.setStatsApiCallRequired(true);
        //To inactive driver during passenger calling state
        AppPreferences.setTripStatus(TripStatus.ON_IN_PROGRESS);

        if (Utils.isConnected(JobCallActivity.this, false))
            userRepo.requestLocationUpdate(this, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude());


        setInitialData();
        startTimer();
    }

    @OnClick({R.id.acceptCallBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptCallBtn:
                if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                stopSound();
                Dialogs.INSTANCE.showLoader(JobCallActivity.this);
                acceptSeconds = counterTv.getText().toString();
                acceptJob();
                timer.cancel();
                break;
        }
    }

    @Subscribe
    public void onEvent(final Intent intent) {
        isCancelledByPassenger = true;
        JobCallActivity.this.runOnUiThread(() -> {
            if (null != intent && null != intent.getExtras()) {
                if (intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_RIDE)
                        || intent.getStringExtra("action").equalsIgnoreCase(Keys.BROADCAST_CANCEL_BY_ADMIN)) {
                    onJobCallCancelled();
                }
            }
        });

    }

    /**
     * Finish current screen
     */
    private void finishActivity() {
        if (Utils.isConnected(JobCallActivity.this, false))
            userRepo.requestLocationUpdate(this, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude());
        finish();
    }

    /**
     * Log call data to mix panel
     *
     * @param callData   Job call data
     * @param isOnAccept is post accepted
     */
    private void logMixPanelEvent(JobCall callData, boolean isOnAccept) {
        try {

            JSONObject data = new JSONObject();
            data.put("PassengerID", callData.getCustomer_id());
            data.put("DriverID", AppPreferences.getPilotData().getId());
            data.put("TripID", callData.getTrip_id());
            data.put("TripNo", callData.getBooking_no());
            data.put("PickUpLocation", callData.getPickup().getLat() + "," + callData.getPickup().getLng());
            data.put("timestamp", Utils.getIsoDate());
            if (callData.getDropoff() != null) {
                data.put("DropOffLocation", callData.getDropoff().getLat() + "," + callData.getDropoff().getLng());
            }
            data.put("ETA", "" + callData.getPickup().getDuration());
            data.put("EstimatedDistance", AppPreferences.getEstimatedDistance());
            data.put("CurrentLocation", Utils.getCurrentLocation());
            data.put("DriverName", AppPreferences.getPilotData().getFullName());
            data.put("SignUpCity", AppPreferences.getPilotData().getCity().getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start timer with dynamic timing, also play the calling sound
     */
    private void startTimer() {

        _mpSound = MediaPlayer.create(this, R.raw.ringtone);
        _mpSound.start();

        donutProgress.setMax(timerDuration);
        donutProgress.setProgress(0);

        timer = new CountDownTimer(timerDuration * 1000, 1000) {
            int progress = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                progress++;
                if (progress < timerDuration) {
                    donutProgress.setProgress(progress);
                    counterTv.setText(String.valueOf(timerDuration - progress));
                } else {
                    timer.onFinish();
                }
            }

            @Override
            public void onFinish() {
                donutProgress.setProgress(0);
                counterTv.setText("0");
                acceptCallBtn.setEnabled(false);
                stopSound();
                Utils.setCallIncomingStateWithoutRestartingService();
                ActivityStackManager.getInstance().startHomeActivity(true, JobCallActivity.this);
                finishActivity();
            }
        };
        timer.start();
    }

    /**
     * Stop calling sound
     */
    private void stopSound() {
        if (null != _mpSound && _mpSound.isPlaying()) {
            _mpSound.stop();
        }
        if (null != timer) timer.cancel();
    }

    /**
     * Render job calling data
     */
    private void setInitialData() {
        tripId = jobCall.getTrip_id();
        Utils.redLog(TAG, "Call Data: " + new Gson().toJson(jobCall));
        logMixPanelEvent(jobCall, false);
        counterTv.setText(String.valueOf(timerDuration));

        ivCallType.setImageDrawable(ContextCompat.getDrawable(this, getJobImage(jobCall.getService_code())));
        estArrivalTimeTV.setText(getArrivalTime(jobCall.getPickup()));
        dropZoneNameTV.setText(getDropOffZoneName(jobCall.getDropoff()));
        estDistanceTV.setText(getDropOffDistance(jobCall.getDropoff()));
    }

    /**
     * Get job service image
     *
     * @param service_code Job service code
     * @return Drawable ID
     */
    private int getJobImage(int service_code) {
        if (service_code == RIDE_CODE)
            return R.drawable.ride;
        else if (service_code == SEND_CODE || service_code == SEND_COD_CODE)
            return R.drawable.bhejdo_no_caption;
        else
            return R.drawable.ride;
    }

    /**
     * Get arrival time from current location to pickup
     *
     * @param pickup Pickup stop
     * @return Displayable time in minutes
     */
    private String getArrivalTime(Stop pickup) {
        if (pickup != null && pickup.getDuration() > 0)
            return String.valueOf(pickup.getDuration() / 60);
        else
            return "1";
    }

    /**
     * Get the drop-off zone name to show
     *
     * @param dropoff Drop-off stop
     * @return Displayable zone name
     */
    private String getDropOffZoneName(Stop dropoff) {
        if (dropoff != null) {
            if (dropoff.getZone_ur() != null && !dropoff.getZone_ur().isEmpty()) {
                return dropoff.getZone_ur();
            } else if (dropoff.getZone_en() != null && !dropoff.getZone_en().isEmpty())
                return dropoff.getZone_en();
            else if (dropoff.getAddress() != null && !dropoff.getAddress().isEmpty())
                return dropoff.getAddress();
            else
                return getString(R.string.customer_btayega);
        } else
            return getString(R.string.customer_btayega);
    }

    /**
     * Get the drop-off distance from pickup stop
     *
     * @param dropoff Drop-off stop
     * @return Displayable distance in kilometer
     */
    private String getDropOffDistance(Stop dropoff) {
        if (dropoff != null && dropoff.getDistance() != 0)
            return String.valueOf(dropoff.getDistance() / 1000);
        else return "?";
    }

    /**
     * Inform server to accept the job request
     */
    private void acceptJob() {
        JobsRepository jobsRepo = Injection.INSTANCE.provideJobsRepository(getApplication().getApplicationContext());
        jobsRepo.acceptJob(tripId, Integer.valueOf(acceptSeconds), new JobsDataSource.AcceptJobCallback() {
            @Override
            public void onJobAccepted() {
                if (!isCancelledByPassenger) {
                    onAcceptSuccess(true, "Job Accepted");
                } else {
                    onJobCallCancelled();
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
    }

    /**
     * On success of job call accept
     *
     * @param success Success status
     * @param message Success message
     */
    private void onAcceptSuccess(Boolean success, String message) {
        runOnUiThread(() -> {
            Dialogs.INSTANCE.dismissDialog();
            Dialogs.INSTANCE.showTempToast(message);
            if (!isCancelledByPassenger) {
                if (success) {
                    AppPreferences.clearTripDistanceData();
                    AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL);
                    AppPreferences.setTripAcceptTime(System.currentTimeMillis());
                    logMixPanelEvent(jobCall, true);
                    AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude());
                    AppPreferences.setIsOnTrip(true);
                    AppPreferences.setDeliveryType(Constants.CallType.SINGLE);
                    ActivityStackManager.getInstance().startJobActivity(JobCallActivity.this);
                    stopSound();
                    finishActivity();
                } else {
                    Utils.setCallIncomingState();
                    Dialogs.INSTANCE.showToast(message);
                }
            } else {
                onJobCallCancelled();
            }
        });

    }

    /**
     * On failure of accept call
     *
     * @param message Failure message
     */
    private void onAcceptFailed(String message) {
        runOnUiThread(() -> {
            Dialogs.INSTANCE.showToast(message);
            Dialogs.INSTANCE.dismissDialog();
            if (AppPreferences.isOnTrip()) {
                AppPreferences.setIncomingCall(false);
                AppPreferences.setTripStatus(TripStatus.ON_FREE);
            } else {
                AppPreferences.setIncomingCall(true);
            }
            ActivityStackManager.getInstance().startHomeActivity(true, JobCallActivity.this);
            stopSound();
            finishActivity();
        });
    }

    /**
     * On job call cancelled during job calling
     */
    private void onJobCallCancelled() {
        Utils.setCallIncomingState();
        AppPreferences.setTripStatus(TripStatus.ON_FREE);
        stopSound();
        ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(false, this);
        finishActivity();
    }
}
