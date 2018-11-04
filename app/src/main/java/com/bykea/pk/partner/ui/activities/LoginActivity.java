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
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {
    private LoginActivity mCurrentActivity;


    @BindView(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;

    @BindView(R.id.loginBtn)
    ImageView loginBtn;

    private UserRepository repository;
    private TextWatcher textWatcher;

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
        setupTextWatcherForPhoneNumber();
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

    /***
     * Log analytics events on Facebook Analytics
     *
     * @param event Event name which needs to be send.
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
        repository.requestDriverLogin(mCurrentActivity, handler,
                Utils.phoneNumberForServer(phoneNumber),
                AppPreferences.getLatitude(),
                AppPreferences.getLongitude(),
                Constants.OTP_SMS);
    }
    //endregion

    //region Helper method for Text watcher

    /**
     * Setup phone number watcher for validation
     */
    private void setupTextWatcherForPhoneNumber() {
        textWatcher = new TextWatcher() {
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

        if (phoneNumberEt != null)
            phoneNumberEt.addTextChangedListener(textWatcher);
    }
    //endregion

    //region View Click listener Method

    @OnClick({R.id.loginBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (Utils.isValidNumber(mCurrentActivity, phoneNumberEt)) {
                        if (StringUtils.isBlank(AppPreferences.getRegId())) {
                            AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken());
                        }
                        Utils.hideSoftKeyboard(mCurrentActivity, phoneNumberEt);
                        Dialogs.INSTANCE.showLoader(mCurrentActivity);
                        AppPreferences.setPhoneNumber(Utils.phoneNumberForServer(phoneNumberEt.getText().toString()));
                        logAnalyticsEvent(Constants.AnalyticsEvents.ON_SIGN_UP_BTN_CLICK);
                        sendLoginRequest(phoneNumberEt.getText().toString());
                    }
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, getString(R.string.error_internet_connectivity));
                }
                break;
        }
    }
    //endregion

    //region API response handler
    private UserDataHandler handler = new UserDataHandler() {

        @Override
        public void onNumberVerification(final VerifyNumberResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        if (response != null) {
                            if (response.isSuccess()) {
                                Utils.appToast(mCurrentActivity, response.getMessage());
                                ActivityStackManager.getInstance()
                                        .startPhoneNumberVerificationActivity(mCurrentActivity);
                                mCurrentActivity.finish();
                            } else {
                                handleDriverErrorCase(response);
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
                        Dialogs.INSTANCE.showAlertDialog(mCurrentActivity,
                                getString(R.string.error_heading),
                                errorMessage);
                    }
                });
            }

        }
    };

    /***
     * Handle Various driver Failure use cases.
     * <ul>
     *     <li> App Force Update Error. </li>
     *     <li> Driver License Expire. </li>
     *     <li> Driver not registered. </li>
     *     <li> Driver not allowed to login in this region. </li>
     *     <li> All other error cases. </li>
     * </ul>
     *
     * @param verifyNumberResponse Latest response received from API Server
     */
    private void handleDriverErrorCase(VerifyNumberResponse verifyNumberResponse) {
        if (verifyNumberResponse != null) {
            switch (verifyNumberResponse.getCode()) {
                case Constants.APP_FORCE_UPDATE: {
                    verifyNumberResponse.setLink(String.format(getString(R.string.force_app_update_link),
                            DriverApp.getApplication().getPackageName()));

                    Dialogs.INSTANCE.showUpdateAppDialog(mCurrentActivity,
                            getString(R.string.force_app_update_title),
                            verifyNumberResponse.getMessage(),
                            verifyNumberResponse.getLink());
                    break;
                }
                case Constants.DRIVER_LICENSE_EXPIRED: {
                    if (verifyNumberResponse.getMessage().toLowerCase().contains(
                            getString(R.string.driver_licence_expire_error))) {
                        Dialogs.INSTANCE.showInactiveAccountDialog(mCurrentActivity,
                                verifyNumberResponse.getSupportNumber());
                    } else {
                        Dialogs.INSTANCE.showAlertDialogNotSingleton(mCurrentActivity,
                                new StringCallBack() {
                                    @Override
                                    public void onCallBack(String msg) {

                                    }
                                }, null, "", verifyNumberResponse.getMessage());
                        //Dialogs.INSTANCE.showInactiveAccountDialog(mCurrentActivity, loginResponse.getSupport(), loginResponse.getMessage());
                    }
                    break;
                }
                case Constants.DRIVER_NOT_REGISTER: {
                    ActivityStackManager.getInstance().startRegisterationActiivty(mCurrentActivity);
                    break;
                }
                case Constants.DRIVER_REGION_NOT_ALLOWED:
                    Dialogs.INSTANCE.showRegionOutErrorDialog(mCurrentActivity,
                            getString(R.string.region_out_support_helpline),
                            getString(R.string.region_out_message_ur));
                    break;
                default: {
                    String msg = StringUtils.containsIgnoreCase(verifyNumberResponse.getMessage(),
                            getString(R.string.invalid_phone)) ?
                            getString(R.string.invalid_phone_urdu) : verifyNumberResponse.getMessage();
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

    }


    //endregion

}
