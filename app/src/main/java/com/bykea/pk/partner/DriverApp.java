package com.bykea.pk.partner;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.bykea.pk.partner.communication.socket.WebIO;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.Instabug;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.onesignal.OneSignalDbHelper;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class DriverApp extends MultiDexApplication {

    private static DriverApp mContext;
    public static boolean isCallListenerAttached = false;
    private Emitter.Listener mJobCallListener = new WebIORequestHandler.JobCallListener();


    @Override
    public void onCreate() {
        super.onCreate();
        if (mContext == null) {
            mContext = this;
        }
        isCallListenerAttached = false;
        if (AppPreferences.isLoggedIn(mContext) && (AppPreferences.getAvailableStatus(mContext) || AppPreferences.isOutOfFence(mContext)))
            ActivityStackManager.getInstance(mContext).startLocationService();

        new Instabug.Builder(this, BuildConfig.DEBUG ? Constants.INSTA_BUG_BETA_KEY : Constants.INSTA_BUG_LIVE_KEY)
                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                .setShakingThreshold(470)
                .build();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.InAppAlert)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationReceivedHandler(new OneSignalNotificationReceivedHandler())
                .setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler())
                .filterOtherGCMReceivers(true)
                .init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (mContext == null) {
            mContext = this;
        }
    }

    public Emitter.Listener connectionListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Utils.redLog(Constants.APP_NAME + "  ########################    ", "Socket Connection Established....");
            WebIO.getInstance().on(ApiTags.SOCKET_PASSENGER_CALL, mJobCallListener);
            isCallListenerAttached = true;
        }
    };

    public void connect(String from) {
        Utils.redLog(Constants.APP_NAME + "  connect method called    ", from);
        try {
            WebIO.getInstance().onConnect(connectionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect(String trackingFrom) {
        WebIO.getInstance().off(ApiTags.SOCKET_PASSENGER_CALL, mJobCallListener);
        WebIO.getInstance().getSocket().disconnect();
        WebIO.getInstance().getSocket().close();
        isCallListenerAttached = false;
    }

    public static Context getContext() {
        return mContext.getApplicationContext();
    }

    public static void startLocationService(Context context) {
        if (AppPreferences.isLoggedIn(context) && (AppPreferences.getAvailableStatus(mContext) || AppPreferences.isOutOfFence(mContext)))
            ActivityStackManager.getInstance(context).startLocationService();
    }


    private class OneSignalNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            ActivityStackManager.getInstance(mContext).startLauncherActivity();
        }
    }


    private class OneSignalNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            if (AppPreferences.isLoggedIn(mContext)) {
                NotificationData notificationData = new NotificationData();
                notificationData.setTitle(notification.payload.title);
                notificationData.setMessage(notification.payload.body);
                if (StringUtils.isNotBlank(notification.payload.launchURL)) {
                    notificationData.setLaunchUrl(notification.payload.launchURL);
                }
                if (StringUtils.isNotBlank(notification.payload.bigPicture)) {
                    notificationData.setImageLink(notification.payload.bigPicture);
                }
                AppPreferences.setAdminMsg(mContext, notificationData);
                if (notification.isAppInFocus) {
                    EventBus.getDefault().post(Constants.ON_NEW_NOTIFICATION);
                }
            }
        }
    }
}
