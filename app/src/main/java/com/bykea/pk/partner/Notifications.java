package com.bykea.pk.partner;


import android.app.ActivityManager;
import android.app.Notification;
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
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Patterns;

import com.bykea.pk.partner.models.ReceivedMessage;
import com.bykea.pk.partner.ui.activities.ChatActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public static void removeAllNotifications(Context context) {
        try {
            NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
        // Check for empty push message
        if (StringUtils.isBlank(message))
            return;


        // notification icon
        final int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + R.raw.notification_sound);

        if (!StringUtils.isBlank(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon,
                                       String title, String message, String timeStamp,
                                       PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_BIG_IMAGE, notification);
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

    /*public static void createChatNotification(Context context, ReceivedMessage receivedMessage) {

        if (receivedMessage.getData().getMessage().contains(".wav")) {
            receivedMessage.getData().setMessage("Voice Message");
        }

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.chat_alert_sound);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.myAppName))
                .setContentText("" + receivedMessage.getData().getMessage())
                .setSound(soundUri).setAutoCancel(true);

        Intent targetIntent = new Intent(context, ChatActivity.class);
        targetIntent.putExtra(Tags.CHAT_CONVERSATION_ID, receivedMessage.getData().getConversationId());
        targetIntent.putExtra("chat", true);

        PendingIntent contentIntent = PendingIntent       //  06-03-2015 14:45 oclock
                .getActivity(context, 0,
                        targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        nManager.notify(Constants.CHAT_NOTIFICATION_ID, builder.build());
    }*/

    public static void createNotification(Context context, String message, int id) {

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.notification_sound);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
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

    public static void createCancelNotification(Context context, String message, int id) {

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.notification_sound);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setContentTitle(Constants.APP_NAME)
                .setContentText("" + message)
                .setSound(soundUri).setAutoCancel(true);

        Intent targetIntent = new Intent(context, HomeActivity.class);
        targetIntent.putExtra("isCancelledTrip", true);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent
                .getActivity(context, 0,
                        targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        nManager.notify(id, builder.build());
    }


    public static void generateAdminNotification(Context context, String msg) {


        Intent intent = new Intent();
        intent.setClass(context, HomeActivity.class);
        intent.putExtra("isCancelledTrip", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.putExtra("adminMsg", msg);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.notification_sound);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
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

        if (receivedMessage.getData().getMessage().contains(".wav")) {
            receivedMessage.getData().setMessage("Voice Message");
        }

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://"
                + context.getPackageName() + "/"
                + R.raw.notification_sound);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setContentTitle(Constants.BYKEA)
                .setContentText("" + receivedMessage.getData().getMessage())
                .setSound(soundUri).setAutoCancel(true);

        Intent targetIntent = new Intent(context, ChatActivity.class);
        targetIntent.putExtra(Keys.CHAT_CONVERSATION_ID, receivedMessage.getData().getConversationId());
        targetIntent.putExtra("chat", true);

        PendingIntent contentIntent = PendingIntent       //  06-03-2015 14:45 oclock
                .getActivity(context, 0,
                        targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        nManager.notify(0, builder.build());
    }

}
