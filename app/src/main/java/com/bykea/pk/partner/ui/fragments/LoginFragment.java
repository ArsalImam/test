package com.bykea.pk.partner.ui.fragments;


import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.LoginActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Permissions;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontButton;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.instabug.library.Instabug;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    @Bind(R.id.pinCodeTv)
    FontEditText pinCodeTv;
    @Bind(R.id.loginBtn)
    FontButton loginBtn;
    @Bind(R.id.forgotPassTv)
    FontTextView forgotPassTv;/*
    @Bind(R.id.countryCodeEt)
    FontEditText countryCodeEt;*/
    @Bind(R.id.phoneNumberEt)
    FontEditText phoneNumberEt;

    private UserRepository repository;
    private LoginActivity mCurrentActivity;
    private PhoneNumberFormattingTextWatcher mPhoneNumberFormattingTextWatcher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new UserRepository();
        mCurrentActivity = (LoginActivity) getActivity();
        phoneNumberEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Permissions.hasLocationPermissions(getContext())) {
                Permissions.getLocationPermissions(this);
            }
        }
        if (StringUtils.isBlank(AppPreferences.getRegId(mCurrentActivity))) {
            AppPreferences.setRegId(mCurrentActivity, FirebaseInstanceId.getInstance().getToken());
        }
        Utils.setOneSignalPlayerId(mCurrentActivity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.loginBtn, R.id.forgotPassTv})
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.loginBtn:
                if (Connectivity.isConnectedFast(mCurrentActivity)) {
                    if (Utils.isValidNumber(mCurrentActivity, phoneNumberEt) && validate()) {
                        if (StringUtils.isBlank(AppPreferences.getRegId(mCurrentActivity))) {
                            AppPreferences.setRegId(mCurrentActivity, FirebaseInstanceId.getInstance().getToken());
                        }
                        AppPreferences.setStatsApiCallRequired(mCurrentActivity, true);
                        Dialogs.INSTANCE.showLoader(getActivity());
                        repository.requestUserLogin(getActivity(), handler,
                                Utils.phoneNumberForServer(phoneNumberEt.getText().toString()),
                                pinCodeTv.getText().toString());
                    }
                } else {
                    Dialogs.INSTANCE.showToast(mCurrentActivity, "Please check your internet connection.");
                }
                break;
            case R.id.forgotPassTv:
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out);
                fragmentTransaction.replace(R.id.containerView, forgotPasswordFragment, null);
                fragmentTransaction.addToBackStack("forgotPassword");
                fragmentTransaction.commit();
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

    public boolean parseContact(String contact, String countrycode) {
        Phonenumber.PhoneNumber phoneNumber = null;
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String finalNumber = null;
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countrycode));
        boolean isValid = false;
        PhoneNumberUtil.PhoneNumberType isMobile = null;
        try {
            phoneNumber = phoneNumberUtil.parse(contact, isoCode);
            isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            isMobile = phoneNumberUtil.getNumberType(phoneNumber);

        } catch (NumberParseException | NullPointerException e) {
            e.printStackTrace();
        }


        if (isValid
                && (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile ||
                PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE == isMobile)) {
            finalNumber = phoneNumberUtil.format(phoneNumber,
                    PhoneNumberUtil.PhoneNumberFormat.E164).substring(1);
            String mPhoneNumber = finalNumber;
            return true;
        }

        return false;
    }

    private UserDataHandler handler = new UserDataHandler() {
        @Override
        public void onUserLogin(final LoginResponse loginResponse) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        if (loginResponse.isSuccess()) {
                            Utils.redLog("token_id at Login", loginResponse.getUser().getAccessToken());
                            ActivityStackManager.getInstance(getActivity()).startLocationService();
                            AppPreferences.setPilotData(getActivity(), loginResponse.getUser());
                            AppPreferences.setAvailableStatus(getActivity(), loginResponse.getUser().isAvailable());
                            AppPreferences.setCashInHands(mCurrentActivity, loginResponse.getUser().getCashInHand());
//                            AppPreferences.setVerifiedStatus(getActivity(), loginResponse.getUser().isVerified());
                            AppPreferences.saveLoginStatus(getActivity(), true);
                            Instabug.setUserData(loginResponse.getUser().getFullName() + " " + loginResponse.getUser().getPhoneNo());
                            Instabug.setUserEmail(loginResponse.getUser().getPhoneNo());
                            Instabug.setUsername(loginResponse.getUser().getFullName());
                            Utils.setOneSignalTag("city", loginResponse.getUser().getCity().getName().toLowerCase());
                            Utils.setOneSignalTag("type", loginResponse.getUser().is_vendor() ? "vendor" : "normal");
                            Utils.setOneSignalTag("tag", "driver");
                            Utils.setOneSignalTag("driver_id", loginResponse.getUser().getId());
                            ActivityStackManager.getInstance(getActivity()).startHomeActivity(true);
                            // Connect socket
                            ((DriverApp) mCurrentActivity.getApplicationContext()).connect("Login Activity");

                            getActivity().finish();
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
            if (mCurrentActivity != null && getView() != null) {
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permissions.LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Permissions.getLocationPermissions(this);
                Dialogs.INSTANCE.showWarningMessage(getActivity(), loginBtn, "Location permission is denied." +
                        " Location is required for your ride");
            }
        }

    }
}
