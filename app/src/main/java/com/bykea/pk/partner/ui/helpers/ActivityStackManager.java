package com.bykea.pk.partner.ui.helpers;

import android.app.Activity;
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
import com.bykea.pk.partner.ui.activities.ChatActivityNew;
import com.bykea.pk.partner.ui.activities.ConfirmDropOffAddressActivity;
import com.bykea.pk.partner.ui.activities.FeedbackActivity;
import com.bykea.pk.partner.ui.activities.HistoryCancelDetailsActivity;
import com.bykea.pk.partner.ui.activities.HistoryDetailActivity;
import com.bykea.pk.partner.ui.activities.HistoryMissedCallsActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.activities.BookingActivity;
import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.bykea.pk.partner.ui.activities.PaymentRequestActivity;
import com.bykea.pk.partner.ui.activities.PostProblemActivity;
import com.bykea.pk.partner.ui.activities.ProblemActivity;
import com.bykea.pk.partner.ui.activities.ReportActivity;
import com.bykea.pk.partner.ui.activities.ReportPostActivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;

import top.oply.opuslib.OpusService;


public class ActivityStackManager {
    private static final ActivityStackManager mActivityStack = new ActivityStackManager();

    private ActivityStackManager() {
    }

    private static void setContext() {
    }

    public static ActivityStackManager getInstance() {
        return mActivityStack;
    }

    public void startLoginActivity(Context mContext) {
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    public void startHomeActivity(boolean firstTime, Context mContext) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        if (firstTime) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        mContext.startActivity(intent);
    }

    public void startHomeActivityFromCancelTrip(boolean isCanceledByAdmin, String cancelMsg, Context mContext) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.putExtra("isCancelledTrip", true);
        intent.putExtra("isCanceledByAdmin", isCanceledByAdmin);
        intent.putExtra("cancelMsg", cancelMsg);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }

    public void startJobActivity(Context mContext) {
        Intent intent = new Intent(mContext, BookingActivity.class);
        mContext.startActivity(intent);
    }

    public void startFeedbackActivity(Context mContext) {
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        mContext.startActivity(intent);
    }

    public void startFeedbackFromResume(Context mContext) {
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        mContext.startActivity(intent);
    }

    public void startLocationService(Context mContext) {
        if (!Utils.isServiceRunning(mContext, LocationService.class)) {
            mContext.startService(new Intent(mContext, LocationService.class));
        }
    }

    public void stopLocationService(Context mContext) {
        if (Utils.isServiceRunning(mContext, LocationService.class)) {
            mContext.stopService(new Intent(mContext, LocationService.class));
        }
    }

    public void restartLocationService(Context mContext) {
        stopLocationService(mContext);
        startLocationService(mContext);
    }

    public void restartLocationService(Context mContext, String STATUS) {
        stopLocationService(mContext);
        if (!Utils.isServiceRunning(mContext, LocationService.class)) {
            Intent intent = new Intent(mContext, LocationService.class);
            intent.putExtra(Constants.Extras.LOCATION_SERVICE_STATUS, STATUS);
            mContext.startService(intent);
        }
    }

    public void startCallingActivity(NormalCallData callData, boolean isFromGcm, Context mContext) {
        if (AppPreferences.getAvailableStatus()
                && Utils.isGpsEnable(mContext)
                && AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_FREE)
                && Utils.isNotDelayed(callData.getData().getSentTime())) {

            AppPreferences.setCallData(callData.getData());
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

    public void startChatActivity(String title, String refId, boolean isChatEnable, Context mContext) {
        Utils.redLog(Constants.APP_NAME + " CONVERSATION ID = ", refId);
        Intent intent = new Intent(mContext, ChatActivityNew.class);
        intent.putExtra(Keys.CHAT_CONVERSATION_ID, refId);
        intent.putExtra("chat", isChatEnable);
        intent.putExtra("title", title);
        mContext.startActivity(intent);
    }

    public void startRequestPaymentActivity(Context mContext) {
        Intent intent = new Intent(mContext, PaymentRequestActivity.class);
        mContext.startActivity(intent);
    }

    public void startMissedCallsActivity(Context mContext) {
        Intent intent = new Intent(mContext, HistoryMissedCallsActivity.class);
        mContext.startActivity(intent);
    }

    public void startCancelDetailsActivity(TripHistoryData data, Context mContext) {
        Intent intent = new Intent(mContext, HistoryCancelDetailsActivity.class);
        intent.putExtra(Constants.Extras.TRIP_DETAILS, data);
        mContext.startActivity(intent);
    }

    public void startLauncherActivity(Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(mContext.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
        mContext.startActivity(mainIntent);
    }

    public void startCompletedDetailsActivity(TripHistoryData historyData, Context mContext) {
        Intent intent = new Intent(mContext, HistoryDetailActivity.class);
        intent.putExtra(Constants.Extras.TRIP_DETAILS, historyData);
        mContext.startActivity(intent);
    }

    public void startConfirmDestActivity(Activity mContext, String toolBarTitle, String searchBoxTitle) {
        Intent returndropoffIntent = new Intent(mContext, ConfirmDropOffAddressActivity.class);
        returndropoffIntent.putExtra("from", Constants.CONFIRM_DROPOFF_REQUEST_CODE);
        returndropoffIntent.putExtra(Constants.TOOLBAR_TITLE, toolBarTitle);
        returndropoffIntent.putExtra(Constants.SEARCHBOX_TITLE, toolBarTitle);
        mContext.startActivityForResult(returndropoffIntent, Constants.CONFIRM_DROPOFF_REQUEST_CODE);
    }

    public void startProblemPostActivity(Context context, String tripId, String reason) {
        Intent intent = new Intent(context, PostProblemActivity.class);
        intent.putExtra("TRIP_ID", tripId);
        intent.putExtra("REASON", reason);
        context.startActivity(intent);
    }

    public void startProblemActivity(Context context, String tripNo) {
        Intent intent = new Intent(context, ProblemActivity.class);
        intent.putExtra("TRIP_ID", tripNo);
        context.startActivity(intent);
    }

    public void startReportActivity(Context mContext) {
        Intent intent = new Intent(mContext, ReportActivity.class);
        mContext.startActivity(intent);
    }

    public void startReportPostActivity(String reason, Context mContext) {
        Intent intent = new Intent(mContext, ReportPostActivity.class);
        intent.putExtra("reason", reason);
        mContext.startActivity(intent);
    }


    public void startOpusService(Context mContext) {
        if (!Utils.isServiceRunning(mContext, OpusService.class)) {
            Intent intent = new Intent(mContext, OpusService.class);
            mContext.startService(intent);
            Utils.redLog("StackManager", "OpusService Starts from " + mContext.getClass().getSimpleName());
        }
    }

    public void stopOpusService(Context mContext) {
        if (Utils.isServiceRunning(mContext, OpusService.class)) {
            mContext.stopService(new Intent(mContext, OpusService.class));
            Utils.redLog("StackManager", "OpusService Stop");
        }
    }

}
