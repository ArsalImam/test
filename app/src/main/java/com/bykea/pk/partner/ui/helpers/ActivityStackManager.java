package com.bykea.pk.partner.ui.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.models.data.BankData;
import com.bykea.pk.partner.models.data.DeliveryScheduleModel;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.OfflineNotificationData;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.TripHistoryData;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.services.HandleInactivePushService;
import com.bykea.pk.partner.services.LocationService;
import com.bykea.pk.partner.ui.activities.BanksDetailsActivity;
import com.bykea.pk.partner.ui.activities.BookingActivity;
import com.bykea.pk.partner.ui.activities.CallingActivity;
import com.bykea.pk.partner.ui.activities.ChatActivityNew;
import com.bykea.pk.partner.ui.activities.DeliveryScheduleDetailActivity;
import com.bykea.pk.partner.ui.activities.FeedbackActivity;
import com.bykea.pk.partner.ui.activities.ForgotPasswordActivity;
import com.bykea.pk.partner.ui.activities.HistoryCancelDetailsActivity;
import com.bykea.pk.partner.ui.activities.HistoryDetailActivity;
import com.bykea.pk.partner.ui.activities.HistoryMissedCallsActivity;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.activities.LandingActivity;
import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.bykea.pk.partner.ui.activities.MapDetailsActivity;
import com.bykea.pk.partner.ui.activities.MultiDeliveryCallingActivity;
import com.bykea.pk.partner.ui.activities.MultiDeliveryFeedbackActivity;
import com.bykea.pk.partner.ui.activities.MultipleDeliveryBookingActivity;
import com.bykea.pk.partner.ui.activities.NumberVerificationActivity;
import com.bykea.pk.partner.ui.activities.PaymentRequestActivity;
import com.bykea.pk.partner.ui.activities.PostProblemActivity;
import com.bykea.pk.partner.ui.activities.ProblemActivity;
import com.bykea.pk.partner.ui.activities.RankingActivity;
import com.bykea.pk.partner.ui.activities.RegistrationActivity;
import com.bykea.pk.partner.ui.activities.ReportActivity;
import com.bykea.pk.partner.ui.activities.ReportPostActivity;
import com.bykea.pk.partner.ui.activities.SavePlaceActivity;
import com.bykea.pk.partner.ui.activities.ShahkarActivity;
import com.bykea.pk.partner.ui.loadboard.detail.LoadboardDetailActivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;


public class ActivityStackManager {
    private static final ActivityStackManager mActivityStack = new ActivityStackManager();

    private ActivityStackManager() {
    }

    private static void setContext() {
    }

    public static ActivityStackManager getInstance() {
        return mActivityStack;
    }


    /***
     * Open Number verification screen i.e. OTP
     *
     * @param context Calling context
     */
    public void startPhoneNumberVerificationActivity(Context context) {
        Intent intent = new Intent(context, NumberVerificationActivity.class);
        context.startActivity(intent);
    }

    /***
     *  Open Login screen and clear all other activities
     *
     * @param context Calling context
     */
    public void startLoginActivityNoFlag(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /***
     * Open splash screen and clear all activities from task.
     * @param context Calling context.
     */
    public void startLandingActivity(Context context) {
        Intent intent = new Intent(context, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Start Login Activity and clear whole stack.
     *
     * @param context Calling context.
     */
    public void startLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /***
     * Start login Activity
     * @param mContext Calling context.
     * @param clearWholeTask should whole stack need to be cleared before opening login screen.
     */
    public void startLoginActivity(Context mContext, boolean clearWholeTask) {
        Intent intent = new Intent(mContext, LoginActivity.class);
        if (clearWholeTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        mContext.startActivity(intent);
    }

    public void startHomeActivity(boolean firstTime, Context mContext) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        if (firstTime) {
            intent.putExtra("isLogin", "yes");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        mContext.startActivity(intent);
    }

    /***
     * Start Home screen for Inactive push for user interaction
     * @param mContext Calling context.
     */
    public void startHomeActvityForInActivePush(Context mContext) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        mContext.startActivity(intent);
    }

    /**
     * clears activity stack before starting HomeActivity (if activity is already running it will not launch new instance)
     * HomeFragment will be loaded from onNewIntent method of HomeActivity
     *
     * @param context calling activity
     */
    public void startHomeActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.Extras.NAVIGATE_TO_HOME_SCREEN, true);
        context.startActivity(intent);
    }

    /**
     * This method starts home activity with cancel extras that indicates we need to show cancel notification
     */
    public void startHomeActivityFromCancelTrip(boolean isCanceledByAdmin, Context mContext) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.putExtra(Constants.Extras.IS_CANCELED_TRIP, true);
        intent.putExtra(Constants.Extras.IS_CANCELED_TRIP_BY_ADMIN, isCanceledByAdmin);
//        intent.putExtra("cancelMsg", cancelMsg);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }

    public void startJobActivity(Context mContext) {
        Intent intent = new Intent(mContext, BookingActivity.class);
        mContext.startActivity(intent);
    }

    /***
     * Start Map Details Activity.
     *
     * @param mContext an activity context holding the reference of an activity.
     * @param type a type of a fragment you want to open in an activity.
     */
    public void startMapDetailsActivity(Context mContext, String type) {
        Intent intent = new Intent(mContext, MapDetailsActivity.class);
        intent.putExtra(Keys.FRAGMENT_TYPE_NAME, type);
        mContext.startActivity(intent);
    }

    /***
     * Start MultiDelivery Booking activity using activity context
     * @param mContext hold the reference of an activity.
     */
    public void startMultiDeliveryBookingActivity(Context mContext) {
        Intent intent = new Intent(mContext, MultipleDeliveryBookingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public void startFeedbackActivity(Context mContext) {
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * Start multi delivery feedback activity.
     *
     * @param mContext Holding the reference of an activity.
     * @param isComingFromOnGoingRide Is user coming from on going ride.
     * @param tripID Current Trip id.
     */
    public void startMultiDeliveryFeedbackActivity(Context mContext, String tripID,boolean isComingFromOnGoingRide) {
        Intent intent = new Intent(mContext, MultiDeliveryFeedbackActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Keys.MULTIDELIVERY_TRIP_ID, tripID);
        bundle.putBoolean(Keys.MULTIDELIVERY_FEEDBACK_SCREEN, isComingFromOnGoingRide);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    public void startFeedbackFromResume(Context mContext) {
        Intent intent = new Intent(mContext, FeedbackActivity.class);
        mContext.startActivity(intent);
    }

    public void startLocationService(Context mContext) {
        if (!Utils.isServiceRunning(mContext, LocationService.class)) {
            Intent intent = new Intent(mContext, LocationService.class);
            intent.setAction(Constants.Actions.STARTFOREGROUND_ACTION);
            startService(mContext, intent);

        }
    }

    /*
     * This method check for Android version of device and starts location service as foreground
     * service when OS is greater or equal to Android O
     */
    private void startService(Context mContext, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(intent);
        } else {
            mContext.startService(intent);
        }
    }

    /**
     * This method stops Location Service.
     *
     * @param context Calling Context
     * @see Constants.Actions#STOPFOREGROUND_ACTION
     */
    public synchronized void stopLocationService(Context context) {
        if (Utils.isServiceRunning(context, LocationService.class)) {
            Intent intent = new Intent(context, LocationService.class);
            intent.setAction(Constants.Actions.STOPFOREGROUND_ACTION);
            startService(context, intent);
        }
    }

    /**
     * This method restarts location service by first stopping the service if it is already running
     * and then calling startLocationService method to start Location service. Handler added to fix
     * issue for android 8.0 and above where notification gets removed when we try to start service
     * immediately after stopping it.
     *
     * @param context Calling Context
     */
    public void restartLocationService(final Context context) {
        /*stopLocationService(context);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startLocationService(context);
            }
        }, Constants.RESTART_LOCATION_SERVICE_DELAY);*/
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(Utils.isServiceRunning(context, LocationService.class) ?
                Constants.Actions.UPDATE_FOREGROUND_NOTIFICATION : Constants.Actions.STARTFOREGROUND_ACTION);
        startService(context, intent);
    }

    /**
     * This method restarts location service with custom interval when partner is on trip
     * @param context Calling Context
     * @param updateInterval interval in millis
     */
    public void restartLocationServiceWithCustomIntervals(final Context context, long updateInterval) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(Utils.isServiceRunning(context, LocationService.class) ?
                Constants.Actions.UPDATE_FOREGROUND_NOTIFICATION : Constants.Actions.STARTFOREGROUND_ACTION);
        intent.putExtra(Constants.Extras.ON_TRIP_LOCATION_UPDATE_CUSTOM_INTERVAL, updateInterval);
        startService(context, intent);
    }

    public void restartLocationService(Context mContext, String STATUS) {
        stopLocationService(mContext);
        if (!Utils.isServiceRunning(mContext, LocationService.class)) {
            Intent intent = new Intent(mContext, LocationService.class);
            intent.setAction(Constants.Actions.STARTFOREGROUND_ACTION);
            intent.putExtra(Constants.Extras.LOCATION_SERVICE_STATUS, STATUS);
            startService(mContext, intent);
        }
    }

    public void startCallingActivity(NormalCallData callData, boolean isFromGcm, Context mContext) {

        Utils.redLog("Calling Activity", "Status Available: " + AppPreferences.getAvailableStatus() +
                "GPS status: " + Utils.isGpsEnable() + " Trip Status (Free): " + AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_FREE) +
                " Should display screen (if request difference is not delayed): " + Utils.isNotDelayed(callData.getData().getSentTime()));
        if (AppPreferences.getAvailableStatus()
                //&& !AppPreferences.isAvailableStatusAPICalling()
                && Utils.isGpsEnable()
                && AppPreferences.getTripStatus().equalsIgnoreCase(TripStatus.ON_FREE)
                && Utils.isNotDelayed(callData.getData().getSentTime())) {

            AppPreferences.setCallData(callData.getData());
            Intent callIntent = new Intent(DriverApp.getContext(), CallingActivity.class);
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            callIntent.setAction(Intent.ACTION_MAIN);
            callIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (isFromGcm) {
                callIntent.putExtra("isGcm", true);
                Utils.redLog("Calling Activity", "On Call FCM opening Calling Activity");
            }
            mContext.startActivity(callIntent);
        }
    }

    /**
     * Start multi delivery calling activity.
     *
     * @param response The {@link MultiDeliveryCallDriverData} object.
     * @param isFromGcm boolean indicating that start activity from GCM or not.
     * @param mContext Holding the reference of an activity.
     */
    public void startMultiDeliveryCallingActivity(MultiDeliveryCallDriverData response,
                                                  boolean isFromGcm,
                                                  Context mContext) {

        if (AppPreferences.getAvailableStatus() && Utils.isGpsEnable()) {
            AppPreferences.setMultiDeliveryCallDriverData(response);
            Intent callIntent = new Intent(DriverApp.getContext(),
                    MultiDeliveryCallingActivity.class);
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            callIntent.setAction(Intent.ACTION_MAIN);
            callIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (isFromGcm) {
                callIntent.putExtra(Constants.IS_FROM_GCM, true);
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

    public void startShahkarActivity(Context mContext) {
        Intent intent = new Intent(mContext, ShahkarActivity.class);
        mContext.startActivity(intent);
    }

    public void startStatsActivity(Context mContext) {
        Intent intent = new Intent(mContext, RankingActivity.class);
        mContext.startActivity(intent);
    }

    public void startLauncherActivity(Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(mContext.getPackageName());
        ComponentName componentName = null;
        if (intent != null) {
            componentName = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
            mContext.startActivity(mainIntent);
        }
    }

    public void startCompletedDetailsActivity(TripHistoryData historyData, Context mContext) {
        Intent intent = new Intent(mContext, HistoryDetailActivity.class);
        intent.putExtra(Constants.Extras.TRIP_DETAILS, historyData);
        mContext.startActivity(intent);
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

    public void startReportActivity(Context mContext, String cTtype) {
        Intent intent = new Intent(mContext, ReportActivity.class);
        intent.putExtra(Constants.Extras.CONTACT_TYPE, cTtype);
        mContext.startActivity(intent);
    }

    /**
     * This method will start details activity for Scheduled Delivery Service
     *
     * @param mContext             Calling context
     * @param deliveryScheduleData DeliveryScheduleModel Selected delivery trip
     */
    public void startDeliveryScheduleDetailActivity(Context mContext, DeliveryScheduleModel deliveryScheduleData) {
        Intent intent = new Intent(mContext, DeliveryScheduleDetailActivity.class);
        intent.putExtra(Constants.Extras.SELECTED_ITEM, deliveryScheduleData);
        mContext.startActivity(intent);
    }

    public void startReportPostActivity(Context mContext, String reason, String contactType) {
        Intent intent = new Intent(mContext, ReportPostActivity.class);
        intent.putExtra("reason", reason);
        intent.putExtra(Constants.Extras.CONTACT_TYPE, contactType);
        mContext.startActivity(intent);
    }

    public void startForgotPasswordActivity(Context mContext) {
        Intent intent = new Intent(mContext, ForgotPasswordActivity.class);
        mContext.startActivity(intent);
    }


    public void startSavePlaceActivity(Context context, PlacesResult placesResult) {
        Intent intent = new Intent(context, SavePlaceActivity.class);
        intent.putExtra(Constants.Extras.SELECTED_ITEM, placesResult);
        context.startActivity(intent);
    }

    public void startBankDetailsActivity(Context context, BankData data) {
        Intent intent = new Intent(context, BanksDetailsActivity.class);
        intent.putExtra(Constants.Extras.SELECTED_ITEM, data);
        context.startActivity(intent);
    }

    public void startRegisterationActiivty(Context context) {
        Intent intent = new Intent(context, RegistrationActivity.class);
//        Intent intent = new Intent(context, JsBankFingerSelectionActivity.class);
        context.startActivity(intent);
    }

    /**
     * open loadboard booking screen
     * @param context Context
     * @param bookingId selected booking id
     */
    public void startLoadboardBookingDetailActiivty(Context context, Long bookingId) {
        Intent intent = new Intent(context, LoadboardDetailActivity.class);
        intent.putExtra(LoadboardDetailActivity.BOOKING_ID, bookingId);
        context.startActivity(intent);
    }


    /**
     * This method starts a service to handle Inactive Push Notification
     *
     * @param data    Notifiation data
     * @param context Calling context
     */
    public void startHandleInactivePushService(Context context, OfflineNotificationData data) {
        if (!Utils.isServiceRunning(context, HandleInactivePushService.class)) {
            Intent intent = new Intent(context, HandleInactivePushService.class);
            intent.putExtra(Constants.Extras.INACTIVE_PUSH_DATA, data);
            context.startService(intent);
        }
    }

}
