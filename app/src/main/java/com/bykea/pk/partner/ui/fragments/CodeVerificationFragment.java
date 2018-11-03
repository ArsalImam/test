package com.bykea.pk.partner.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.NumberVerificationActivity;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.StringCallBack;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.DonutProgress;
import com.bykea.pk.partner.widgets.FontEditText;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.FontUtils;
import com.instabug.library.Instabug;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class CodeVerificationFragment extends Fragment {


    @BindView(R.id.verificationCodeEt_1)
    FontEditText verificationCodeEt;
    @BindView(R.id.doneBtn)
    Button doneBtn;
    @BindView(R.id.donut_progress)
    DonutProgress donutProgress;
    @BindView(R.id.counterTv)
    FontTextView counterTv;
    @BindView(R.id.llBottom)
    LinearLayout llBottom;
    @BindView(R.id.titleMsg)
    FontTextView titleMsg;
    @BindView(R.id.tvSendCodeViaCall)
    FontTextView tvSendCodeViaCall;

    @BindView(R.id.resendTv)
    FontTextView resendTv;

    private NumberVerificationActivity mCurrentActivity;
    private UserRepository mUserRepository;
    private ActivityStackManager mStackManager;
    private int progress = 0;
    private CountDownTimer timer;
    private int counter = 0;
    private int totalTime = (int) (Constants.VERIFICATION_WAIT_MAX_TIME / 1000);
    private Unbinder unbinder;

    public CodeVerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_code_verification, container, false);
        unbinder = ButterKnife.bind(this, v);

        mCurrentActivity = (NumberVerificationActivity) getActivity();
        mStackManager = ActivityStackManager.getInstance();

        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserRepository = new UserRepository();
        mCurrentActivity.setTitleCustomToolbarWithUrdu(
                getString(R.string.wait_while_we_verify), "");
        clearEditText();
        initVerificationEditText();
        initDonutProgress();
        animateDonutProgress();
        titleMsg.setText(Utils.phoneNumberToShow(AppPreferences.getPhoneNumber()));
    }

    //region General Helper methods

    /**
     * This method sets initial title text
     */
    private void setTitleAtStart() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(FontUtils.getStyledTitle(mCurrentActivity, R.string.received_code_ur,
                Constants.FontNames.JAMEEL_NASTALEEQI));
        spannableStringBuilder.append(FontUtils.getStyledTitle(mCurrentActivity, R.string.sms,
                Constants.FontNames.OPEN_SANS_REQULAR));
        spannableStringBuilder.append(FontUtils.getStyledTitle(mCurrentActivity, R.string.enter_code_ur,
                Constants.FontNames.JAMEEL_NASTALEEQI));
        mCurrentActivity.setTitleCustomToolbar(spannableStringBuilder);
    }


    /**
     * This method sets required listeners with verificationCodeEt
     */
    private void initVerificationEditText() {
        verificationCodeEt.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        verificationCodeEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                verificationCodeEt.setFocusableInTouchMode(true);
                verificationCodeEt.setFocusable(true);
                verificationCodeEt.requestFocus();
                return false;
            }
        });
        verificationCodeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Utils.hideSoftKeyboard(mCurrentActivity, verificationCodeEt);
                return true;
            }
        });
        verificationCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (verificationCodeEt.getText().length() == 4) {
                    handleDoneButtonClick();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    /***
     * Clear verification code text view
     */
    private void clearEditText() {
        if (verificationCodeEt != null) {
            verificationCodeEt.getText().clear();
        }
    }


    /***
     * Setup count down timer for progress bar for OTP
     */
    private void setupCountDownTimer() {
        timer = new CountDownTimer(Constants.VERIFICATION_WAIT_MAX_TIME,
                Constants.VERIFICATION_WAIT_COUNT_DOWN) {
            @Override
            public void onTick(long millisUntilFinished) {
                donutProgress.setProgress(progress++);
                counter++;
                if (counter == 10) {
                    counter = 0;
                    totalTime--;
                    counterTv.setText(String.valueOf(totalTime));
                }

            }

            @Override
            public void onFinish() {
                totalTime = 0;
                counterTv.setText(R.string.digit_zero);
                donutProgress.setProgress((int) (Constants.VERIFICATION_WAIT_MAX_TIME / 100));
                llBottom.setVisibility(View.VISIBLE);
                //tvSendCodeViaCall.setVisibility(View.VISIBLE);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(FontUtils.getStyledTitle(mCurrentActivity,
                        R.string.verify_on_call_ur,
                        Constants.FontNames.JAMEEL_NASTALEEQI));
                mCurrentActivity.updateTitleCustomToolbar(spannableStringBuilder);
            }
        };


    }

    /***
     * Start Count down timer
     */
    private void startTimer() {
        if (timer != null) {
            timer.start();
        }
    }

    /***
     * Clear count down timer
     */
    private void clearTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    /***
     * Handle button click when user has entered OTP code.
     */
    private void handleDoneButtonClick() {
        if (Utils.isConnected(mCurrentActivity, true)) {
            if (validateOtpCode()) {
                requestCodeVerification(verificationCodeEt.getText().toString().trim());
            }
        }
    }

    /***
     * Handle request for resend code when user has failed to enter OTP in given time frame.
     * We give user the option to receive OTP via call.
     */
    private void handleResendCode() {
        if (Utils.isConnected(mCurrentActivity, true)) {
            Dialogs.INSTANCE.showLoader(mCurrentActivity);
            animateDonutProgress();
            requestVerificationCodeViaCall();
        }
    }

    /***
     *  Validate OTP code in term of not being empty.
     * @return True if OTP number is entered otherwise its false.
     */
    private boolean validateOtpCode() {
        if (StringUtils.isBlank(verificationCodeEt.getText().toString())) {
            verificationCodeEt.setError(getString(R.string.error_field_empty));
            verificationCodeEt.requestFocus();
            return false;
        }
        return true;
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


    /***
     * Register SMS receiver broadcast intent.
     */
    private void registerSMSReceiver() {
        IntentFilter filter = new IntentFilter(Constants.SMS_RECEIVER_TAG);
        if (mCurrentActivity != null) {
            mCurrentActivity.registerReceiver(mSmsReceiver, filter);
        }
    }

    /***
     * Un Register SMS Receiver broadcast intent
     */
    private void unRegisterSMSReceiver() {
        if (mCurrentActivity != null) {
            mCurrentActivity.unregisterReceiver(mSmsReceiver);
        }
    }

    //endregion

    //region Life cycle methods
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        clearTimer();
        clearEditText();
    }

    @Override
    public void onResume() {
        registerSMSReceiver();
        super.onResume();
    }

    @Override
    public void onPause() {
        unRegisterSMSReceiver();
        super.onPause();
    }


    //endregion

    //region View click listener
    @OnClick({R.id.doneBtn, R.id.resendTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.doneBtn:
                handleDoneButtonClick();
                break;
            case R.id.resendTv:
                handleResendCode();
                break;
        }
    }
    //endregion

    //region Helper methods for Donut Progress

    /**
     * This method starts count down Progress animation
     */
    private void animateDonutProgress() {
        llBottom.setVisibility(View.INVISIBLE);
        setTitleAtStart();
        clearEditText();
        clearTimer();

        progress = 0;
        counter = 0;
        totalTime = (int) (Constants.VERIFICATION_WAIT_MAX_TIME / 1000);
        counterTv.setText(String.valueOf(totalTime));

        setupCountDownTimer();
        startTimer();


    }

    /**
     * This method sets initial values for Donut Progress View
     */
    private void initDonutProgress() {
        donutProgress.setMax((int) (Constants.VERIFICATION_WAIT_MAX_TIME / 100));
    }

    //endregion

    //region Helper methods for API request and response handling

    /***
     * Send request to API server with provided OTP code entered by User.
     * @param enteredOTP Entered OTP
     */
    private void requestCodeVerification(String enteredOTP) {
        Dialogs.INSTANCE.showLoader(mCurrentActivity);
        mUserRepository.requestUserLogin(mCurrentActivity, mCallBack,
                AppPreferences.getPhoneNumber(),
                enteredOTP);
    }

    /***
     * Send Request to API server which tell OTP should be send to user via phone call
     */
    private void requestVerificationCodeViaCall() {
        mUserRepository.requestDriverLogin(mCurrentActivity, mCallBack,
                AppPreferences.getPhoneNumber(),
                AppPreferences.getLatitude(),
                AppPreferences.getLongitude(),
                Constants.OTP_CALL);
    }

    /***
     * Handle Driver successful login use case, Store information received from API in local storage.
     * @param loginResponse Login response received from API Server.
     */
    private void saveDriverDataInStorage(LoginResponse loginResponse) {
        AppPreferences.setStatsApiCallRequired(true);
        ActivityStackManager.getInstance().startLocationService(mCurrentActivity);
        AppPreferences.setPilotData(loginResponse.getUser());
        AppPreferences.setAvailableStatus(loginResponse.getUser().isAvailable());
        AppPreferences.setCashInHands(loginResponse.getUser().getCashInHand());
        /*AppPreferences.setCashInHandsRange(mCurrentActivity, loginResponse.getUser().getCashInHandRange());
        AppPreferences.setVerifiedStatus(mCurrentActivity, loginResponse.getUser().isVerified());*/
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
    }

    /***
     * Handle Verify driver API error case.
     * @param loginResponse Latest response received from API
     */
    private void handleDriverErrorCase(LoginResponse loginResponse) {
        if (loginResponse != null) {
            String errorMessage;
            if (StringUtils.containsIgnoreCase(loginResponse.getMessage(),
                    getString(R.string.invalid_code_error_message))) {
                errorMessage = getString(R.string.invalid_phone_urdu);
            } else {
                errorMessage = loginResponse.getMessage();
            }

            Dialogs.INSTANCE.showAlertDialogUrduWithTickCross(mCurrentActivity, errorMessage, 0f,
                    null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Dialogs.INSTANCE.dismissDialog();
                        }
                    });
        }

    }

    //endregion

    //region Helper methods for SMS receiver and Extraction

    private BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.SMS_RECEIVER_TAG.equalsIgnoreCase(intent.getAction())) {
                try {
                    SmsMessage smsMessage = null;

                    //Todo test commented code that is alternative of deprecated method SmsMessage.createFromPdu
                    if (Build.VERSION.SDK_INT >= 19) { //KITKAT
                        SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                        smsMessage = msgs[0];
                    } else {
                        final Bundle bundle = intent.getExtras();
                        if (bundle != null) {
                            Object pdus[] = (Object[]) bundle.get("pdus");
                            if (pdus != null)
                                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
                        }
                    }
                    // verification code from sms
                    if (smsMessage != null) {
                        Constants.VERIFICATION_CODE_RECEIVED =
                                getVerificationCode(smsMessage.getMessageBody());
                        if (!Constants.VERIFICATION_CODE_RECEIVED.isEmpty()) {
                            verificationCodeEt.setText(Constants.VERIFICATION_CODE_RECEIVED);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    /***
     * Extract verification code once received.
     * @param message Message received from SMS broadcast.
     * @return Extracted message.
     */
    private String getVerificationCode(String message) {
        String code = "";
        String[] msgs = message.split(" ");
        for (String msg : msgs) {
            if (msg.length() == 4 && msg.matches(Constants.REG_EX_DIGIT)) {
                code = msg;
                break;
            }
        }
        return code;
    }

    //endregion

    //region API response Callback

    private IUserDataHandler mCallBack = new UserDataHandler() {

        @Override
        public void onUserLogin(final LoginResponse loginResponse) {
            super.onUserLogin(loginResponse);

            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loginResponse.isSuccess()) {
                            Utils.redLog("token_id at Login",
                                    loginResponse.getUser().getAccessToken());
                            saveDriverDataInStorage(loginResponse);
                            mCurrentActivity.finish();
                        } else {
                            handleDriverErrorCase(loginResponse);
                        }
                    }
                });
            }
        }

        @Override
        public void onNumberVerification(final VerifyNumberResponse response) {
            if (mCurrentActivity != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        Utils.appToast(mCurrentActivity, response.getMessage());

                    }
                });
            }
        }


        @Override
        public void onError(int errorCode, String errorMessage) {
            if (mCurrentActivity != null && getView() != null) {
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.INSTANCE.dismissDialog();
                        Dialogs.INSTANCE.showInvalidCodeDialog(mCurrentActivity);
                    }
                });
            }
        }
    };

    //endregion

}
