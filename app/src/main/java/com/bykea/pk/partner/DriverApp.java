package com.bykea.pk.partner;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.bykea.pk.partner.communication.socket.WebIO;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.dagger2.component.BasicComponent;
import com.bykea.pk.partner.dagger2.component.DaggerBasicComponent;
import com.bykea.pk.partner.dagger2.module.SharedPrefModule;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.FileUtil;
import com.bykea.pk.partner.utils.Utils;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.facebook.appevents.AppEventsLogger;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.File;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DriverApp extends MultiDexApplication {

    private static DriverApp mContext;
    private BasicComponent mBasicComponent;
    private Emitter.Listener mJobCallListener = new WebIORequestHandler.JobCallListener();
    /**
     * XLog Printer global object which would be used for writing logs on file.
     */
    public static Printer globalFilePrinter;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (com.squareup.leakcanary.LeakCanaryLeakCanary.isInAnalyzerProcess(this)) {
        // This process is dedicated to LeakCanary for heap analysis.
        // You should not init your app in this process.
//            return;
//        }
//        com.squareup.leakcanary.LeakCanaryLeakCanary.install(this);
        if (mContext == null) {
            mContext = this;
        }
        AppEventsLogger.activateApp(this);

        mBasicComponent = DaggerBasicComponent.builder()
                .sharedPrefModule(new SharedPrefModule())
                .build();

        if (AppPreferences.isLoggedIn() && (AppPreferences.getAvailableStatus() || AppPreferences.isOutOfFence()))
            ActivityStackManager.getInstance().startLocationService(mContext);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.InAppAlert)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationReceivedHandler(new OneSignalNotificationReceivedHandler())
                .setNotificationOpenedHandler(new OneSignalNotificationOpenedHandler())
                .filterOtherGCMReceivers(true)
                .init();
        setupLoggerConfigurations();
    }

    /***
     * Setup logging configurations where all logs are stored date wise and app cache folder
     */
    private void setupLoggerConfigurations() {

        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                        : LogLevel.NONE)
                .tag(getString(R.string.global_tag))
//                .t()    // Enable thread info, disabled by default
//                .st(2) // Enable stack trace info with depth 2, disabled by default
                .build();

        // Printer that print the log using com.elvishew.xlog.XLog.Log
        Printer androidPrinter = new AndroidPrinter();
        // Printer that print the log to the file system

        File logRootFolder = FileUtil.createRootDirectoryForLogs(this);
        Printer filePrinter = new FilePrinter
                .Builder(logRootFolder.getPath())       // Specify the path to save log file
                .fileNameGenerator(new DateFileNameGenerator()) // saves each log file according to DateFileName
                .backupStrategy(new FileSizeBackupStrategy(Constants.LogTags.LOG_FILE_MAX_SIZE))
                .logFlattener(new ClassicFlattener())
                .build();


        //Initialize XLog and with provided configuration and Android Printer
        XLog.init(config, androidPrinter, filePrinter);

        // For future usage: partial usage in MainActivity.
        globalFilePrinter = filePrinter;

    }

    public static DriverApp getApplication() {
        return mContext;
    }

    public BasicComponent getBasicComponent() {
        return mBasicComponent;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (mContext == null) {
            mContext = this;
        }
    }

    private Emitter.Listener connectionListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            WebIO.getInstance().off(Socket.EVENT_CONNECT, this);
            Utils.redLog(Constants.APP_NAME + "  ########################    ", "Socket Connection Established....");
            attachListenersOnSocketConnected();

        }
    };

    public void attachListenersOnSocketConnected() {
        EventBus.getDefault().post(Constants.ON_SOCKET_CONNECTED);
        WebIO.getInstance().on(ApiTags.SOCKET_PASSENGER_CALL, mJobCallListener);
        if (AppPreferences.isOnTrip()) {
            WebIORequestHandler.getInstance().registerChatListener();
        }
    }

    public void connect() {
        try {
            if (!WebIO.getInstance().isSocketConnected() && AppPreferences.isLoggedIn()) {
                WebIO.getInstance().onConnect(connectionListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect(String trackingFrom) {
        WebIO.getInstance().off(ApiTags.SOCKET_PASSENGER_CALL, mJobCallListener);
        WebIO.getInstance().getSocket().disconnect();
        WebIO.getInstance().getSocket().close();
    }

    public static Context getContext() {
        return mContext.getApplicationContext();
    }

    public static void startLocationService(Context context) {
        if (AppPreferences.isLoggedIn() && (AppPreferences.getAvailableStatus() || AppPreferences.isOutOfFence()))
            ActivityStackManager.getInstance().startLocationService(mContext);
    }


    private class OneSignalNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            ActivityStackManager.getInstance().startLauncherActivity(mContext);
        }
    }


    private class OneSignalNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            if (AppPreferences.isLoggedIn()) {
                NotificationData notificationData = new NotificationData();
                notificationData.setTitle(notification.payload.title);
                notificationData.setMessage(notification.payload.body);
                if (StringUtils.isNotBlank(notification.payload.launchURL)) {
                    notificationData.setLaunchUrl(notification.payload.launchURL);
                }
                if (StringUtils.isNotBlank(notification.payload.bigPicture)) {
                    notificationData.setImageLink(notification.payload.bigPicture);
                }

                //show action button with value from notification (if any)
                if (notification.payload.additionalData != null) {
                    if (notification.payload.additionalData.has("showBtn")) {
                        try {
                            notificationData.setShowActionButton((String) notification.payload.additionalData.get("showBtn"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (notification.payload.additionalData.has("type")) {
                        try {
                            notificationData.setType((String) notification.payload.additionalData.get("type"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                AppPreferences.setAdminMsg(notificationData);
                if (notification.isAppInFocus) {
                    EventBus.getDefault().post(Constants.ON_NEW_NOTIFICATION);
                }
            }
        }
    }
}
