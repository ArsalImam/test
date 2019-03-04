package com.bykea.pk.partner.ui.helpers;

/**
 * OTP Receiver Listener Interface
 */
public interface OTPReceiveListener {

    /**
     * Called when OTP is successfully received on device and code is extracted.
     *
     * @param otpCode Extracted code from OTP
     */
    void onOTPReceived(String otpCode);

    /**
     * When we are unable to receive OTP due to TIMEOUT of listener i.e. 5 min set by Google
     */
    void onOTPTimeOut();
}
