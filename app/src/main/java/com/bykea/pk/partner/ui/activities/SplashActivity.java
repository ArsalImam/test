package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.communication.rest.RestRequestHandler;
import com.bykea.pk.partner.communication.socket.WebIO;
import com.bykea.pk.partner.models.data.OfflineNotificationData;
import com.bykea.pk.partner.ui.helpers.AdvertisingIdTask;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.Constants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;


public class SplashActivity extends BaseActivity {

    private static final int REQUEST_CODE = 123;
    private SplashActivity mCurrentActivity;
    private UserRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mCurrentActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Utils.setFullScreen(mCurrentActivity);
        // Resets API Key requirement to "false" after 24 hours if there was any error while getting address via Reverse Geo Coding method without using API key.
        if (AppPreferences.isGeoCoderApiKeyRequired() && Utils.isGeoCoderApiKeyCheckRequired()) {
            AppPreferences.setGeoCoderApiKeyRequired(false);
        }
        Utils.setOneSignalPlayerId();
        checkInactivePush();

    }

    /**
     * This method check if Splash Activity is launched from Inactive Push Notification
     */
    private void checkInactivePush() {
        Utils.redLogLocation(Constants.LogTags.BYKEA_INACTIVE_PUSH, "Notification Clicked");
        //get notification data info
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.get("event") != null) {
            String event = (String) bundle.get("event");
            if (Constants.FcmEvents.INACTIVE_PUSH.equalsIgnoreCase(event)) {
                String dataJsonString = (String) bundle.get("data");
                ActivityStackManager.getInstance().startHandleInactivePushService(mCurrentActivity,
                        new Gson().fromJson(dataJsonString, OfflineNotificationData.class));
            }
        }
    }


    @Override
    public void onEvent(String action) {
        super.onEvent(action);
        if (action.equalsIgnoreCase(Constants.ON_PERMISSIONS_GRANTED)) {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("local")) {
                if (BuildConfig.FLAVOR_URL.equalsIgnoreCase(ApiTags.LOCAL_BASE_URL) ||
                        !Utils.isValidUrl(ApiTags.LOCAL_BASE_URL)) {
                    Dialogs.INSTANCE.showInputAlert(mCurrentActivity, new StringCallBack() {
                        @Override
                        public void onCallBack(String localUrl) {
                            ApiTags.LOCAL_BASE_URL = localUrl;
                            ApiTags.BASE_SERVER_URL = ApiTags.LOCAL_BASE_URL;
                            AppPreferences.setLocalBaseUrl(ApiTags.BASE_SERVER_URL);
                            WebIO.getInstance().clearConnectionData();
                            new RestRequestHandler().clearRetrofitClient();
                            init();
                        }
                    });
                } else {
                    init();
                }
            } else {
                init();
            }
        }
    }

    /**
     * This method call all required apis once base url is set and then it will start a timer that
     * will decide which activity needs to be started as launch next activity
     */
    private void init() {
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
        Utils.redLog("BASE_SERVER_URL", ApiTags.BASE_SERVER_URL);
        startTimer();
    }

    private void startTimer() {
        timer.start();
    }


    private CountDownTimer timer = new CountDownTimer(2000, 2000) {
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
                            if (StringUtils.isBlank(AppPreferences.getRegId())
                                    && StringUtils.isNotBlank(FirebaseInstanceId.getInstance().getToken())) {
                                AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken());
                            }
                            ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
                            if (AppPreferences.isLoggedIn()) {
                                // Connect socket
                                DriverApp.getApplication().connect();
                                if (AppPreferences.isOnTrip()) {
                                    repository.requestRunningTrip(mCurrentActivity, handler);
                                } else {
                                    startHomeActivity();
                                }
                            } else {
                                ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                                finish();
                            }
                        } else {
                            if (!Dialogs.INSTANCE.isShowing()) {
                                Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Dialogs.INSTANCE.dismissDialog();
                                        finish();
                                    }
                                }, null, "Internet Connection", getString(R.string.dialog_internet_error));
                            }
                        }
                    }
                });
            }
        }
    };

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onRunningTrips(final CheckDriverStatusResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccess()) {
                            try {
                                if (StringUtils.isNotBlank(response.getData().getStarted_at())) {
                                    AppPreferences.setStartTripTime(
                                            AppPreferences.getServerTimeDifference() +
                                                    Utils.getTimeInMiles(response.getData().getStarted_at()));
                                }
                                AppPreferences.setCallData(response.getData());
                                AppPreferences.setTripStatus(response.getData().getStatus());
                                if (!response.getData().getStatus().equalsIgnoreCase(TripStatus.ON_FINISH_TRIP)) {
                                    WebIORequestHandler.getInstance().registerChatListener();
                                    ActivityStackManager.getInstance()
                                            .startJobActivity(mCurrentActivity);
                                } else {
                                    ActivityStackManager.getInstance()
                                            .startFeedbackFromResume(mCurrentActivity);
                                }
                                finish();
                            } catch (NullPointerException e) {
                                //If there is no pending trip free all states for new trip..
                                Utils.setCallIncomingState();
                                startHomeActivity();
                            }
                        } else {
                            if (response.getCode() == HTTPStatus.UNAUTHORIZED) {
                                Utils.onUnauthorized(mCurrentActivity);
                            } else {
                                //If there is no pending trip free all states for new trip..
                                Utils.setCallIncomingState();
                                startHomeActivity();
                            }
                        }
                    }
                });
            }

        }

        @Override
        public void onError(final int errorCode, final String errorMessage) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (errorCode == HTTPStatus.UNAUTHORIZED) {
                            AppPreferences.saveLoginStatus(false);
                            AppPreferences.setIncomingCall(false);
                            AppPreferences.setCallData(null);
                            AppPreferences.setTripStatus("");
                            AppPreferences.saveLoginStatus(false);
                            AppPreferences.setPilotData(null);
                            HomeActivity.visibleFragmentNumber = 0;
                            Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialogs.INSTANCE.dismissDialog();
                                    ActivityStackManager.getInstance().startLoginActivity(mCurrentActivity);
                                    finish();
                                }
                            }, null, "UnAuthorized", "Your account is logged in to another device. You are not login here " +
                                    "anymore.");
                        } else {
                            timer.onFinish();
                        }
                    }
                });
            }

        }
    };

    private void startHomeActivity() {
        ActivityStackManager.getInstance().startHomeActivity(false, mCurrentActivity);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (findViewById(R.id.activity_splash) != null) {
            Utils.unbindDrawables(findViewById(R.id.activity_splash));
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}
