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
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Dialogs
import com.bykea.pk.partner.utils.NumericKeyBoardTransformationMethod
import com.bykea.pk.partner.utils.Utils
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

        if (intent != null && intent.extras != null) {
            if (intent.extras.containsKey(Constants.Extras.PHONE_NUMBER)) {
                mobileNumber = intent.extras.getString(Constants.Extras.PHONE_NUMBER)
                titleMsg.text = mobileNumber
            }
            if (intent.extras.containsKey(Constants.Extras.RIDE_CREATE_DATA)) {
                rideCreateRequestObject = intent.extras.getParcelable(Constants.Extras.RIDE_CREATE_DATA)
            }
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
                donutProgress.progress = (Constants.VERIFICATION_WAIT_MAX_TIME / 100).toInt().toFloat()

                llBottom.visibility = View.VISIBLE
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
        linLayoutOtpWrongEntered.visibility = View.GONE
        if (Utils.isConnected(this@RideCodeVerificationActivity, true)) {
            Dialogs.INSTANCE.showLoader(this@RideCodeVerificationActivity)
            animateDonutProgress()
            requestGenerateCode()
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
    private fun requestGenerateCode() {
        Dialogs.INSTANCE.showLoader(this@RideCodeVerificationActivity)
        jobsRepository.requestOtpGenerate(Utils.phoneNumberForServer(mobileNumber), OTP_SMS, object : JobsDataSource.OtpGenerateCallback {
            override fun onSuccess(verifyNumberResponse: com.bykea.pk.partner.dal.source.remote.response.VerifyNumberResponse) {
                Dialogs.INSTANCE.dismissDialog()
            }

            override fun onFail(code: Int, message: String?) {
                Dialogs.INSTANCE.dismissDialog()
                displayErrorToast(code, message)
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

    /**
     * Display Error Toast
     * @param code : Server Code
     * @param message : Error Message For Toast
     */
    private fun displayErrorToast(code: Int, message: String?) {
        if ((!message.isNullOrEmpty() && StringUtils.containsIgnoreCase(message, getString(R.string.invalid_code_error_message))) ||
                code.equals(Constants.ApiError.BUSINESS_LOGIC_ERROR)) {
            linLayoutOtpWrongEntered.visibility = View.VISIBLE
        } else {
            Utils.appToast(getString(R.string.error_try_again))
        }

    }
}