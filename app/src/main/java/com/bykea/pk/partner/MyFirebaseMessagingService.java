package com.bykea.pk.partner;

import android.content.Intent;

import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.models.data.OfflineNotificationData;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.ui.activities.CallingActivity;
import com.bykea.pk.partner.ui.activities.ChatActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Messaging Service";
    private Notifications notifications;
    private EventBus mBus = EventBus.getDefault();
    private MyFirebaseMessagingService mContext;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) {
            return;
        }
        mContext = this;
        Gson gson = new Gson();
        //if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Utils.redLog(TAG, "Notification Received : " + "--------------------------------------------------------------");
        Utils.redLog(TAG, "From: " + remoteMessage.getFrom());

        if (!AppPreferences.isLoggedIn(this) || remoteMessage == null)
            return;


        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null && remoteMessage.getData().get("event") != null) {
            Utils.redLog(TAG, "NOTIFICATION DATA : " + remoteMessage.getData().toString());
            if (remoteMessage.getData().get("event").equalsIgnoreCase("1")) {
                NormalCallData callData = gson.fromJson(remoteMessage.getData().get("data"), NormalCallData.class);
                if (StringUtils.isNotBlank(callData.getStatus()) &&
                        callData.getStatus().equalsIgnoreCase(TripStatus.ON_CANCEL_TRIP)) {
                    if (Utils.isGpsEnable(mContext) || AppPreferences.isOnTrip(mContext)) {
                        Utils.redLog(Constants.APP_NAME, " CANCEL CALLING FCM");
                        Intent intent = new Intent(Keys.BROADCAST_CANCEL_RIDE);
//                    if (remoteMessage.getData().get("cancel_by").equalsIgnoreCase("admin"))
                        intent.putExtra("action", Keys.BROADCAST_CANCEL_BY_ADMIN);
//                    else
//                        intent.putExtra("action", Keys.BROADCAST_CANCEL_RIDE);
                        intent.putExtra("msg", callData.getMessage());
                        if (AppPreferences.isJobActivityOnForeground(mContext) ||
                                AppPreferences.isCallingActivityOnForeground(mContext)) {
                            mContext.sendBroadcast(intent);
                        } else {
                            Utils.setCallIncomingState(this);
                            Notifications.createCancelNotification(mContext, callData.getMessage(), 23);
                        }
                    }
                } else if (StringUtils.isNotBlank(callData.getStatus()) &&
                        callData.getStatus().equalsIgnoreCase(TripStatus.ON_COMPLETED_TRIP)) {
                    Intent intent = new Intent(Keys.BROADCAST_COMPLETE_BY_ADMIN);
                    intent.putExtra("action", Keys.BROADCAST_COMPLETE_BY_ADMIN);
                    intent.putExtra("msg", callData.getMessage());
                    if (AppPreferences.isJobActivityOnForeground(mContext)) {
                        mContext.sendBroadcast(intent);
                    } else {
                        Utils.setCallIncomingState(this);
                        Notifications.createNotification(mContext, callData.getMessage(), 23);
                    }
                } else {
                    if (AppPreferences.getAvailableStatus(this) && Utils.isGpsEnable(this)
                            && Utils.isNotDelayed(this, callData.getData().getSentTime())) {
                        AppPreferences.setCallData(getApplicationContext(), callData.getData());
                        Intent callIntent = new Intent(DriverApp.getContext(), CallingActivity.class);
                        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        callIntent.setAction(Intent.ACTION_MAIN);
                        callIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        callIntent.putExtra("isGcm", true);
                        startActivity(callIntent);
                        ActivityStackManager.activities = 1;
                    }
                }
            } else if (remoteMessage.getData().get("event").equalsIgnoreCase("2")) {
                if (AppPreferences.isOnTrip(getApplicationContext())) {
                    Intent chatIntent = new Intent(DriverApp.getContext(), ChatActivity.class);
                    chatIntent.putExtra("chat", true);
                    chatIntent.putExtra("fromNotification", true);
                    chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    chatIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(chatIntent);
                    WebIORequestHandler.getInstance().registerChatListener();
                    ActivityStackManager.activities = 1;
                }
            } else if (remoteMessage.getData().get("event").equalsIgnoreCase("7")) {//when server inactive any driver via Cron job
                if (AppPreferences.isLoggedIn(getApplicationContext())) {
                    OfflineNotificationData data = gson.fromJson(remoteMessage.getData().get("data"), OfflineNotificationData.class);
                    /*
                    * Check Coordinates when there's any delay in FCM Push Notification and ignore
                    * this notification when there are different coordinates.
                    * */
                    if (StringUtils.isNotBlank(data.getLat()) && StringUtils.isNotBlank(data.getLng())
                            && data.getLat().equalsIgnoreCase(AppPreferences.getLastUpdatedLatitude(getApplicationContext()))
                            && data.getLng().equalsIgnoreCase(AppPreferences.getLastUpdatedLongitude(getApplicationContext()))) {
                        AppPreferences.setAdminMsg(this, StringUtils.EMPTY);
                        AppPreferences.setAvailableStatus(getApplicationContext(), false);
                        mBus.post("INACTIVE-PUSH");
                        Notifications.generateAdminNotification(this, data.getMessage());
                    }
                }
            } else if ((remoteMessage.getData().get("event").equalsIgnoreCase("3"))) {
                if (AppPreferences.isLoggedIn(this)) {
                    CommonResponse adminNotification = gson.fromJson(remoteMessage.getData().get("data"), CommonResponse.class);
                    AppPreferences.setAdminMsg(this, adminNotification.getMessage());
                    Notifications.generateAdminNotification(this, adminNotification.getMessage());
                    mBus.post(Constants.ON_NEW_NOTIFICATION);
                }
            } else if ((remoteMessage.getData().get("event").equalsIgnoreCase("4"))) {
                if (AppPreferences.isLoggedIn(this)) {
                    NotificationData adminNotification = gson.fromJson(remoteMessage.getData().get("data"), NotificationData.class);
                    AppPreferences.setAdminMsg(this, adminNotification.getMessage());
                    Notifications.generateAdminNotification(this, adminNotification.getMessage());
                    if (AppPreferences.getAvailableStatus(this)
                            != adminNotification.isActive()) {
                        AppPreferences.setAvailableStatus(getApplicationContext(), adminNotification.isActive());
                        mBus.post("INACTIVE-PUSH");
                    }
                    mBus.post(Constants.ON_NEW_NOTIFICATION);
                }
            }
        }


    }
}
