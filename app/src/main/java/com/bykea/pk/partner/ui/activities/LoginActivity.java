package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.R;
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
        callSettingsApiIfRequired();
        ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
        repository = new UserRepository();
        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        if (StringUtils.isBlank(AppPreferences.getRegId())) {
            AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken());
        }
        Utils.setOneSignalPlayerId();
        if (Utils.isGetCitiesApiCallRequired()) {
            repository.getCities(mCurrentActivity, handler);
        }

        disableBookingBtn();
        phoneNumberEt.addTextChangedListener(mTextWatcher);
        pinCodeTv.addTextChangedListener(mTextWatcher);
    }

    private void callSettingsApiIfRequired() {
        if (AppPreferences.getSettings() == null
                || AppPreferences.getSettings().getSettings() == null
                || StringUtils.isBlank(AppPreferences.getSettings().getSettings().getPartner_signup_url())
                || AppPreferences.getSettings().getRegion_services() == null) {
            new UserRepository().requestSettings(mCurrentActivity, handler);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            validateFileds();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void validateFileds() {
        if (StringUtils.isNotBlank(pinCodeTv.getText().toString())
                && pinCodeTv.getText().length() == 4
                && Utils.isValidNumber(phoneNumberEt)) {
            enableBookingBtn();
        } else {
            disableBookingBtn();
        }
    }

    @OnClick({R.id.loginBtn, R.id.forgotPassTv, R.id.registerBtn, R.id.tvTerms})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (Utils.isValidNumber(mCurrentActivity, phoneNumberEt) && validate()) {
                        if (StringUtils.isBlank(AppPreferences.getRegId())) {
                            AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken());
                        }
                        AppPreferences.setStatsApiCallRequired(true);
                        Dialogs.INSTANCE.showLoader(mCurrentActivity);
                        repository.requestUserLogin(mCurrentActivity, handler,
                                Utils.phoneNumberForServer(phoneNumberEt.getText().toString()),
                                pinCodeTv.getText().toString());
                    }
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, "Please check your internet connection.");
                }
                break;
            case R.id.forgotPassTv:
                ActivityStackManager.getInstance().startForgotPasswordActivity(mCurrentActivity);
                break;
            case R.id.registerBtn:
                logAnalyticsEvent(Constants.AnalyticsEvents.ON_SIGN_UP_BTN_CLICK);
                ActivityStackManager.getInstance().startRegisterationActiivty(mCurrentActivity);
                break;
            case R.id.tvTerms:
                if (AppPreferences.getSettings() != null) {
                    Utils.startCustomWebViewActivity(mCurrentActivity,
                            AppPreferences.getSettings().getSettings().getTerms(), "Terms of Services");
                } else {
                    Utils.startCustomWebViewActivity(mCurrentActivity,
                            "https://www.bykea.com/partner-terms", "Terms of Services");
                }
                break;
        }
    }


    private boolean validate() {
        if (StringUtils.isBlank(pinCodeTv.getText().toString())) {
            pinCodeTv.setError(getString(R.string.error_field_empty));
            pinCodeTv.requestFocus();
            return false;
        }
        return true;
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
                                Dialogs.INSTANCE.showAlertDialogUrduWithTickCross(mCurrentActivity, msg,
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

    private void disableBookingBtn() {
        loginBtn.setEnabled(false);
        loginBtn.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.button_gray_round));
    }

    private void enableBookingBtn() {
        loginBtn.setEnabled(true);
        loginBtn.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.button_green));
    }


    private void logAnalyticsEvent(String event) {
        try {
            JSONObject data = new JSONObject();
            data.put("timestamp", System.currentTimeMillis());
            Utils.logFacebookEvent(mCurrentActivity, event, data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
