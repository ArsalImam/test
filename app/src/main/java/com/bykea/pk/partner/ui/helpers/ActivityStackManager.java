package com.bykea.pk.partner.ui.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.IntentCompat;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.services.LocationService;
import com.bykea.pk.partner.ui.activities.CallingActivity;
import com.bykea.pk.partner.ui.activities.ChatActivity;
import com.bykea.pk.partner.ui.activities.FeedbackActivity;
import com.bykea.pk.partner.ui.activities.HistoryCancelDetailsActivity;
import com.bykea.pk.partner.ui.activities.HistoryDetailActivity;
import com.bykea.pk.partner.ui.activities.HistoryMissedCallsActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.activities.JobActivity;
import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.bykea.pk.partner.ui.activities.PaymentRequestActivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;


public class ActivityStackManager {
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
        mContext.startActivity(intent);
    }

    public void startHomeActivity(boolean firstTime) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        if (firstTime) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        mContext.startActivity(intent);
    }

    public void startHomeActivityFromCancelTrip(boolean isCanceledByAdmin) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.putExtra("isCancelledTrip", true);
        intent.putExtra("isCanceledByAdmin", isCanceledByAdmin);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }

    public void startJobActivity() {
        Intent intent = new Intent(mContext, JobActivity.class);
        mContext.startActivity(intent);
    }

    public void startFeedbackActivity() {
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        mContext.startActivity(intent);
    }

    public void startFeedbackFromResume() {
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        mContext.startActivity(intent);
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

    public void startCallingActivity(NormalCallData callData, boolean isFromGcm) {
        if (AppPreferences.getAvailableStatus(mContext) && Utils.isGpsEnable(mContext)
                && AppPreferences.getTripStatus(mContext).equalsIgnoreCase(TripStatus.ON_FREE)
                && Utils.isNotDelayed(mContext, callData.getData().getSentTime())) {
            AppPreferences.setCallData(mContext, callData.getData());
            Intent callIntent = new Intent(DriverApp.getContext(), CallingActivity.class);
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            callIntent.setAction(Intent.ACTION_MAIN);
            callIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (isFromGcm) {
                callIntent.putExtra("isGcm", true);
            }
            mContext.startActivity(callIntent);
        }
    }

    public void startChatActivity(String title, String refId, boolean isChatEnable) {
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
        intent.putExtra(Constants.Extras.TRIP_DETAILS, data);
        mContext.startActivity(intent);
    }

    public void startLauncherActivity() {
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(mContext.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
        mContext.startActivity(mainIntent);
    }

    public void startCompletedDetailsActivity(TripHistoryData historyData) {
        Intent intent = new Intent(mContext, HistoryDetailActivity.class);
        intent.putExtra(Constants.Extras.TRIP_DETAILS, historyData);
        mContext.startActivity(intent);
    }
}
