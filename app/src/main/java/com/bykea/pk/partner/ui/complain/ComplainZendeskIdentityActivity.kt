package com.bykea.pk.partner.ui.complain

import android.os.Bundle
import android.os.CountDownTimer
import androidx.databinding.DataBindingUtil
import com.bykea.pk.partner.R
import com.bykea.pk.partner.databinding.ActivityComplainZendeskIdentityBinding
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import kotlinx.android.synthetic.main.activity_complain_zendesk_identity.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.util.*

class ComplainZendeskIdentityActivity : BaseActivity() {
    private var mProgress: Int = 0
    private var countDownTimer: CountDownTimer? = null
    private var counter = 0
    private var totalTime = 0
    private var waitingTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityComplainZendeskIdentityBinding = DataBindingUtil.setContentView(this, R.layout.activity_complain_zendesk_identity)

        tvTitle.text = resources.getString(R.string.wait_for_zendesk)
        waitingTime = Constants.ZendeskConfigurations.ZENDESK_SETTING_IDENTITY_MAX_TIME - (Date().time - AppPreferences.getZendeskSDKSetupTime().time)
        ivBackBtn.setOnClickListener { onBackPressed() }

        donut_progress.setMax((waitingTime / 100).toInt())

        animateDonutProgress()
    }


    /**
     * This method starts count down Progress animation
     */
    private fun animateDonutProgress() {
        clearTimer()

        mProgress = 0
        counter = 0
        totalTime = (waitingTime / 1000).toInt()
        counterTv.text = totalTime.toString()

        setupCountDownTimer()
        startTimer()
    }

    /***
     * Setup count down countDownTimer for mProgress bar for OTP
     */
    private fun setupCountDownTimer() {
        countDownTimer = object : CountDownTimer(waitingTime,
                Constants.ZendeskConfigurations.ZENDESK_SETTING_IDENTITY_INTERVAL_TIME) {
            override fun onTick(millisUntilFinished: Long) {
                donut_progress.setProgress(mProgress++.toFloat())
                counter++
                if (counter == 10) {
                    counter = 0
                    totalTime--
                    counterTv.setText(totalTime.toString())
                }

            }

            override fun onFinish() {
                totalTime = 0
                counterTv.setText(R.string.digit_zero)
                donut_progress.setProgress((waitingTime / 100).toInt().toFloat())
                AppPreferences.setZendeskSDKReady()
                onBackPressed()
            }
        }
    }

    /**
     * Start count down countDownTimer
     */
    private fun startTimer() {
        if (countDownTimer != null) {
            countDownTimer?.start()
        }
    }

    /***
     * Clear count down countDownTimer
     */
    private fun clearTimer() {
        if (countDownTimer != null) {
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearTimer()
    }
}
