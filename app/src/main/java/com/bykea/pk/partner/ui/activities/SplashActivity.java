package com.bykea.pk.partner.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bykea.pk.partner.ui.helpers.AdvertisingIdTask;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
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

import org.apache.commons.lang3.StringUtils;


public class SplashActivity extends BaseActivity {

    private SplashActivity mCurrentActivity;
    private UserRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        repository = new UserRepository();
        Utils.setFullScreen(mCurrentActivity);
        // Resets API Key requirement to "false" after 24 hours if there was any error while getting address via Reverse Geo Coding method without using API key.
        if (AppPreferences.isGeoCoderApiKeyRequired() && Utils.isGeoCoderApiKeyCheckRequired()) {
            AppPreferences.setGeoCoderApiKeyRequired(false);
        }
        if (Utils.isGetCitiesApiCallRequired()) {
            repository.getCities(mCurrentActivity, handler);
        }

        if (StringUtils.isBlank(AppPreferences.getADID()) && !AppPreferences.isLoggedIn()) {
            new AdvertisingIdTask().execute();
        }
        Utils.setOneSignalPlayerId();
    }


    @Override
    public void onEvent(String action) {
        super.onEvent(action);
        if (action.equalsIgnoreCase(Constants.ON_PERMISSIONS_GRANTED)) {
            startTimer();
        }
    }

    private void startTimer() {
        timer.start();
    }


    private CountDownTimer timer = new CountDownTimer(1000, 3000) {
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
                            Utils.infoLog("SPLASH FCM TOKEN : ", AppPreferences.getRegId());
                            ActivityStackManager.getInstance(mCurrentActivity.getApplicationContext()).startLocationService();
                            if (AppPreferences.isLoggedIn()) {
                                // Connect socket
                                ((DriverApp) getApplicationContext()).connect("Splash Activity");
                                WebIORequestHandler.getInstance().setContext(mCurrentActivity);
                                if (AppPreferences.isOnTrip()) {
                                    repository.requestRunningTrip(mCurrentActivity, handler);
                                } else {
                                    startHomeActivity();
                                }
                            } else {
                                ActivityStackManager.getInstance(mCurrentActivity).startLoginActivity();
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
                                    ActivityStackManager.getInstance(mCurrentActivity)
                                            .startJobActivity();
                                } else {
                                    ActivityStackManager.getInstance(mCurrentActivity)
                                            .startFeedbackFromResume();
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
                        Utils.infoLog("CHECK RUNNING RESONSE ", errorMessage);
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
                                    ActivityStackManager.getInstance(mCurrentActivity).startLoginActivity();
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
        ActivityStackManager.getInstance(mCurrentActivity).startHomeActivity(false);
        finish();
    }
}
