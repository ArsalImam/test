package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.instabug.library.Instabug;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {
    private LoginActivity mCurrentActivity;

    @BindView(R.id.pinCodeTv)
    FontEditText pinCodeTv;
    @BindView(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;

    @BindView(R.id.loginBtn)
    ImageView loginBtn;

    private UserRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        initialSetupProcess();
    }


    //region General Helper methods

    /***
     * Setup initial configuration for login screen.
     */
    private void initialSetupProcess() {
        repository = new UserRepository();
        ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
        callSettingsApiIfRequired();
        Utils.setOneSignalPlayerId();
        enableLoginButton(false);

        if (StringUtils.isBlank(AppPreferences.getRegId())) {
            AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken());
        }

        if (Utils.isGetCitiesApiCallRequired()) {
            repository.getCities(mCurrentActivity, handler);
        }

        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        phoneNumberEt.addTextChangedListener(mTextWatcher);

    }


    /**
     * Validate should we call Setting API
     */
    private void callSettingsApiIfRequired() {
        if (AppPreferences.getSettings() == null
                || AppPreferences.getSettings().getSettings() == null
                || StringUtils.isBlank(AppPreferences.getSettings().getSettings().getPartner_signup_url())
                || AppPreferences.getSettings().getRegion_services() == null) {
            new UserRepository().requestSettings(mCurrentActivity, handler);
        }
    }

    /***
     * Validate is our entered phone is valid it enables login button. Otherwise, it disables it.
     */
    private void validateFields() {
        if (Utils.isValidNumber(phoneNumberEt)) {
            enableLoginButton(true);
        } else {
            enableLoginButton(false);
        }
    }

    /***
     * Update Login button appearance for enable and disable clickable events.
     *
     * @param enable should enable button
     */
    private void enableLoginButton(boolean enable) {
        if (enable) {
            loginBtn.setEnabled(true);
            loginBtn.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.button_green));
        } else {
            loginBtn.setEnabled(false);
            loginBtn.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.button_gray_round));
        }
    }

    /**
     * Send logs to Analytics
     *
     * @param event Event name which needs to be send
     */
    private void logAnalyticsEvent(String event) {
        try {
            JSONObject data = new JSONObject();
            data.put("timestamp", System.currentTimeMillis());
            Utils.logFacebookEvent(mCurrentActivity, event, data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Helper methods for API request

    /***
     * Send request to API server for Login request.
     *
     * @param phoneNumber Driver phone number
     */
    private void sendLoginRequest(String phoneNumber) {
        repository.requestUserLogin(mCurrentActivity, handler,
                Utils.phoneNumberForServer(phoneNumber),
                pinCodeTv.getText().toString());
    }
    //endregion


    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            validateFields();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    @OnClick({R.id.loginBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (Utils.isValidNumber(mCurrentActivity, phoneNumberEt)) {
                        if (StringUtils.isBlank(AppPreferences.getRegId())) {
                            AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken());
                        }
                        AppPreferences.setStatsApiCallRequired(true);
                        Dialogs.INSTANCE.showLoader(mCurrentActivity);
                        sendLoginRequest(phoneNumberEt.getText().toString());
                    }
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, getString(R.string.error_internet_connectivity));
                }
                break;
        }
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onUserLogin(final LoginResponse loginResponse) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        if (loginResponse.isSuccess()) {
                            Utils.redLog("token_id at Login", loginResponse.getUser().getAccessToken());
                            ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
                            AppPreferences.setPilotData(loginResponse.getUser());
                            AppPreferences.setAvailableStatus(loginResponse.getUser().isAvailable());
                            AppPreferences.setCashInHands(loginResponse.getUser().getCashInHand());
//                            AppPreferences.setCashInHandsRange(mCurrentActivity, loginResponse.getUser().getCashInHandRange());
//                            AppPreferences.setVerifiedStatus(mCurrentActivity, loginResponse.getUser().isVerified());
                            AppPreferences.saveLoginStatus(true);
                            Instabug.setUserData(loginResponse.getUser().getFullName() + " " + loginResponse.getUser().getPhoneNo());
                            Instabug.setUserEmail(loginResponse.getUser().getPhoneNo());
                            Instabug.setUsername(loginResponse.getUser().getFullName());
                            Utils.setOneSignalTag("city", loginResponse.getUser().getCity().getName().toLowerCase());
                            Utils.setOneSignalTag("type", loginResponse.getUser().is_vendor() ? "vendor" : "normal");
                            Utils.setOneSignalTag("tag", "driver");
                            Utils.setOneSignalTag("driver_id", loginResponse.getUser().getId());
                            ActivityStackManager.getInstance().startHomeActivity(true, mCurrentActivity);
                            // Connect socket
                            DriverApp.getApplication().connect();
//                            Utils.setMixPanelUserId(mCurrentActivity);

                            logAnalyticsEvent(Constants.AnalyticsEvents.ON_LOGIN_SUCCESS);

                            mCurrentActivity.finish();
                        } else {
                            if (loginResponse.getCode() == 600) {
                                if (StringUtils.isBlank(loginResponse.getLink())) {
                                    loginResponse.setLink("https://play.google.com/store/apps/details?id=com.bykea.pk.partner");
                                }
                                Dialogs.INSTANCE.showUpdateAppDialog(mCurrentActivity, "اعلان !", loginResponse.getMessage(), loginResponse.getLink());
                            } else if (loginResponse.getCode() == 900) {

                                if (loginResponse.getMessage().toLowerCase().contains("your license has expired")) {
                                    Dialogs.INSTANCE.showInactiveAccountDialog(mCurrentActivity, loginResponse.getSupport(), loginResponse.getMessage());
                                } else {
                                    Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity, new StringCallBack() {
                                        @Override
                                        public void onCallBack(String msg) {

                                        }
                                    }, null, "", loginResponse.getMessage());
//                                Dialogs.INSTANCE.showInactiveAccountDialog(mCurrentActivity, loginResponse.getSupport(), loginResponse.getMessage());
                                }
                            } else {
                                String msg = StringUtils.containsIgnoreCase(loginResponse.getMessage(), getString(R.string.invalid_phone))
                                        ? getString(R.string.invalid_phone_urdu) : loginResponse.getMessage();
                                Dialogs.INSTANCE.showAlertDialogUrduWithTickCross(mCurrentActivity, msg, 0f,
                                        null, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Dialogs.INSTANCE.dismissDialog();
                                            }
                                        });
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
                        Dialogs.INSTANCE.dismissDialog();
                        Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, "Error", errorMessage);
                    }
                });
            }

        }
    };


}
