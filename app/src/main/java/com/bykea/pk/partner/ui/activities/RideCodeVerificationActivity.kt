package com.bykea.pk.partner.ui.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateRequestObject
import com.bykea.pk.partner.dal.source.remote.response.RideCreateResponse
import com.bykea.pk.partner.dal.util.*
import com.bykea.pk.partner.databinding.ActivityRideCodeVerificationBinding
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.*
import com.bykea.pk.partner.widgets.FontTextView
import com.bykea.pk.partner.widgets.FontUtils
import kotlinx.android.synthetic.main.activity_complain_zendesk_identity.counterTv
import kotlinx.android.synthetic.main.activity_ride_code_verification.*
import org.apache.commons.lang3.StringUtils

class RideCodeVerificationActivity : BaseActivity() {
    private var mProgress = 0
    private var timer: CountDownTimer? = null
    private var counter = 0
    private var totalTime = (Constants.VERIFICATION_WAIT_MAX_TIME / 1000).toInt()

    private lateinit var jobsRepository: JobsRepository
    private lateinit var binding: ActivityRideCodeVerificationBinding

    private var mobileNumber: String? = null
    private lateinit var rideCreateRequestObject: RideCreateRequestObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_code_verification)

        jobsRepository = Injection.provideJobsRepository(this@RideCodeVerificationActivity)

        clearEditText()
        initVerificationEditText()
        initDonutProgress()
        animateDonutProgress()

        intent?.extras?.let {
            if (it.containsKey(Constants.Extras.PHONE_NUMBER)) {
                mobileNumber = it.getString(Constants.Extras.PHONE_NUMBER)
                titleMsg.text = mobileNumber
            }
            if (it.containsKey(Constants.Extras.RIDE_CREATE_DATA)) {
                it.getParcelable<RideCreateRequestObject>(Constants.Extras.RIDE_CREATE_DATA)?.let { data ->
                    rideCreateRequestObject = data
                }
            }
        }
        resendTv.setOnClickListener {
            resendCode(Constants.OTP_CALL)
        }
    }

    //region General Helper methods

    /**
     * This method sets initial title text
     */
    private fun setTitleAtStart() {
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(FontUtils.getStyledTitle(this@RideCodeVerificationActivity, R.string.received_code_ur,
                Constants.FontNames.JAMEEL_NASTALEEQI))
        spannableStringBuilder.append(FontUtils.getStyledTitle(this@RideCodeVerificationActivity, R.string.sms,
                Constants.FontNames.OPEN_SANS_REQULAR))
        spannableStringBuilder.append(FontUtils.getStyledTitle(this@RideCodeVerificationActivity, R.string.enter_code_ur,
                Constants.FontNames.JAMEEL_NASTALEEQI))
        setTitleCustomToolbar(spannableStringBuilder)
    }

    /* Update Toolbar Title and Back button
     * @param title SpannableStringBuilder */
    fun setTitleCustomToolbar(title: SpannableStringBuilder) {
        (findViewById<View>(R.id.tvTitle) as FontTextView).text = title
        findViewById<View>(R.id.ivBackBtn).setOnClickListener { onBackPressed() }
    }


    /* This method sets required listeners with verificationCodeEt */
    private fun initVerificationEditText() {
        verificationCodeEt.transformationMethod = NumericKeyBoardTransformationMethod()
        verificationCodeEt.setOnTouchListener { v, event ->
            verificationCodeEt.isFocusableInTouchMode = true
            verificationCodeEt.isFocusable = true
            verificationCodeEt.requestFocus()
            false
        }
        verificationCodeEt.setOnEditorActionListener { v, actionId, event ->
            Utils.hideSoftKeyboard(this@RideCodeVerificationActivity, verificationCodeEt)
            true
        }
        verificationCodeEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (verificationCodeEt.text!!.length == 5) {
                    handleDoneButtonClick()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


    /* Clear verification code text view */
    private fun clearEditText() {
        if (verificationCodeEt != null) {
            verificationCodeEt.text!!.clear()
        }
    }


    /* Setup count down timer for progress bar for OTP */
    private fun setupCountDownTimer() {
        timer = object : CountDownTimer(Constants.VERIFICATION_WAIT_MAX_TIME,
                Constants.VERIFICATION_WAIT_COUNT_DOWN) {
            override fun onTick(millisUntilFinished: Long) {
                donutProgress.progress = mProgress++.toFloat()
                counter++
                if (counter == 10) {
                    counter = 0
                    totalTime--
                    counterTv.text = totalTime.toString()
                }

            }

            override fun onFinish() {
                totalTime = 0
                counterTv.setText(R.string.digit_zero)
                llBottom.visibility = View.VISIBLE
                donutProgress.progress = (Constants.VERIFICATION_WAIT_MAX_TIME / 100).toInt().toFloat()
            }
        }
    }

    /* Start Count down timer */
    private fun startTimer() {
        if (timer != null) {
            timer?.start()
        }
    }

    /* Clear count down timer */
    private fun clearTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }


    /* Handle button click when user has entered OTP code. */
    private fun handleDoneButtonClick() {
        linLayoutOtpWrongEntered.visibility = View.GONE
        if (Utils.isConnected(this@RideCodeVerificationActivity, true)) {
            if (validateOtpCode()) {
                requestCodeVerificationTripCreation(verificationCodeEt.text!!.toString().trim { it <= ' ' })
            }
        }
    }

    /* Handle request for resend code when user has failed to enter OTP in given time frame.
     * We give user the option to receive OTP via call. */
    fun handleResendCode(view: View) {
        resendCode()
    }

    /**
     *
     */
    fun resendCode(type: String = OTP_SMS) {
        linLayoutOtpWrongEntered.visibility = View.GONE
        if (Utils.isConnected(this@RideCodeVerificationActivity, true)) {
            Dialogs.INSTANCE.showLoader(this@RideCodeVerificationActivity)
            animateDonutProgress()
            requestGenerateCode(type)
        }
    }

    /*Validate OTP code in term of not being empty.
     * @return True if OTP number is entered otherwise its false. */
    private fun validateOtpCode(): Boolean {
        if (StringUtils.isBlank(verificationCodeEt.text!!.toString())) {
            verificationCodeEt.error = getString(R.string.error_field_empty)
            verificationCodeEt.requestFocus()
            return false
        }
        return true
    }

    public override fun onDestroy() {
        super.onDestroy()
        clearTimer()
        clearEditText()
    }

    //region Helper methods for Donut Progress
    /**
     * This method starts count down Progress animation
     */
    private fun animateDonutProgress() {
        llBottom.visibility = View.INVISIBLE
        setTitleAtStart()
        clearEditText()
        clearTimer()

        mProgress = 0
        counter = 0
        totalTime = (Constants.VERIFICATION_WAIT_MAX_TIME / 1000).toInt()
        counterTv.text = totalTime.toString()

        setupCountDownTimer()
        startTimer()
    }

    /**
     * This method sets initial values for Donut Progress View
     */
    private fun initDonutProgress() {
        donutProgress.max = (Constants.VERIFICATION_WAIT_MAX_TIME / 100).toInt()
    }

    //endregion

    //region Helper methods for API request and response handling

    /***
     * Send request to API server with provided OTP code entered by User.
     * @param enteredOTP Entered OTP
     */
    private fun requestCodeVerificationTripCreation(enteredOTP: String) {
        Dialogs.INSTANCE.showLoader(this@RideCodeVerificationActivity)
        rideCreateRequestObject.trip.code = enteredOTP
        jobsRepository.createTrip(rideCreateRequestObject, object : JobsDataSource.CreateTripCallback {
            override fun onSuccess(rideCreateResponse: RideCreateResponse) {
                Dialogs.INSTANCE.dismissDialog()

                AppPreferences.clearTripDistanceData()
                AppPreferences.setTripStatus(TripStatus.ON_ARRIVED_TRIP)
                AppPreferences.setTripAcceptTime(System.currentTimeMillis())
                AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude())
                AppPreferences.setIsOnTrip(true)
                AppPreferences.setDeliveryType(Constants.CallType.SINGLE)
                val callData = NormalCallData()
                callData.status = TripStatus.ON_ARRIVED_TRIP
                callData.tripNo = rideCreateResponse.data?.trip_no
                AppPreferences.setCallData(callData)

                ActivityStackManager.getInstance().startJobActivity(this@RideCodeVerificationActivity);
            }

            override fun onFail(code: Int, subCode: Int?, message: String?) {
                Dialogs.INSTANCE.dismissDialog()
                displayErrorToast(code, subCode, message)
            }
        })
    }

    /**
     * Send Request to API server which tell OTP should be send to user via phone call
     */
    private fun requestGenerateCode(otpType: String) {
        Dialogs.INSTANCE.showLoader(this@RideCodeVerificationActivity)
        jobsRepository.requestOtpGenerate(Utils.phoneNumberForServer(mobileNumber), otpType, object : JobsDataSource.OtpGenerateCallback {
            override fun onSuccess(verifyNumberResponse: com.bykea.pk.partner.dal.source.remote.response.VerifyNumberResponse) {
                Dialogs.INSTANCE.dismissDialog()
            }

            override fun onFail(code: Int, subCode: Int?, message: String?) {
                Dialogs.INSTANCE.dismissDialog()
                displayErrorToast(code, subCode, message)
            }
        })
    }
    //endregion

    /**
     * Display Error Toast
     * @param code : Server Code
     * @param subCode : Server Sub Code
     * @param message : Error Message For Toast
     */
    private fun displayErrorToast(code: Int, subCode: Int?, message: String?) {
        if (subCode != null) {
            when (subCode) {
                SUB_CODE_1009 -> Utils.appToast(message)
                SUB_CODE_1019 -> Utils.appToast(message)
                SUB_CODE_1028 -> Utils.appToast(message)
                SUB_CODE_1051 -> Utils.appToast(message)
                SUB_CODE_1052 -> Utils.appToast(SUB_CODE_1052_MSG)
                SUB_CODE_1053 -> linLayoutOtpWrongEntered.visibility = View.VISIBLE
                SUB_CODE_1054 -> Utils.appToast(SUB_CODE_1054_MSG)
                SUB_CODE_1055 -> {
                    Utils.appToast(SUB_CODE_1055_MSG)
                    finish()
                }
                else -> Utils.appToast(getString(R.string.error_try_again))
            }
        } else {
            if ((!message.isNullOrEmpty() && StringUtils.containsIgnoreCase(message, getString(R.string.invalid_code_error_message))) ||
                    code.equals(Constants.ApiError.BUSINESS_LOGIC_ERROR)) {
                linLayoutOtpWrongEntered.visibility = View.VISIBLE
            } else {
                Utils.appToast(getString(R.string.error_try_again))
            }
        }
    }
}