package com.bykea.pk.partner.ui.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SMS Broadcast receiver which handles SMS reading from System using {@link SmsRetriever}
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = SmsBroadcastReceiver.class.getSimpleName();

    private OTPReceiveListener otpReceiveListener;

    /**
     * Set {@link OTPReceiveListener callback} listener on calling {@link android.app.Activity}
     * or {@link android.support.v4.app.Fragment}
     *
     * @param receiveListener {@link OTPReceiveListener} listener
     */
    public void setOtpCallback(OTPReceiveListener receiveListener) {
        this.otpReceiveListener = receiveListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.
                    contentEquals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status status;
                if (extras != null) {
                    status = extras.getParcelable(SmsRetriever.EXTRA_STATUS);
                    if (status != null) {
                        switch (status.getStatusCode()) {
                            case CommonStatusCodes.SUCCESS:
                                extractOtpVerificationCode(extras);
                                break;
                            case CommonStatusCodes.TIMEOUT:
                                // Waiting for SMS timed out (5 minutes)
                                if (otpReceiveListener != null)
                                    otpReceiveListener.onOTPTimeOut();
                                break;
                        }
                    }

                }
            }
        }
    }

    /**
     * Extracting OTP verification code received on user device
     *
     * @param extras {@link Bundle} object which contains payload for OTP message
     */
    private void extractOtpVerificationCode(Bundle extras) {
        // Get SMS message contents
        String otpMessage = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE);

        Pattern smsPattern = Pattern.compile("(\\d{4})");
        Matcher matcher = smsPattern.matcher(otpMessage);
        // Extract one-time code from the message and complete verification
        String otpCode = null;
        if (matcher.find()) {
            Log.d(TAG, matcher.group(1));
            otpCode = matcher.group(1);
        }
        Log.d(TAG, "code extracted:" + otpCode);
        if (otpReceiveListener != null)
            otpReceiveListener.onOTPReceived(otpCode);
    }

}
