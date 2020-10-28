package com.bykea.pk.partner;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bykea.pk.partner.communication.socket.WebIO;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.dal.source.JobsDataSource;
import com.bykea.pk.partner.dal.source.JobsRepository;
import com.bykea.pk.partner.dal.source.socket.payload.JobCall;
import com.bykea.pk.partner.dal.source.socket.payload.JobCallPayload;
import com.bykea.pk.partner.dal.util.Injection;
import com.bykea.pk.partner.models.ReceivedMessage;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.NotificationData;
import com.bykea.pk.partner.models.data.OfflineNotificationData;
import com.bykea.pk.partner.models.response.BookingAcceptedResponse;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryCallDriverAcknowledgeResponse;
import com.bykea.pk.partner.models.response.MultipleDeliveryCallDriverResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TelloTalkManager;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.net.HttpURLConnection;

import static com.bykea.pk.partner.utils.ApiTags.BATCH_UPDATED;
import static com.bykea.pk.partner.utils.ApiTags.BOOKING_REQUEST;
import static com.bykea.pk.partner.utils.ApiTags.BOOKING_UPDATED_DROP_OFF;
import static com.bykea.pk.partner.utils.ApiTags.MULTI_DELIVERY_SOCKET_TRIP_MISSED;
import static com.bykea.pk.partner.utils.ApiTags.SOCKET_NEW_JOB_CALL;
import static com.bykea.pk.partner.utils.Constants.CallType.SINGLE;
import static com.bykea.pk.partner.utils.Constants.FCM_EVENTS_MULTIDELIVER_CANCEL_BY_ADMIN;
import static com.bykea.pk.partner.utils.Constants.FCM_EVENTS_MULTIDELIVER_INCOMING_CALL;
import static com.bykea.pk.partner.utils.Constants.Notification.DATA_TYPE_TELLO_VAL;
import static com.bykea.pk.partner.utils.Constants.Notification.CONTENT_AVAILABLE_CHANNEL;

//import com.bykea.pk.partner.utils.Constants.FCMEvents;


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

        if (remoteMessage.getData() != null) {
            String value = remoteMessage.getData().get(Constants.Notification.DATA_TYPE_TELLO);
            if (value != null) {
                if (value.equalsIgnoreCase(DATA_TYPE_TELLO_VAL)) {
                    TelloTalkManager.instance().onMessageReceived();
                } else if (value.equalsIgnoreCase(CONTENT_AVAILABLE_CHANNEL)) {
                    TelloTalkManager.instance().onMessageReceived(remoteMessage.getData());
                }
            }
        } else if (remoteMessage.getData() != null && remoteMessage.getData()
                .get(Constants.Notification.EVENT_TYPE) != null) {
            Utils.redLog(TAG, "NOTIFICATION DATA (FCM) : " + remoteMessage.getData().toString());
            if (remoteMessage.getData().get(Constants.Notification.EVENT_TYPE).equalsIgnoreCase("1")) {
                NormalCallData callData = gson.fromJson(remoteMessage.getData()
                        .get(Constants.Notification.DATA_TYPE), NormalCallData.class);

                setUIForStatus(callData);

            } else if (remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase("2")) {
                ReceivedMessage receivedMessage = gson.fromJson(remoteMessage.getData().
                        get(Constants.Notification.DATA_TYPE), ReceivedMessage.class);
                if (AppPreferences.isOnTrip()) {
                    //Do not open Chat activity when the trip is of Batch i.e Multi Delivery Trip
                    if (StringUtils.isNotBlank(receivedMessage.getData().getBatchID()))
                        return;

                    if (!AppPreferences.isChatActivityOnForeground()) {
                        Notifications.createChatNotification(DriverApp.getContext(), receivedMessage);
                    }
                    Intent intent = new Intent(Keys.BROADCAST_MESSAGE_RECEIVE);
                    intent.putExtra("action", Keys.BROADCAST_MESSAGE_RECEIVE);
                    intent.putExtra("msg", receivedMessage);
                    EventBus.getDefault().post(intent);
                    AppPreferences.setLastMessageID(receivedMessage.getData().getMessageId());
                    WebIORequestHandler.getInstance().registerChatListener();
                }
            } else if (remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase("7")) {//when server inactive any driver via Cron job
                OfflineNotificationData data = gson.fromJson(remoteMessage.getData().get(Constants.Notification.DATA_TYPE), OfflineNotificationData.class);
                ActivityStackManager.getInstance().startHandleInactivePushService(mContext, data);
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase("3"))) {
                if (AppPreferences.isLoggedIn()) {
                    NotificationData adminNotification = gson.fromJson(remoteMessage.getData().get(Constants.Notification.DATA_TYPE), NotificationData.class);
                    AppPreferences.setAdminMsg(adminNotification);
                    Notifications.generateAdminNotification(this, adminNotification.getMessage());
                    mBus.post(Constants.ON_NEW_NOTIFICATION);
                }
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase("4"))) {
                if (AppPreferences.isLoggedIn()) {
                    NotificationData adminNotification = gson.fromJson(remoteMessage.getData()
                            .get(Constants.Notification.DATA_TYPE), NotificationData.class);
                    AppPreferences.setAdminMsg(adminNotification);
                    Notifications.generateAdminNotification(this, adminNotification.getMessage());
                    if (AppPreferences.getAvailableStatus() != adminNotification.isActive()
                            && !AppPreferences.isOutOfFence() && AppPreferences.getTripStatus()
                            .equalsIgnoreCase(TripStatus.ON_FREE)) {
                        AppPreferences.setAvailableStatus(adminNotification.isActive());
                        AppPreferences.setWalletAmountIncreased(!adminNotification.isActive());
                        mBus.post("INACTIVE-PUSH");
                    }
                    mBus.post(Constants.ON_NEW_NOTIFICATION);
                }
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE).equalsIgnoreCase(BOOKING_REQUEST))) {
                JobCallPayload payload = gson.fromJson(remoteMessage.getData().get(Constants.Notification.DATA_TYPE), JobCallPayload.class);
                JobCall jobCall = payload.getTrip();
                jobCall.setType(payload.getType());
                if (payload.getTrip().getDispatch() != null && payload.getTrip().getDispatch()) {
                    Utils.appToastDebug("Job Dispatch FCM Received");
                    ActivityStackManager.getInstance().startCallingActivity(jobCall, true, mContext);
                } else if (AppPreferences.getAvailableStatus() && payload.getType().equalsIgnoreCase(SINGLE)) {
                    JobsRepository jobsRepo = Injection.INSTANCE.provideJobsRepository(getApplication().getApplicationContext());
                    jobsRepo.ackJobCall(jobCall.getTrip_id(), new JobsDataSource.AckJobCallCallback() {
                        @Override
                        public void onJobCallAcknowledged() {
                            Utils.appToastDebug("Job Call Acknowledged");
                            Log.d(TAG, "Push Notification Received");
                            Utils.redLog(TAG, "On Call FCM");
                            ActivityStackManager.getInstance().startCallingActivity(jobCall, true, mContext);
                        }

                        @Override
                        public void onJobCallAcknowledgeFailed() {
                            Utils.appToastDebug("Job Call Acknowledgement Failed");
                        }
                    });
                }
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase(FCM_EVENTS_MULTIDELIVER_INCOMING_CALL))) { //Multi delivery call
                MultipleDeliveryCallDriverResponse response = gson.fromJson(
                        remoteMessage.getData().get(Constants.Notification.DATA_TYPE),
                        MultipleDeliveryCallDriverResponse.class);
                MultiDeliveryCallDriverData data = response.getData();
                if (data != null && AppPreferences.getAvailableStatus()) {
                    if (data.getBatchID() != null) {
                        AppPreferences.setMultiDeliveryCallDriverData(data);
                        new UserRepository().requestDriverAcknowledged(handler);
                    }
                }
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase(FCM_EVENTS_MULTIDELIVER_CANCEL_BY_ADMIN))) { //Multi delivery cancel by admin
                mBus.post(Keys.MULTIDELIVERY_CANCELLED_BY_ADMIN);
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase(SOCKET_NEW_JOB_CALL))) {
                try {
                    BookingAcceptedResponse response = gson.fromJson(remoteMessage.getData().get(Constants.Notification.DATA_TYPE), BookingAcceptedResponse.class);
                    if (response.isSuccess() && !AppPreferences.isJobActivityOnForeground()) {
                        ActivityStackManager.getInstance().startJobActivity(DriverApp.getContext(), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase(MULTI_DELIVERY_SOCKET_TRIP_MISSED))) {
                EventBus.getDefault().post(Keys.MULTIDELIVERY_MISSED_EVENT);
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase(BOOKING_UPDATED_DROP_OFF))) {
                if (AppPreferences.isJobActivityOnForeground()) {
                    Intent intent = new Intent(Keys.BROADCAST_DROP_OFF_UPDATED);
                    intent.putExtra("action", Keys.BROADCAST_DROP_OFF_UPDATED);
                    EventBus.getDefault().post(intent);
                } else {
                    AppPreferences.setDropOffUpdateRequired(true);
                    Notifications.createDropOffUpdateNotification();
                }
            } else if ((remoteMessage.getData().get(Constants.Notification.EVENT_TYPE)
                    .equalsIgnoreCase(BATCH_UPDATED))) {
                if (AppPreferences.isJobActivityOnForeground()) {
                    Intent intent = new Intent(Keys.BROADCAST_BATCH_UPDATED);
                    intent.putExtra(Constants.ACTION, Keys.BROADCAST_BATCH_UPDATED);
                    EventBus.getDefault().post(intent);

                    Utils.appToast(getString(R.string.batch_update_by_passenger));
                } else {
                    Notifications.createBatchUpdateNotification();
                }
            }
        }
    }

    /**
     * Method Is Listening To Both The Notification
     * 1. Call From Trip Notifcation - 7
     * 2. MultiDelivery Trip Notifcation - 23
     *
     * @param callData : Object Class
     */
    private void setUIForStatus(NormalCallData callData) {
        if (StringUtils.isNotBlank(callData.getStatus())) {
            if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_CANCEL_TRIP)) {
                if (Utils.isGpsEnable() || AppPreferences.isOnTrip()) {
                    AppPreferences.removeReceivedMessageCount();
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
                        Notifications.createCancelNotification(mContext, callData.getMessage());
                    }
                }
            } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_BATCH_BOOKING_CANCELLED)) {
                if (Utils.isGpsEnable() || AppPreferences.isOnTrip()) {
                    AppPreferences.removeReceivedMessageCount();
                    Utils.redLog(Constants.APP_NAME, " CANCEL CALLING FCM");
                    Intent intent = new Intent(Keys.BROADCAST_CANCEL_BATCH);
                    intent.putExtra("action", Keys.BROADCAST_CANCEL_BATCH);
                    intent.putExtra("msg", callData.getMessage());
                    Utils.setCallIncomingState();
                    if (AppPreferences.isJobActivityOnForeground() ||
                            AppPreferences.isCallingActivityOnForeground()) {
//                                mContext.sendBroadcast(intent);
                        EventBus.getDefault().post(intent);
                    } else {
                        EventBus.getDefault().post(intent);
                        Notifications.createCancelNotification(mContext, callData.getMessage());
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
            } else if (callData.getStatus().equalsIgnoreCase(TripStatus.ON_CALLING) ||
                    callData.getStatus().equalsIgnoreCase(TripStatus.ON_CALLING_OPEN) ||
                    callData.getStatus().equalsIgnoreCase(TripStatus.ON_CALLING_SEARCHING)) {
                Log.d(TAG, "Push Notification Received: With Status: " + callData.getStatus());
                Utils.redLog(TAG, "On Call FCM");
                ActivityStackManager.getInstance().startCallingActivity(callData, true, mContext);
            } else {
                Utils.updateTripData(callData);
            }
        }
    }

    private synchronized void onInactivePushReceived(RemoteMessage remoteMessage, Gson gson) {
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Inactive Push Received");
        if (AppPreferences.isLoggedIn() && (AppPreferences.getAvailableStatus() || AppPreferences.isOutOfFence()) && Utils.isInactiveCheckRequired()) {
            Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Driver is Logged In");
            AppPreferences.setInactiveCheckTime(System.currentTimeMillis());
            final OfflineNotificationData data = gson.fromJson(remoteMessage.getData().get(Constants.Notification.DATA_TYPE), OfflineNotificationData.class);
            /*
             * Check Coordinates when there's any delay in FCM Push Notification and ignore
             * this notification when there are different coordinates.
             * */
            if (StringUtils.isNotBlank(data.getLat()) && StringUtils.isNotBlank(data.getLng())
                    && data.getLat().equalsIgnoreCase(AppPreferences.getLastUpdatedLatitude())
                    && data.getLng().equalsIgnoreCase(AppPreferences.getLastUpdatedLongitude()) && !isCountDownTimerRunning) {
                Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Valid Data");
                if (Connectivity.isConnectedFast(mContext) && Utils.isGpsEnable()) {
                    //If we don't get response of location update in 15 sec, then we'll consider driver is in inactive state
                    Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Valid Connection and GPS is active");
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
                                    if (response.isSuccess()) {
                                        ActivityStackManager.getInstance().restartLocationService(mContext);
                                    } else {
                                        handleLocationErrorUseCase(response);
                                    }
                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {
                                    onLocationUpdateError(errorCode, errorMessage, data);
                                }
                            }, AppPreferences.getLatitude(), AppPreferences.getLongitude());
                        }
                    });
                } else {
                    Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "onInactiveByCronJob | GPS = " + Utils.isGpsEnable() + " Internet = " + Connectivity.isConnectedFast(mContext));
                    onInactiveByCronJob(data.getMessage());
                }
            } else {
                Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "FCM Ignored. Location Already Updated via update-lat-lng API or API is being called.");
            }
        } else {
            String logMsg = "FCM Ignored. login = " + AppPreferences.isLoggedIn() + "active = " + AppPreferences.getAvailableStatus() + " outOfFense = " + AppPreferences.isOutOfFence() + " InactiveCheckRequired = " + Utils.isInactiveCheckRequired();
            Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, logMsg);
        }
    }


    private void onLocationUpdateError(int errorCode, String errorMessage, OfflineNotificationData data) {
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "onLocationUpdateError " + errorMessage);
        if (errorCode == HTTPStatus.UNAUTHORIZED) {
            Intent locationIntent = new Intent(Keys.UNAUTHORIZED_BROADCAST);
            sendBroadcast(locationIntent);
        } /*else if (errorCode == HTTPStatus.FENCE_ERROR) {
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
        }*/ /*else {
            onInactiveByCronJob(data.getMessage());
        }*/
    }

    /***
     * Handle Location API Error case for API failures.
     * @param locationResponse latest response from server.
     */
    private void handleLocationErrorUseCase(LocationResponse locationResponse) {
        if (locationResponse != null) {
            switch (locationResponse.getCode()) {
                case Constants.ApiError.BUSINESS_LOGIC_ERROR: {
                    Utils.handleLocationBusinessLogicErrors(mBus, locationResponse);
                    break;
                }
                //TODO Will update unauthorized check on error callback when API team adds 401 status code in their middle layer.
                case HTTPStatus.UNAUTHORIZED: {
                    EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
                    break;
                }
                default:
                    Utils.appToast(locationResponse.getMessage());
            }
        }

    }


    private void onInactiveByCronJob(String data) {
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "onInactiveByCronJob " + data);
        AppPreferences.setAdminMsg(null);
        AppPreferences.setAvailableStatus(false);
        mBus.post(Keys.INACTIVE_PUSH);
        Notifications.generateAdminNotification(mContext, data);
    }

    /**
     * This method handles fcm token when refreshed
     * Updated previous code that was being handled in FirebaseInstanceIdService as
     * FirebaseInstanceIdService is depricated in latest sdk versions
     *
     * @param refreshedToken String fcm token
     */
    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Utils.redLog(TAG, "REFRESHED TOKEN GENERATED : " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to Bykea servers.
     * This method associates the user's FCM InstanceID token with bykea server-side account
     *
     * @param token String The new token.
     */
    private void sendRegistrationToServer(String token) {
        AppPreferences.setRegId(token);
        if (Utils.isFcmIdUpdateRequired(AppPreferences.isLoggedIn())) {
            new UserRepository().updateRegid(this, new UserDataHandler());
        }
    }


    /**
     * Handle for Acknowledge API Call
     */
    private static IUserDataHandler handler = new UserDataHandler() {
        @Override
        public void onDriverAcknowledgeResponse(MultiDeliveryCallDriverAcknowledgeResponse
                                                        response) {
            if (response != null) {
                ActivityStackManager
                        .getInstance()
                        .startMultiDeliveryCallingActivity(
                                AppPreferences.getMultiDeliveryCallDriverData(),
                                true,
                                DriverApp.getContext()
                        );
            }
        }

        @Override
        public void onError(int errorCode, String errorMessage) {
            if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
            } else {
                EventBus.getDefault().post(Keys.MULTIDELIVERY_ERROR_BORADCAST);
                Utils.redLog("Error:", errorMessage);
            }
        }
    };
}
