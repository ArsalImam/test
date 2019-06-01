package com.bykea.pk.partner.ui.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.MultiDeliveryAcceptCallResponse;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.HttpURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MultiDeliveryCallingActivity extends BaseActivity {
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
    @BindView(R.id.serviceImageView)
    AppCompatImageView serviceImageView;

    @BindView(R.id.pickLocationTv)
    TextView pickLocationTv;

    @BindView(R.id.pickDistanceTv)
    TextView pickDistanceTv;

    @BindView(R.id.deliveryCountTv)
    TextView deliveryCountTv;

    @BindView(R.id.dropDistanceTv)
    TextView dropDistanceTv;

    @BindView(R.id.timeTv)
    TextView timeTv;

    @BindView(R.id.activity_multi_delivery_calling)
    LinearLayout activity_multi_delivery_calling;

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

    @BindView(R.id.kharidariKiRaqamTv)
    FontTextView kharidariKiRaqamTv;

    private UserRepository repository;
    private MediaPlayer _mpSound;
    private MultiDeliveryCallingActivity mCurrentActivity;
    private float progress = 0;

    private int counter = 0;
    private int total = 1;

    private boolean isFreeDriverApiCalled = false;
    private MultiDeliveryCallDriverData response;
    private int timeInMilliSeconds;
    private int timePercentage;
    private int ACCEPTANCE_TIMEOUT;
    private CountDownTimer timer;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_delivery_calling);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        repository = new UserRepository();
        Utils.unlockScreen(mCurrentActivity);
        AppPreferences.setStatsApiCallRequired(true);
        //To inactive driver during passenger calling state
        AppPreferences.setTripStatus(TripStatus.ON_IN_PROGRESS);
        repository.requestLocationUpdate(
                mCurrentActivity,
                handler,
                AppPreferences.getLatitude(),
                AppPreferences.getLongitude());
        response = AppPreferences.getMultiDeliveryCallDriverData();
        donutProgress.setProgress(response.getTimer());


        if (null != getIntent() && getIntent().getBooleanExtra(Constants.IS_FROM_GCM,
                false)) {
            DriverApp.getApplication().connect();
            DriverApp.startLocationService(mCurrentActivity);
        }

        setInitialData();

    }



    @Override
    protected void onResume() {
        super.onResume();
//        WebIORequestHandler.getInstance().setContext(mCurrentActivity);
        /*SETTING SERVICE CONTEXT WITH ACTIVITY TO SEND BROADCASTS*/
//        LocationService.setContext(MultiDeliveryCallingActivity.this);
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
        Utils.unbindDrawables(activity_multi_delivery_calling);
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
            case R.id.acceptCallBtn:
                if (mLastClickTime != 0 && (SystemClock.elapsedRealtime() - mLastClickTime < 1000)) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (!isFreeDriverApiCalled) {
                    stopSound();
                    Dialogs.INSTANCE.showLoader(mCurrentActivity);
                    acceptSeconds = counterTv.getText().toString();
                    repository.requestMultiDeliveryAcceptCall(
                            mCurrentActivity,
                            acceptSeconds,
                            handler
                    );
                }
                break;
        }
    }

    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onMultiDeliveryAcceptCall(MultiDeliveryAcceptCallResponse response) {
            if (mCurrentActivity != null) {
                AppPreferences.clearTripDistanceData();
                AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL);

                MultiDeliveryCallDriverData multiDeliveryAcceptCallResponse =
                        AppPreferences.getMultiDeliveryCallDriverData();
                if (multiDeliveryAcceptCallResponse != null) {
                    multiDeliveryAcceptCallResponse.setBatchStatus(TripStatus.ON_ACCEPT_CALL);
                    multiDeliveryAcceptCallResponse.setAcceptTime(
                            System.currentTimeMillis() +
                            AppPreferences.getServerTimeDifference()
                    );
                    AppPreferences.setMultiDeliveryCallDriverData(multiDeliveryAcceptCallResponse);
                }

                Dialogs.INSTANCE.dismissDialog();
                AppPreferences.setIsOnTrip(true);
                AppPreferences.setDeliveryType(Constants.CallType.BATCH);
                ActivityStackManager
                        .getInstance()
                        .startMultiDeliveryBookingActivity(mCurrentActivity);
                stopSound();
                finishActivity();
            }
        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialogs.INSTANCE.dismissDialog();
                    Dialogs.INSTANCE.showToast(mCurrentActivity, errorMessage);
                    if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    } else {
                        ActivityStackManager.getInstance().startHomeActivity(true,
                                mCurrentActivity);
                        stopSound();
                        finishActivity();
                    }
                }
            });
        }
    };

    private void finishActivity() {
        repository.requestLocationUpdate(mCurrentActivity, handler,
                AppPreferences.getLatitude(),
                AppPreferences.getLongitude());
        mCurrentActivity.finish();
    }

    /**
     * Count down timer class
     */
    private class CountDownTimerClass extends CountDownTimer {

        int totalTime = timeInMilliSeconds /1000;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountDownTimerClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            isRunning = true;
            progress = (ACCEPTANCE_TIMEOUT - millisUntilFinished) / 1000;

            if (progress >= response.getTimer()) {
                timer.onFinish();
            } else {
                if (!_mpSound.isPlaying()) _mpSound.start();
                donutProgress.setProgress(progress);
                try {
                    int elapsedTime = (timeInMilliSeconds - ACCEPTANCE_TIMEOUT) / 1000;
                    Log.d("progress", progress+" elapsed" + elapsedTime);
                    counterTv.setText(String.valueOf((int) ((millisUntilFinished / 1000) +
                            elapsedTime)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFinish() {
            isRunning = false;
            totalTime = 0;
            donutProgress.setProgress(response.getTimer());
            counterTv.setText(String.valueOf(totalTime));
//            rejectCallBtn.setEnabled(false);
            acceptCallBtn.setEnabled(false);
            stopSound();
            ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
            finishActivity();
            /*if (!isFreeDriverApiCalled) {
                Utils.setCallIncomingStateWithoutRestartingService();
                //repository.freeDriverStatus(mCurrentActivity, handler);
                isFreeDriverApiCalled = true;

            }*/
        }
    }

    /**
     * Start the timer animation with sound
     */
    private void startAnimation() {
        _mpSound = MediaPlayer.create(mCurrentActivity, R.raw.ringtone);
        _mpSound.start();
        timer.start();
    }

    /**
     * Stop the sound and cancel the timer.
     */
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

    /**
     * Set the initial data.
     *
     * <p>
     *     <ul>Calculate the timer percentage</ul>
     *     <ul>Initiate the timer</ul>
     *     <ul>Start the timer animation</ul>
     *     <ul>Map the data to UI</ul>
     * </p>
     */
    private void setInitialData() {

        //Todo 1: Change the object and log the event on mixpanel
        //logMixpanelEvent(callData, false);
        timeInMilliSeconds = Utils.getTimeInMilliseconds(response.getTimer());
        timePercentage = Utils.getTimeInPercentage(timeInMilliSeconds,
                Constants.TIME_IN_MILLISECONDS_PERCENTAGE);
        ACCEPTANCE_TIMEOUT = timeInMilliSeconds - timePercentage;

        counterTv.setText(String.valueOf(response.getTimer()));
        timer = new CountDownTimerClass(ACCEPTANCE_TIMEOUT, 100);

        startAnimation();

        mapCallDataToUI(response);
    }

    /***
     * Map the calling data to UI which is comming from socket.
     * @param response is a socket response which is listen by Call onLoadBoardListFragmentInteractionListener
     */
    private void mapCallDataToUI(MultiDeliveryCallDriverData response) {
        try {
            int i = 0;
            String type = response.getBookings().get(i).getTrip().getType();
            deliveryCountTv.setText(String.valueOf(response.getBookings().size()));
            Utils.loadMultipleDeliveryImageURL(
                    serviceImageView,
                    response.getImageURL(),
                    R.drawable.bhejdo
            );
            pickLocationTv.setText(response.getPickup().getPickupAddress());
            pickDistanceTv.setText(Utils.getDistance(response.getPickup().getDistance()));
            dropDistanceTv.setText(Utils.getDistance(response.getEstTotalDistance()));
            timeTv.setText(String.valueOf(
                    Utils.getDuration(response.getEstTotalDuration())
            ));
            kraiKiKamaiTv.setText(String.valueOf(response.getEstFare()));

            String cashKiWasoliValue = response.getEstCashCollection() < 0 ?
                    getString(R.string.cash_value_zero) :
                    String.valueOf(response.getEstCashCollection());
            cashKiWasooliTv.setText(cashKiWasoliValue);

            cashKiWasooliLayout.setVisibility(View.VISIBLE);
            kraiKiKamaiLayout.setVisibility(View.VISIBLE);

            if (Utils.isPurchaseService(type)) {
                kharidariPriceLayout.setVisibility(View.VISIBLE);
                //Todo 2: Add it later
                //kharidariKiRaqamTv.setText(response.getCodAmount());
            } else {
                kharidariPriceLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEvent(final String action) {
        if (action.equalsIgnoreCase(Keys.MULTIDELIVERY_MISSED_EVENT)) {
            if (isRunning) {
                finishActivity();
                stopSound();
            }
        }
    }


}
