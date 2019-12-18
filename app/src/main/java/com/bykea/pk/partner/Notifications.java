package com.bykea.pk.partner;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.bykea.pk.partner.models.ReceivedMessage;
import com.bykea.pk.partner.models.ReceivedMessageCount;
import com.bykea.pk.partner.ui.activities.BookingActivity;
import com.bykea.pk.partner.ui.activities.ChatActivityNew;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.bykea.pk.partner.utils.Constants.ANDROID_RESOURCE_URI;
import static com.bykea.pk.partner.utils.Constants.DIGIT_ZERO;
import static com.bykea.pk.partner.utils.Constants.SEPERATOR;
import static com.bykea.pk.partner.utils.Constants.TRANSALATION_SEPERATOR;

public class Notifications {

    private static String TAG = Notifications.class.getSimpleName();
    // notifications id's
    public static int NOTIFICATION_ID = 9021;
    public static int NOTIFICATION_ID_BIG_IMAGE = 8021;

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    private Context mContext;

    public Notifications(Context mContext) {
        this.mContext = mContext;
    }

    public static void removeAllNotifications(Context context) {
        try {
            NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + R.raw.notification_sound);
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void createNotification(Context context, String message, int id) {

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.notification_sound);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, Utils.getChannelID())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setContentTitle(Constants.APP_NAME)
                .setContentText("" + message)
                .setSound(soundUri).setAutoCancel(true);

        Intent targetIntent = new Intent(context, HomeActivity.class);

        PendingIntent contentIntent = PendingIntent
                .getActivity(context, 0,
                        targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        nManager.notify(id, builder.build());
    }

    public static void createCancelNotification(Context context, String message) {

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.one);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, Utils.getChannelIDForCancelNotifications())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setContentTitle(Constants.APP_NAME)
                .setContentText("" + message)
                .setSound(soundUri).setAutoCancel(true);

        Intent targetIntent = new Intent(context, HomeActivity.class);
        targetIntent.putExtra(Constants.Extras.IS_CANCELED_TRIP, true);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent
                .getActivity(context, 0,
                        targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        nManager.notify(0, builder.build());
    }


    public static void generateAdminNotification(Context context, String msg) {


        Intent intent = new Intent();
        intent.setClass(context, HomeActivity.class);
        intent.putExtra(Constants.Extras.IS_CANCELED_TRIP, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.putExtra("adminMsg", msg);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.notification_sound);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, Utils.getChannelID())
                        .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                        .setContentTitle("Notification")
                        .setContentText(msg)
                        .setSound(soundUri)
                        .setContentIntent(pIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg));

        builder.setAutoCancel(true);
        builder.setTicker(msg);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setLights(Color.YELLOW, 3000, 3000);
        int notificationId = "admin".hashCode();
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(notificationId, builder.build());
    }


    public static void createChatNotification(Context context, ReceivedMessage receivedMessage) {

        //Check for Multiple Delivery if the chat send by multi delivery
        // passenger do not create notification
        if (StringUtils.isNotBlank(receivedMessage.getData().getBatchID())) return;

        if (receivedMessage.getData().getMessage().contains(".wav")) {
            receivedMessage.getData().setMessage("Voice Message");
        }

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.notification_sound);

        NotificationCompat.Builder builder = null;
        String newLineEscape = "<br>";
        if (receivedMessage.getData().getMessage().contains(TRANSALATION_SEPERATOR)) {
            try {
                String messageToDisplay = StringUtils.EMPTY;
                String[] strings = receivedMessage.getData().getMessage().split(TRANSALATION_SEPERATOR);

                if (strings.length == 2 && strings[DIGIT_ZERO] != null && strings[Constants.DIGIT_ONE] != null &&
                        StringUtils.isNotEmpty(strings[DIGIT_ZERO]) && StringUtils.isNotEmpty(strings[Constants.DIGIT_ONE])) {
                    messageToDisplay = messageToDisplay.concat(strings[DIGIT_ZERO]);
                    messageToDisplay = messageToDisplay.concat(newLineEscape);
                    messageToDisplay = messageToDisplay.concat(strings[Constants.DIGIT_ONE]);
                    messageToDisplay = Utils.getTextFromHTML(messageToDisplay);
                    receivedMessage.getData().setMessage(messageToDisplay);
                } else if (strings.length <= 2 && StringUtils.isNotEmpty(strings[DIGIT_ZERO])) {
                    messageToDisplay = messageToDisplay.concat(strings[DIGIT_ZERO]);
                    receivedMessage.getData().setMessage(messageToDisplay);
                } else if (strings.length <= 2 && StringUtils.isNotEmpty(strings[Constants.DIGIT_ONE])) {
                    messageToDisplay = messageToDisplay.concat(strings[Constants.DIGIT_ONE]);
                    receivedMessage.getData().setMessage(messageToDisplay);
                }

                builder = createNotificationForMessage(context, receivedMessage, soundUri);
            } catch (Exception e) {
                builder = createNotificationForMessage(context, receivedMessage, soundUri);
            }
        } else {
            builder = createNotificationForMessage(context, receivedMessage, soundUri);
        }

        ReceivedMessageCount receivedMessageAppPref = AppPreferences.getReceivedMessageCount();
        if (DriverApp.isChatActivityVisible()) {
            if (receivedMessageAppPref != null)
                AppPreferences.removeReceivedMessageCount();
        } else {
            ReceivedMessageCount receivedMessageCount = null;
            if (receivedMessageAppPref == null) {
                receivedMessageCount = new ReceivedMessageCount(receivedMessage.getData().getTripId(), Constants.DIGIT_ONE);
                AppPreferences.setReceivedMessageCount(receivedMessageCount);
                context.sendBroadcast(new Intent(Constants.Broadcast.CHAT_MESSAGE_RECEIVED));
            } else {
                if (!StringUtils.isEmpty(receivedMessageAppPref.getTripId()) && !StringUtils.isEmpty(receivedMessage.getData().getTripId())
                        && receivedMessageAppPref.getTripId().equals(receivedMessage.getData().getTripId())) {
                    if (receivedMessage.getData().getStatus().equalsIgnoreCase("unread")) {
                        receivedMessageCount
                                = new ReceivedMessageCount(receivedMessage.getData().getTripId(),
                                receivedMessageAppPref.getConversationMessageCount() + Constants.DIGIT_ONE);
                        AppPreferences.setReceivedMessageCount(receivedMessageCount);
                        context.sendBroadcast(new Intent(Constants.Broadcast.CHAT_MESSAGE_RECEIVED));
                    }
                } else if (!StringUtils.isEmpty(receivedMessageAppPref.getTripId()) && !StringUtils.isEmpty(receivedMessage.getData().getTripId())
                        && !receivedMessageAppPref.getTripId().equals(receivedMessage.getData().getTripId())) {
                    receivedMessageCount = new ReceivedMessageCount(receivedMessage.getData().getTripId(), Constants.DIGIT_ONE);
                    AppPreferences.setReceivedMessageCount(receivedMessageCount);
                    context.sendBroadcast(new Intent(Constants.Broadcast.CHAT_MESSAGE_RECEIVED));
                }
            }
        }

        Intent targetIntent = new Intent(context, ChatActivityNew.class);
        targetIntent.putExtra(Keys.CHAT_CONVERSATION_ID, receivedMessage.getData().getConversationId());
        targetIntent.putExtra("chat", true);

        PendingIntent contentIntent = PendingIntent       //  06-03-2015 14:45 oclock
                .getActivity(context, 0,
                        targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        nManager.notify(0, builder.build());
    }

    /**
     * Create Drop Off Update Notification
     */
    public static void createDropOffUpdateNotification() {
        NotificationManager nManager = (NotificationManager) DriverApp.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        ReceivedMessage receivedMessage = new ReceivedMessage();
        receivedMessage.setMessage(DriverApp.getContext().getString(R.string.drop_off_update_by_passenger));
        receivedMessage.setData(receivedMessage);

        Uri soundUri = Uri.parse(ANDROID_RESOURCE_URI
                + DriverApp.getContext().getPackageName() + SEPERATOR
                + R.raw.notification_sound);
        NotificationCompat.Builder builder;
        builder = createNotificationForMessage(DriverApp.getContext(), receivedMessage, soundUri);

        Intent targetIntent = new Intent(DriverApp.getContext(), BookingActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(DriverApp.getContext(), DIGIT_ZERO, targetIntent, DIGIT_ZERO);
        builder.setContentIntent(contentIntent);
        nManager.notify(DIGIT_ZERO, builder.build());
    }

    /**
     * Use To Create Notification When Chat Message Is Received
     *
     * @param context         : Calling Context
     * @param receivedMessage : Notifcation Received Message Object
     * @param soundUri        : Sound Uri To Play
     * @return
     */
    private static NotificationCompat.Builder createNotificationForMessage(Context context, ReceivedMessage receivedMessage, Uri soundUri) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, Utils.getChannelID())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setContentTitle(Constants.BYKEA)
                .setContentText("" + receivedMessage.getData().getMessage())
                .setSound(soundUri).setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("" + receivedMessage.getData().getMessage()));
        return builder;
    }
}
