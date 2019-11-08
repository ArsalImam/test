package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.rest.RestRequestHandler;
import com.bykea.pk.partner.communication.socket.WebIO;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.dal.source.remote.Backend;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.OfflineNotificationData;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryTrip;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AdvertisingIdTask;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bykea.pk.partner.utils.Constants.ScreenRedirections.HOME_SCREEN_S;


public class SplashActivity extends BaseActivity {

    public final String TAG = SplashActivity.class.getSimpleName();

    private SplashActivity mCurrentActivity;
    private UserRepository repository;
    private CountDownTimer splashTimer;

    @BindView(R.id.tv_welcome_message)
    FontTextView txtWelcomeMessage;

    private EventBus mBus = EventBus.getDefault();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCurrentActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        screenConfigurationSetup();
        resetGeoReverseCode();
        Utils.setOneSignalPlayerId();
    }


    //region General helper method

    /***
     * Configure Screen for initial setup
     */
    private void screenConfigurationSetup() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Utils.setFullScreen(mCurrentActivity);
    }

    /***
     * Resets API Key requirement to "false" after 24 hours
     * if there was any error while getting address via Reverse Geo Coding method without using API key.
     */
    private void resetGeoReverseCode() {
        if (AppPreferences.isGeoCoderApiKeyRequired()
                && Utils.isGeoCoderApiKeyCheckRequired()) {
            AppPreferences.setGeoCoderApiKeyRequired(false);
        }
        Utils.setOneSignalPlayerId();
        checkInactivePush();

    }

    /**
     * This method check if Splash Activity is launched from Inactive Push Notification
     */
    private void checkInactivePush() {
        //get notification data info
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.get("event") != null) {
            String event = (String) bundle.get("event");
            if (Constants.FcmEvents.INACTIVE_PUSH.equalsIgnoreCase(event)) {
                Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Notification Clicked");
                String dataJsonString = (String) bundle.get("data");
                ActivityStackManager.getInstance().startHandleInactivePushService(mCurrentActivity,
                        new Gson().fromJson(dataJsonString, OfflineNotificationData.class));
            }
        }
    }


    /**
     * Call all required apis once base url is set and then it will start a timer that
     * will decide which activity needs to be started as launch next activity
     */
    private void configureScreenForApiRequest() {
        fetchUserLocationAndFireBaseDeviceID();
        repository = new UserRepository();
        if (Utils.isGetCitiesApiCallRequired()) {
            repository.getCities(mCurrentActivity, handler);
        }
        if (AppPreferences.isLoggedIn()) {
            if (Utils.isFcmIdUpdateRequired(true)) {
                repository.updateRegid(this, handler);
            }
        } else if (StringUtils.isBlank(AppPreferences.getADID())) {
            new AdvertisingIdTask().execute();
        }
        Utils.redLog(TAG, ApiTags.BASE_SERVER_URL);
        configureCountdownTimeForSplash();
        startTimer();
    }

    /**
     * Open Home screen activity i.e. Driver Dashboard screen
     */
    private void startHomeActivity() {
        ActivityStackManager.getInstance().startHomeActivity(false, mCurrentActivity);
        finish();
    }

    //endregion

    //region Helper methods for Splash layout

    /***
     * Start time for splash layout
     */
    private void startTimer() {
        splashTimer.start();
    }

    /***
     * Stop time for splash layout
     */
    private void disconnectTimer() {
        if (splashTimer != null) {
            splashTimer.cancel();
        }
    }

    /***
     * Configure countdown time for splash screen where we start location service for API,
     * We request FCM ID for registration of the device.
     */
    private void configureCountdownTimeForSplash() {
        splashTimer = new CountDownTimer(Constants.SPLASH_SCREEN_FUTURE_TIMER,
                Constants.SPLASH_SCREEN_INTERVAL_TIMER) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (mCurrentActivity != null) {
                    mCurrentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Connectivity.isConnectedFast(mCurrentActivity)) {
                                if (Utils.isGpsEnable()) {
                                    validateLoginFlow();
                                }
                            } else {
                                displayNoInternetDialog();
                            }
                        }
                    });
                }
            }
        };
    }

    /***
     * Request Location manager for device latest location via a background service,
     * Plus we request FCM Device ID if it's found we store it inside App preference
     */
    private void fetchUserLocationAndFireBaseDeviceID() {
        if (StringUtils.isBlank(AppPreferences.getRegId())
                && StringUtils.isNotBlank(FirebaseInstanceId.getInstance().getToken())) {
            AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken());
        }
        ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
    }


    /***
     * Validate Login flow for the user.
     * If user is already login we connect socket with server and check if there are some running trip.
     * If we find there are some running trip for the login user to redirect him to ride screen.
     * Otherwise, we redirect him to dashboard screen.
     *
     * If the user not logged In we hide splash view and show welcome screen view
     */
    private void validateLoginFlow() {
        if (AppPreferences.isLoggedIn()) {
            // Connect socket
            DriverApp.getApplication().connect();
            if (AppPreferences.isOnTrip()) {
                repository.requestRunningTrip(mCurrentActivity, handler);
            } else {
                startHomeActivity();
            }
        } else {
            ActivityStackManager.getInstance().startLandingActivity(mCurrentActivity);
        }
    }


    /***
     * Display no internet dialog when we detect internet connect is offline.
     */
    private void displayNoInternetDialog() {
        if (!Dialogs.INSTANCE.isShowing() && mCurrentActivity != null && !mCurrentActivity.isFinishing()) {
            Dialogs.INSTANCE.showAlertDialogUrduWithTickCross(mCurrentActivity,
                    getString(R.string.no_internet_msg_ur),
                    getResources().getDimension(R.dimen._5sdp),
                    null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Dialogs.INSTANCE.dismissDialog();
                            finish();
                        }
                    });
        }
    }

    //endregion

    //region Helper method for Build variant identification

    /**
     * Validate Base URL for local build variants.
     * if it's valid base URL would call respective API for screens.
     * Otherwise would show dialog for base URL input.
     */
    private void validateLocalBuildVariantURL() {
        if (BuildConfig.FLAVOR_URL.equalsIgnoreCase(ApiTags.LOCAL_BASE_URL) ||
                !Utils.isValidUrl(ApiTags.LOCAL_BASE_URL)) {
            displayDialogBaseURLInput();
        } else {
            configureScreenForApiRequest();
        }
    }

    /***
     * Display Dialog where user will be asked to input base URL.
     * This would only be displayed when build variant is local.
     */
    private void displayDialogBaseURLInput() {
        Dialogs.INSTANCE.showAlertDialogForURL(mCurrentActivity, new StringCallBack() {
            @Override
            public void onCallBack(String localUrl, String localLoadboardUrl) {
                ApiTags.LOCAL_BASE_URL = localUrl;
                ApiTags.BASE_SERVER_URL = ApiTags.LOCAL_BASE_URL;

                AppPreferences.setLocalBaseUrl(ApiTags.BASE_SERVER_URL);
//                ApiClient.INSTANCE.setAPI_BASE_URL(localLoadboardUrl);
                Backend.Companion.setFLAVOR_URL_TELOS(localUrl);
                Backend.Companion.setFLAVOR_URL_LOADBOARD(localLoadboardUrl);

                WebIO.getInstance().clearConnectionData();
                new RestRequestHandler().clearRetrofitClient();
                configureScreenForApiRequest();
            }
        });
    }

    //endregion

    @Override
    public void onEvent(String action) {
        super.onEvent(action);
        if (action.equalsIgnoreCase(Constants.ON_PERMISSIONS_GRANTED)) {
            if (BuildConfig.FLAVOR.equalsIgnoreCase(Constants.BUILD_VARIANT_LOCAL_FLAVOR)) {
                validateLocalBuildVariantURL();
            } else {
                configureScreenForApiRequest();
            }
        } else if (action.equalsIgnoreCase(GPS_ENABLE_EVENT)) {
            validateLoginFlow();
        }
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onRunningTrips(final CheckDriverStatusResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess()) {
                            try {
                                checkTripType(response);
                            } catch (NullPointerException e) {
                                //If there is no pending tripInfo free all states for new tripInfo..
                                Utils.setCallIncomingState();
                                startHomeActivity();
                            }
                        } else {
                            if (response.getCode() == HTTPStatus.UNAUTHORIZED) {
                                Utils.onUnauthorized(mCurrentActivity);
                            } else {
                                //If there is no pending tripInfo free all states for new tripInfo..
                                Utils.setCallIncomingState();
                                startHomeActivity();
                            }
                        }
                    }
                });
            }

        }

        @Override
        public void onError(final int errorCode, String errorMessage) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (errorCode) {
                            case HttpURLConnection.HTTP_UNAUTHORIZED:
                                AppPreferences.saveLoginStatus(false);
                                AppPreferences.setIncomingCall(false);
                                AppPreferences.setCallData(null);
                                AppPreferences.setTripStatus("");
                                AppPreferences.saveLoginStatus(false);
                                AppPreferences.setPilotData(null);
                                HomeActivity.visibleFragmentNumber = HOME_SCREEN_S;
                                Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Dialogs.INSTANCE.dismissDialog();
                                                ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                                                finish();
                                            }
                                        }, null, getString(R.string.unauthorized_title),
                                        getString(R.string.unauthorized_message));
                                break;
                            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                                EventBus.getDefault().post(Keys.MULTIDELIVERY_ERROR_BORADCAST);
                                splashTimer.onFinish();
                                break;
                            default:
                                splashTimer.onFinish();
                        }
                    }
                });
            }

        }
    };

    /**
     * Check the Type of request is it batch request or single
     *
     * <p>
     * <p>
     * Check if the type is single parse the single trip object i.e {@link NormalCallData}
     * other wise parse the batch trip i.e {@link MultiDeliveryCallDriverData}
     * <p>
     * Check also for unfinished trips if there is unfinished trip remaining land
     * to "Feedback Screen" other wise booking screen according to the type
     *
     * <ul>
     * <li>Check if trip is null thats mean there is no active trip</li>
     * <li>Check if the type is {@linkplain Constants.CallType#SINGLE}</li>
     * <li>Check if the trip status is {@linkplain TripStatus#ON_FINISH_TRIP}</li>
     * </ul>
     *
     * </p>
     *
     * @param response The object of {@linkplain CheckDriverStatusResponse}
     */
    private void checkTripType(CheckDriverStatusResponse response) {

        if (response.getData().getTrip() == null) {
            Utils.setCallIncomingState();
            startHomeActivity();
            return;
        }


        Gson gson = new Gson();
        if (response.getData().getType()
                .equalsIgnoreCase(Constants.CallType.SINGLE)) {
            AppPreferences.setDeliveryType(Constants.CallType.SINGLE);

            String trip = gson.toJson(response.getData().getTrip());
            Type type = new TypeToken<NormalCallData>() {
            }.getType();

            NormalCallData callData = gson.fromJson(trip, type);
            if (StringUtils.isNotBlank(callData.getStarted_at())) {
                AppPreferences.setStartTripTime(
                        AppPreferences.getServerTimeDifference() +
                                Utils.getTimeInMiles(
                                        callData.getStarted_at())
                );
            }
            AppPreferences.setCallData(callData);
            AppPreferences.setTripStatus(callData.getStatus());

            if (!callData.getStatus().equalsIgnoreCase(
                    TripStatus.ON_FINISH_TRIP)) {
                WebIORequestHandler.
                        getInstance().
                        registerChatListener();
                ActivityStackManager.
                        getInstance().
                        startJobActivity(mCurrentActivity);

            } else {
                ActivityStackManager.getInstance()
                        .startFeedbackFromResume(mCurrentActivity);
            }
        } else {
            String trip = gson.toJson(response.getData().getTrip());
            Type type = new TypeToken<MultiDeliveryCallDriverData>() {
            }.getType();
            AppPreferences.setDeliveryType(Constants.CallType.BATCH);
            MultiDeliveryCallDriverData deliveryCallDriverData = gson.fromJson(trip, type);
            AppPreferences.setMultiDeliveryCallDriverData(deliveryCallDriverData);
            // check for unfinished ride later
            List<MultipleDeliveryBookingResponse> bookingResponseList =
                    deliveryCallDriverData.getBookings();

            boolean isFinishedStateFound = false;

            for (MultipleDeliveryBookingResponse bookingResponse : bookingResponseList) {
                // get trip instance
                MultiDeliveryTrip tripData = bookingResponse.getTrip();

                // if trip status if "finished", open invoice screen
                if (tripData.getStatus().
                        equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                    isFinishedStateFound = true;
                    ActivityStackManager.getInstance()
                            .startMultiDeliveryFeedbackActivity(mCurrentActivity,
                                    tripData.getId(), false);
                    break;
                }
            }

            //Navigate to booking screen if no pending invoices found
            if (!isFinishedStateFound)
                ActivityStackManager.
                        getInstance().
                        startMultiDeliveryBookingActivity(mCurrentActivity);


        }
        finish();
    }


    //region Life Cycle Methods
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (findViewById(R.id.activity_splash) != null) {
            Utils.unbindDrawables(findViewById(R.id.activity_splash));
        }
        disconnectTimer();
        Dialogs.INSTANCE.dismissDialog();
    }
    //endregion


}
