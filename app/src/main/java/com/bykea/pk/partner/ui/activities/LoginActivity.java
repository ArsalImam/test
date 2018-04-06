package com.bykea.pk.partner.ui.activities;

import android.os.Bundle;
import android.view.View;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontEditText;
import com.google.firebase.iid.FirebaseInstanceId;
import com.instabug.library.Instabug;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity {
    private LoginActivity mCurrentActivity;

    @BindView(R.id.pinCodeTv)
    FontEditText pinCodeTv;
    @BindView(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;

    private UserRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        mCurrentActivity = this;
        ButterKnife.bind(this);
        ActivityStackManager.getInstance().restartLocationService(mCurrentActivity);
        repository = new UserRepository();
        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        if (StringUtils.isBlank(AppPreferences.getRegId())) {
            AppPreferences.setRegId(FirebaseInstanceId.getInstance().getToken());
        }
        Utils.setOneSignalPlayerId();
    }

    @OnClick({R.id.loginBtn, R.id.forgotPassTv})
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

                            mCurrentActivity.finish();
                        } else {
                            if (loginResponse.getCode() == 600) {
                                if (StringUtils.isBlank(loginResponse.getLink())) {
                                    loginResponse.setLink("https://play.google.com/store/apps/details?id=com.bykea.pk.partner");
                                }
                                Dialogs.INSTANCE.showUpdateAppDialog(mCurrentActivity, "Update App", loginResponse.getMessage(), loginResponse.getLink());
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
                                Dialogs.INSTANCE.showAlertDialog(mCurrentActivity, "Error", loginResponse.getMessage());
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
