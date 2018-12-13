package com.bykea.pk.partner.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIO;
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

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Service to handle inactive push notifications
 */
public class HandleInactivePushService extends Service {
    private MediaPlayer player;
    private Handler mpHanlder = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.ringtone); //select music file
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private HandleInactivePushService mContext;
    private EventBus mBus = EventBus.getDefault();

    private boolean isCountDownTimerRunning;
    final CountDownTimer countDownTimer = new CountDownTimer(Constants.LOCATION_API_WAIT_ON_INACTIVE_PUSH, Constants.LOCATION_API_WAIT_ON_INACTIVE_PUSH) {
        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            player.start();
            isCountDownTimerRunning = false;
            mpHanlder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //TODO update msg
                    onInactiveByCronJob(getString(R.string.driver_offline_crone_job_ur));
                }
            }, Constants.IN_ACTIVE_MUSIC_SOUND);

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        if (intent != null && intent.getExtras() != null
                && intent.hasExtra(Constants.Extras.INACTIVE_PUSH_DATA)) {
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
        player.setLooping(true); //set looping
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "onInactiveByCronJob " + data);
        AppPreferences.setAdminMsg(null);
        AppPreferences.setAvailableStatus(false);
        mBus.post(Keys.INACTIVE_PUSH);
        Notifications.generateAdminNotification(mContext, data);
        stopMusicPlayer();
        stopSelf();

    }


    /**
     * This method validates the inactive push notification's data
     *
     * @param data Push Notification data
     */
    private synchronized void onInactivePushReceived(final OfflineNotificationData data) {
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Inactive Push Received");
        if (AppPreferences.isLoggedIn() && (AppPreferences.getAvailableStatus() || AppPreferences.isOutOfFence()) && Utils.isInactiveCheckRequired()) {
            Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Driver is Logged In");
            AppPreferences.setInactiveCheckTime(System.currentTimeMillis());
            /*
             * Check Coordinates when there's any delay in FCM Push Notification and ignore
             * this notification when there are different coordinates.
             * */
            if (StringUtils.isNotBlank(data.getLat()) && StringUtils.isNotBlank(data.getLng())
                    && data.getLat().equalsIgnoreCase(AppPreferences.getLastUpdatedLatitude())
                    && data.getLng().equalsIgnoreCase(AppPreferences.getLastUpdatedLongitude()) && !isCountDownTimerRunning) {
                Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Valid Data");
                if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable()) {
                    //If we don't get response of location update in 15 sec, then we'll consider driver is in inactive state
                    Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Valid Connection and GPS is active");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            countDownTimer.cancel();
                            isCountDownTimerRunning = true;
                            countDownTimer.start();
                            WebIO.getInstance().clearConnectionData();
                            new UserRepository().requestLocationUpdate(mContext, new UserDataHandler() {
                                @Override
                                public void onLocationUpdate(LocationResponse response) {
                                    countDownTimer.cancel();
                                    isCountDownTimerRunning = false;
                                    ActivityStackManager.getInstance().restartLocationService(mContext);
                                    stopSelf();
                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {
//                                            countDownTimer.cancel();
                                    onLocationUpdateError(errorCode, errorMessage, data);
                                    stopSelf();
                                }
                            }, AppPreferences.getLatitude(), AppPreferences.getLongitude());
                        }
                    });
                } else {
                    Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "onInactiveByCronJob | GPS = " + Utils.isGpsEnable() + " Internet = " + Connectivity.isConnectedFast(mContext));
                    onInactiveByCronJob(data.getMessage());
                }
            } else {
                Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "FCM Ignored. Location Already Updated via update-lat-lng API or API is being called.");
                stopSelf();
            }
        } else {
            String logMsg = "FCM Ignored. login = " + AppPreferences.isLoggedIn() + "active = " + AppPreferences.getAvailableStatus() + " outOfFense = " + AppPreferences.isOutOfFence() + " InactiveCheckRequired = " + Utils.isInactiveCheckRequired();
            Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, logMsg);
            stopSelf();
        }
    }


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
            Intent locationIntent = new Intent(Keys.UNAUTHORIZED_BROADCAST);
            sendBroadcast(locationIntent);
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
            mBus.post(Keys.INACTIVE_FENCE);
        } /*else {
            onInactiveByCronJob(data.getMessage());
        }*/
    }

    @Override
    public void onDestroy() {
        stopSelf();
        stopMusicPlayer();
        super.onDestroy();
    }

    /***
     * Stop music player tone.
     */
    private void stopMusicPlayer() {
        if (player != null) {
            player.stop();
            player.release();
        }

    }
}
