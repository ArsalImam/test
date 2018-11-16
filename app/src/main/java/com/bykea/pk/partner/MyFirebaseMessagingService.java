package com.bykea.pk.partner;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

import com.bykea.pk.partner.communication.socket.WebIO;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.models.data.OfflineNotificationData;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.ChatActivityNew;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import okhttp3.internal.Util;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Messaging Service";
    private Notifications notifications;
    private EventBus mBus = EventBus.getDefault();
    private MyFirebaseMessagingService mContext;
    private boolean isCountDownTimerRunning;
    final CountDownTimer countDownTimer = new CountDownTimer(15000, 15000) {
        @Override
        public void onTick(long l) {
        }

        @Override
        public void onFinish() {
            isCountDownTimerRunning = false;
            onInactiveByCronJob("App Offline Hochuke Hain!");//TODO update msg
        }
    };

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) {
            return;
        }
        mContext = this;
        Gson gson = new Gson();
        //if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.

        if (!AppPreferences.isLoggedIn()) {
            return;
        }


        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null && remoteMessage.getData().get("event") != null) {
            Utils.redLog(TAG, "NOTIFICATION DATA (FCM) : " + remoteMessage.getData().toString());
            if (remoteMessage.getData().get("event").equalsIgnoreCase("1")) {
                NormalCallData callData = gson.fromJson(remoteMessage.getData().get("data"), NormalCallData.class);
                if (StringUtils.isNotBlank(callData.getStatus())) {
                    if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_CANCEL_TRIP)) {
                        if (Utils.isGpsEnable() || AppPreferences.isOnTrip()) {
                            Utils.redLog(Constants.APP_NAME, " CANCEL CALLING FCM");
                            Intent intent = new Intent(Keys.BROADCAST_CANCEL_BY_ADMIN);
                            intent.putExtra("action", Keys.BROADCAST_CANCEL_BY_ADMIN);
                            intent.putExtra("msg", callData.getMessage());
                            Utils.setCallIncomingState();
                            if (AppPreferences.isJobActivityOnForeground() ||
                                    AppPreferences.isCallingActivityOnForeground()) {
//                                mContext.sendBroadcast(intent);
                                EventBus.getDefault().post(intent);
                            } else {
                                EventBus.getDefault().post(intent);
                                Notifications.createCancelNotification(mContext, callData.getMessage(), 23);
                            }
                        }
                    } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_COMPLETED_TRIP) && AppPreferences.getAvailableStatus()) {

                        /*
                        * when Gps is off, we don't show Calling Screen so we don't need to show
                        * Cancel notification either if passenger cancels it before booking.
                        * If passenger has cancelled it after booking we will entertain this Cancel notification
                        * */

                        if (Utils.isGpsEnable() || AppPreferences.isOnTrip()) {
                            Intent intent = new Intent(Keys.BROADCAST_COMPLETE_BY_ADMIN);
                            intent.putExtra("action", Keys.BROADCAST_COMPLETE_BY_ADMIN);
                            intent.putExtra("msg", callData.getMessage());
                            if (AppPreferences.isJobActivityOnForeground()) {
                                EventBus.getDefault().post(intent);
//                            mContext.sendBroadcast(intent);
                            } else {
                                Utils.setCallIncomingState();
                                Notifications.createNotification(mContext, callData.getMessage(), 23);
                            }
                        }
                    } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_CALLING)) {
                        ActivityStackManager.getInstance().startCallingActivity(callData, true, mContext);
                    } else {
                        Utils.updateTripData(callData);
                    }
                }
            } else if (remoteMessage.getData().get("event").equalsIgnoreCase("2")) {
                if (AppPreferences.isOnTrip()) {
                    Intent chatIntent = new Intent(DriverApp.getContext(), ChatActivityNew.class);
                    chatIntent.putExtra("chat", true);
                    chatIntent.putExtra("fromNotification", true);
                    chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    chatIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(chatIntent);
                    WebIORequestHandler.getInstance().registerChatListener();
                }
            } else if (remoteMessage.getData().get("event").equalsIgnoreCase("7")) {//when server inactive any driver via Cron job
                onInactivePushReceived(remoteMessage, gson);
            } else if ((remoteMessage.getData().get("event").equalsIgnoreCase("3"))) {
                if (AppPreferences.isLoggedIn()) {
                    NotificationData adminNotification = gson.fromJson(remoteMessage.getData().get("data"), NotificationData.class);
                    AppPreferences.setAdminMsg(adminNotification);
                    Notifications.generateAdminNotification(this, adminNotification.getMessage());
                    mBus.post(Constants.ON_NEW_NOTIFICATION);
                }
            } else if ((remoteMessage.getData().get("event").equalsIgnoreCase("4"))) {
                if (AppPreferences.isLoggedIn()) {
                    NotificationData adminNotification = gson.fromJson(remoteMessage.getData().get("data"), NotificationData.class);
                    AppPreferences.setAdminMsg(adminNotification);
                    Notifications.generateAdminNotification(this, adminNotification.getMessage());
                    if (AppPreferences.getAvailableStatus() != adminNotification.isActive()
                            && !AppPreferences.isOutOfFence() && AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_FREE)) {
                        AppPreferences.setAvailableStatus(adminNotification.isActive());
                        AppPreferences.setWalletAmountIncreased(!adminNotification.isActive());
                        mBus.post("INACTIVE-PUSH");
                    }
                    mBus.post(Constants.ON_NEW_NOTIFICATION);
                }
            }
        }
    }

    private synchronized void onInactivePushReceived(RemoteMessage remoteMessage, Gson gson) {
        if (AppPreferences.isLoggedIn() && (AppPreferences.getAvailableStatus() || AppPreferences.isOutOfFence()) && Utils.isInactiveCheckRequired()) {
            AppPreferences.setInactiveCheckTime(System.currentTimeMillis());
            final OfflineNotificationData data = gson.fromJson(remoteMessage.getData().get("data"), OfflineNotificationData.class);
            /*
            * Check Coordinates when there's any delay in FCM Push Notification and ignore
            * this notification when there are different coordinates.
            * */
            if (StringUtils.isNotBlank(data.getLat()) && StringUtils.isNotBlank(data.getLng())
                    && data.getLat().equalsIgnoreCase(AppPreferences.getLastUpdatedLatitude())
                    && data.getLng().equalsIgnoreCase(AppPreferences.getLastUpdatedLongitude()) && !isCountDownTimerRunning) {

                if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable()) {
                    //If we don't get response of location update in 15 sec, then we'll consider driver is in inactive state

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
                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {
//                                            countDownTimer.cancel();
                                    onLocationUpdateError(errorCode, errorMessage, data);
                                }
                            }, AppPreferences.getLatitude(), AppPreferences.getLongitude());
                        }
                    });
                } else {
                    onInactiveByCronJob(data.getMessage());
                }
            }
        }
    }


    private void onLocationUpdateError(int errorCode, String errorMessage, OfflineNotificationData data) {
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

    private void onInactiveByCronJob(String data) {
        AppPreferences.setAdminMsg(null);
        AppPreferences.setAvailableStatus(false);
        mBus.post(Keys.INACTIVE_PUSH);
        Notifications.generateAdminNotification(mContext, data);
    }
}
