package com.bykea.pk.partner.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.OfflineNotificationData;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Service to handle inactive push notifications
 */
public class HandleInactivePushService extends Service {
    private MediaPlayer player;
    private Handler mpHandler = new Handler();
    private boolean hasLocationResponseReceived = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private HandleInactivePushService mContext;
    private EventBus mBus = EventBus.getDefault();
    private boolean playMusic = true;
    private OfflineNotificationData offlineNotificationData;
    private UserRepository mUserRepository;

    private boolean isCountDownTimerRunning;
    final CountDownTimer countDownTimer = new CountDownTimer(Constants.LOCATION_API_WAIT_ON_INACTIVE_PUSH, Constants.LOCATION_API_WAIT_ON_INACTIVE_PUSH) {
        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Sound playing");
            isCountDownTimerRunning = false;
            if (player != null) {
                /*if (playMusic) {
                    if (!player.isPlaying())
                        player.start();*/
                mpHandler.postDelayed(soundRunnable, Constants.IN_ACTIVE_MUSIC_SOUND);
                //}
            } else {
                onInactiveByCronJob(getString(R.string.driver_offline_crone_job_ur));
            }

        }
    };

    private Runnable soundRunnable = new Runnable() {
        @Override
        public void run() {
            if (!hasLocationResponseReceived) {
                onInactiveByCronJob(getString(R.string.driver_offline_crone_job_ur));
            } else {
                stopMusicPlayer();
                stopSelf();
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUserRepository = new UserRepository();
        player = MediaPlayer.create(this, R.raw.alert_signal); //select music file
        player.setLooping(true);
        mContext = this;
        if (intent != null && intent.getExtras() != null
                && intent.hasExtra(Constants.Extras.INACTIVE_PUSH_DATA)) {
            countDownTimer.cancel();
            countDownTimer.start();
            if (Utils.canSendLocation()) {
                if (player != null)
                    player.start();
                ActivityStackManager.getInstance().startHomeActvityForInActivePush(this);
            }
            onInactivePushReceived((OfflineNotificationData) intent.getParcelableExtra(Constants.Extras.INACTIVE_PUSH_DATA));
        }
        return START_NOT_STICKY;
    }

    /**
     * This method sets inactive status and displays a notification
     *
     * @param data String notification message
     */
    private void onInactiveByCronJob(String data) {
        // //set looping
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "onInactiveByCronJob " + data);
        stopMusicPlayer();
        AppPreferences.setAdminMsg(null);
        AppPreferences.setAvailableStatus(false);
        mBus.post(Keys.INACTIVE_PUSH);
        Notifications.generateAdminNotification(mContext, data);
        stopSelf();

    }


    /**
     * This method validates the inactive push notification's data
     *
     * @param data Push Notification data
     */
    private synchronized void onInactivePushReceived(final OfflineNotificationData data) {
        offlineNotificationData = data;
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Inactive Push Received");
        if (AppPreferences.isLoggedIn() && (AppPreferences.getAvailableStatus() || AppPreferences.isOutOfFence()) && Utils.isInactiveCheckRequired()) {
            Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Driver is Logged In");
            AppPreferences.setInactiveCheckTime(System.currentTimeMillis());
            /*
             * Check Coordinates when there's any delay in FCM Push Notification and ignore
             * this notification when there are different coordinates.
             * */
            if (StringUtils.isNotBlank(data.getLat()) && StringUtils.isNotBlank(data.getLng())
                 /*   && data.getLat().equalsIgnoreCase(AppPreferences.getLastUpdatedLatitude())
                    && data.getLng().equalsIgnoreCase(AppPreferences.getLastUpdatedLongitude())*/ && !isCountDownTimerRunning) {
                Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Valid Data");
                if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable()) {
                    //If we don't get response of location update in 15 sec, then we'll consider driver is in inactive state
                    Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Valid Connection and GPS is active");
                    //countDownTimer.cancel();
                    //countDownTimer.start();
                    isCountDownTimerRunning = true;
                    if (Utils.isConnected(this, false)) {
                        mUserRepository.requestLocationUpdate(mContext, responseHandler,
                                AppPreferences.getLatitude(), AppPreferences.getLongitude());
                    }
                   /* new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });*/
                } else {
                    Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "onInactiveByCronJob | GPS = " + Utils.isGpsEnable() + " Internet = " + Connectivity.isConnectedFast(mContext));
                    /*if (playMusic) {
                        if (player != null && !player.isPlaying())
                            player.start();*/
                    mpHandler.postDelayed(soundRunnable, Constants.IN_ACTIVE_MUSIC_SOUND);
                    //}
                    //onInactiveByCronJob(data.getMessage());
                }
            } else {
                Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "FCM Ignored. Location Already Updated via update-lat-lng API or API is being called.");
                stopSelf();
            }
        } else {
            String logMsg = "FCM Ignored. login = " + AppPreferences.isLoggedIn() + "active = " + AppPreferences.getAvailableStatus() + " outOfFense = " + AppPreferences.isOutOfFence() + " InactiveCheckRequired = " + Utils.isInactiveCheckRequired();
            Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, logMsg);
            stopMusicPlayer();
            stopSelf();
        }
    }


    //region Socket Events response Handler


    private UserDataHandler responseHandler = new UserDataHandler() {
        @Override
        public void onLocationUpdate(LocationResponse response) {
            super.onLocationUpdate(response);
            hasLocationResponseReceived = true;
            isCountDownTimerRunning = false;
            Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH,
                    "location API Response: " + new Gson().toJson(response));
            if (response.isSuccess()) {
                AppPreferences.setDriverOfflineForcefully(false);
                AppPreferences.setLocationSocketNotReceivedCount(Constants.LOCATION_RESPONSE_COUNTER_RESET);
                ActivityStackManager.getInstance().restartLocationService(mContext);
            } else {
                handleLocationErrorUseCase(response);
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            onLocationUpdateError(errorCode, errorMessage, offlineNotificationData);
            stopMusicPlayer();
            stopSelf();
        }
    };

    /***
     * Handle Location API Error case for API failures.
     * @param locationResponse latest response from server.
     */
    private void handleLocationErrorUseCase(LocationResponse locationResponse) {
        if (locationResponse != null) {
            switch (locationResponse.getCode()) {
                case Constants.ApiError.BUSINESS_LOGIC_ERROR: {
                    Utils.handleLocationBusinessLogicErrors(mBus, locationResponse);
                    break;
                }
                //TODO Will update unauthorized check on error callback when API team adds 401 status code in their middle layer.
                case HTTPStatus.UNAUTHORIZED: {
                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    break;
                }
                default:
                    Utils.appToast(locationResponse.getMessage());
            }
        }

    }

    //endregion


    /**
     * This method handle error case of Location API
     *
     * @param errorCode    Int api error code
     * @param errorMessage String api error message
     * @param data         Notification data
     */
    private void onLocationUpdateError(int errorCode, String errorMessage, OfflineNotificationData data) {
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "onLocationUpdateError " + errorMessage);
        if (errorCode == HTTPStatus.UNAUTHORIZED) {
            EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
        } else if (errorCode == HTTPStatus.FENCE_ERROR) {
            AppPreferences.setOutOfFence(true);
            AppPreferences.setAvailableStatus(false);
            mBus.post(Keys.INACTIVE_FENCE);
        } else if (errorCode == HTTPStatus.INACTIVE_DUE_TO_WALLET_AMOUNT) {
            if (StringUtils.isNotBlank(errorMessage)) {
                AppPreferences.setWalletIncreasedError(errorMessage);
            }
            AppPreferences.setWalletAmountIncreased(true);
            AppPreferences.setAvailableStatus(false);
            mBus.post(Keys.INACTIVE_FENCE);
        } else if (errorCode == HTTPStatus.FENCE_SUCCESS) {
            AppPreferences.setOutOfFence(false);
            AppPreferences.setAvailableStatus(true);
            mBus.post(Keys.ACTIVE_FENCE);
        } /*else {
            onInactiveByCronJob(data.getMessage());
        }*/
    }

    @Override
    public void onDestroy() {
        //stopMusicPlayer();
        stopSelf();
        super.onDestroy();
    }

    /***
     * Stop music player tone.
     */
    private void stopMusicPlayer() {
        try {
            if (player != null) {
                player.reset();// It requires again setDataSource for player object.
                player.stop();// Stop it
                player.release();// Release it
                player = null; // Initialize it to null so it can be used later
            }
        } catch (Exception e) {
            Utils.redLog(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Music player ex", e);
            player.reset();// It requires again setDataSource for player object.
            player.release();// Release it
            player = null; // Initialize it to null so it can be used later
        }

    }
}
