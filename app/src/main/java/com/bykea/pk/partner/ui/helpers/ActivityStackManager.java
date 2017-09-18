package com.bykea.pk.partner.ui.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.services.LocationService;
import com.bykea.pk.partner.ui.activities.CallingActivity;
import com.bykea.pk.partner.ui.activities.ChatActivity;
import com.bykea.pk.partner.ui.activities.FeedbackActivity;
import com.bykea.pk.partner.ui.activities.HistoryCancelDetailsActivity;
import com.bykea.pk.partner.ui.activities.HistoryMissedCallsActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.activities.JobActivity;
import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.bykea.pk.partner.ui.activities.PaymentRequestActivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.Utils;


public class ActivityStackManager {
    public static int activities = 0;
    private static Context mContext;
    private static final ActivityStackManager mActivityStack = new ActivityStackManager();

    private ActivityStackManager() {
    }

    public static ActivityStackManager getInstance(Context context) {
        mContext = context;
        return mActivityStack;
    }

    public void startLoginActivity() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        ((Activity) mContext).startActivity(intent);
    }

    public void startHomeActivity(boolean firstTime) {
        activities = 1;
        Intent intent = new Intent(mContext, HomeActivity.class);
        if (firstTime) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        ((Activity) mContext).startActivity(intent);
    }

    public void startHomeActivityFromCancelTrip(boolean isCanceledByAdmin) {
        activities = 1;
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.putExtra("isCancelledTrip", true);
        intent.putExtra("isCanceledByAdmin", isCanceledByAdmin);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ((Activity) mContext).startActivity(intent);
    }

    public void startJobActivity() {
        activities = 1;
        Intent intent = new Intent(mContext, JobActivity.class);
        ((Activity) mContext).startActivity(intent);
    }

    public void startFeedbackActivity() {
        activities = 1;
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        ((Activity) mContext).startActivity(intent);
    }

    public void startFeedbackFromResume() {
        activities = 1;
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        ((Activity) mContext).startActivity(intent);
    }

    public void startLocationService() {
        if (!Utils.isServiceRunning(mContext, LocationService.class)) {
            mContext.startService(new Intent(mContext, LocationService.class));
        }
    }

    public void stopLocationService() {
        AppPreferences.setStopService(mContext, true);
        if (Utils.isServiceRunning(mContext, LocationService.class)) {
            mContext.stopService(new Intent(mContext, LocationService.class));
        }
    }

    public void restartLocationService() {
        if (Utils.isServiceRunning(mContext, LocationService.class)) {
            mContext.stopService(new Intent(mContext, LocationService.class));
        }
        startLocationService();
    }

    public void startCallingActivity() {
        activities = 1;
        Intent intent = new Intent(mContext, CallingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        mContext.startActivity(intent);
    }

    public void stopLocationService(String activityName) {
      /*  activities-=2;
        Utils.redLog(activityName + " DESTROYED : ", activities + "");
        Utils.redLog("TOTAL REMAINING ACTIVITIES : ", activities + "");

        if (activities <= 0)
            mContext.stopService(new Intent(mContext, LocationService.class));*/

    }

    public void startChatActivity(String title, String refId, boolean isChatEnable) {
        activities = 1;
        Utils.redLog(Constants.APP_NAME + " CONVERSATION ID = ", refId);
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(Keys.CHAT_CONVERSATION_ID, refId);
        intent.putExtra("chat", isChatEnable);
        intent.putExtra("title", title);
        mContext.startActivity(intent);
    }

    public void startRequestPaymentActivity() {
        Intent intent = new Intent(mContext, PaymentRequestActivity.class);
        mContext.startActivity(intent);
    }

    public void startMissedCallsActivity() {
        Intent intent = new Intent(mContext, HistoryMissedCallsActivity.class);
        mContext.startActivity(intent);
    }

    public void startCancelDetailsActivity(TripHistoryData data) {
        Intent intent = new Intent(mContext, HistoryCancelDetailsActivity.class);
        intent.putExtra("TRIP_DETAILS", data);
        mContext.startActivity(intent);
    }
}
