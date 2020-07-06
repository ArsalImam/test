package com.bykea.pk.partner.ui.calling

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.analytics.Aog
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.Stop
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.socket.payload.JobCall
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.models.response.AcceptCallResponse
import com.bykea.pk.partner.models.response.FreeDriverResponse
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.models.response.RejectCallResponse
import com.bykea.pk.partner.repositories.UserDataHandler
import com.bykea.pk.partner.repositories.UserRepository
import com.bykea.pk.partner.ui.activities.BaseActivity
import com.bykea.pk.partner.ui.common.Event
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.*
import com.bykea.pk.partner.utils.Constants.*
import com.bykea.pk.partner.utils.Constants.ApiError.BUSINESS_LOGIC_ERROR
import com.bykea.pk.partner.utils.Constants.ServiceCode.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_job_call.*
import org.apache.commons.lang3.StringUtils
import org.greenrobot.eventbus.Subscribe

/**
 * Job call activity to be shown when BOOKING_REQUEST socket or push is recieved.
 *
 * @author Yousuf Sohail
 */
class JobCallActivity : BaseActivity() {

    private lateinit var jobCall: JobCall
    private lateinit var timer: CountDownTimer
    private lateinit var _mpSound: MediaPlayer

    private val DEFAULT_CALL_TIMEOUT = 20 //seconds
    private var callTimeOut = DEFAULT_CALL_TIMEOUT
    private var secondsEclipsed: Float = DIGIT_ZERO.toFloat()
    private var isCancelledByPassenger: Boolean = false
    private lateinit var jobsRepo: JobsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_call)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        jobsRepo = Injection.provideJobsRepository(application.applicationContext)

        intent?.let {
            jobCall = intent.getSerializableExtra(KEY_CALL_DATA) as JobCall
            callTimeOut = jobCall.timer

            if (intent.getBooleanExtra(KEY_IS_FROM_PUSH, false)) {
                DriverApp.getApplication().connect()
                DriverApp.startLocationService(this)
            }
        }

        AppPreferences.removeReceivedMessageCount()
        AppPreferences.setTopUpPassengerWalletAllowed(true)
        Utils.unlockScreen(this)
        AppPreferences.setStatsApiCallRequired(true)
        //To inactive driver during passenger calling state
        AppPreferences.setTripStatus(TripStatus.ON_IN_PROGRESS)

        if (Utils.isConnected(this@JobCallActivity, false))
            UserRepository().requestLocationUpdate(this, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude())

        init()
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        AppPreferences.setCallingActivityOnForeground(true)
    }

    override fun onStop() {
        super.onStop()
        AppPreferences.setCallingActivityOnForeground(false)
    }

    override fun onDestroy() {
        stopSound()
        if (AppPreferences.isOnTrip()) {
            AppPreferences.setIncomingCall(false)
        } else {
            AppPreferences.setIncomingCall(true)
        }
        AppPreferences.setCallingActivityOnForeground(false)
        Utils.unbindDrawables(activity_calling)
        super.onDestroy()
    }

    @Subscribe
    fun onEvent(intent: Intent?) {
        isCancelledByPassenger = true
        this@JobCallActivity.runOnUiThread {
            if (null != intent && null != intent.extras) {
                if (intent.getStringExtra(KEY_ACTION).equals(Keys.BROADCAST_CANCEL_RIDE, ignoreCase = true) || intent.getStringExtra(KEY_ACTION).equals(Keys.BROADCAST_CANCEL_BY_ADMIN, ignoreCase = true)) {
                    onJobCallCancelled()
                }
            }
        }
    }

    /**
     * Finish current screen
     */
    private fun finishActivity() {
        if (Utils.isConnected(this@JobCallActivity, false))
            UserRepository().requestLocationUpdate(this, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude())
        finish()
    }

    /**
     * Start timer with dynamic timing, also play the calling sound
     */
    private fun startTimer() {

        _mpSound = MediaPlayer.create(this, R.raw.ringtone)
        _mpSound.start()

        donut_progress.max = callTimeOut * DIGIT_TEN
        donut_progress.progress = callTimeOut * DIGIT_TEN
        secondsEclipsed = callTimeOut.toFloat()
        timer = object : CountDownTimer((callTimeOut * DIGIT_THOUSAND).toLong(), DIGIT_HUNDRED.toLong()) {

            override fun onTick(millisUntilFinished: Long) {
                if (secondsEclipsed.toInt() > DIGIT_ZERO) {
                    donut_progress.progress = (millisUntilFinished / (DIGIT_HUNDRED)).toInt()
                    counterTv.text = secondsEclipsed.toInt().toString()
                    secondsEclipsed -= 0.1f
                } else {
                    timer.onFinish()

                }
            }

            override fun onFinish() {
                donut_progress.progress = DIGIT_ZERO
                counterTv.text = "$DIGIT_ZERO"
                acceptCallBtn.isEnabled = false
                stopSound()
                Utils.setCallIncomingStateWithoutRestartingService()
                ActivityStackManager.getInstance().startHomeActivity(true, this@JobCallActivity)
                finishActivity()
            }
        }
        timer.start()
    }

    /**
     * Stop calling sound
     */
    private fun stopSound() {
        if (_mpSound.isPlaying) _mpSound.stop()
        timer.cancel()
    }

    /**
     * Render job calling data
     */
    private fun init() {
        Utils.redLog(TAG, "Call Data: " + Gson().toJson(jobCall))
        Aog.onJobCallAndJobAccept(jobCall, false, secondsEclipsed.toInt())
        counterTv.text = callTimeOut.toString()

        /*ivCallType.setImageDrawable(ContextCompat.getDrawable(this, getJobImage(jobCall.service_code)))*/
        estArrivalTimeTV.text = getArrivalTime(jobCall.pickup)
        estArrivalDistanceTV.text = getPickUpDistance(jobCall.pickup)
        dropZoneNameTV.text = getDropOffZoneName(jobCall.dropoff)
        estDistanceTV.text = getDropOffDistance(jobCall.dropoff)
        dropAddressTV.text = getDropOffAddress(jobCall.dropoff)

        skipJobiV.setOnClickListener {
            runOnUiThread {
                AppPreferences.setIncomingCall(false)
                AppPreferences.setTripStatus(TripStatus.ON_FREE)
                stopSound()
                skipJob()
                timer.cancel()
                finishActivity()
            }
        }

        acceptCallBtn.setOnClickListener {
            stopSound()
            Dialogs.INSTANCE.showLoader(this@JobCallActivity)
            if (jobCall.dispatch == null || !jobCall.dispatch!!) {
                acceptJob()
            } else {
                assignJobFromLoadboard()
            }
            timer.cancel()
        }
    }

    /**
     * Get job service image
     *
     * @param service_code Job service code
     * @return Drawable ID
     */
    private fun getJobImage(service_code: Int): Int {
        return if (service_code == RIDE)
            R.drawable.ride_right
        else if (service_code == SEND || service_code == SEND_COD)
            R.drawable.bhejdo_no_caption
        else
            R.drawable.ride_right
    }

    /**
     * Get arrival time from current location to pickup
     *
     * @param pickup Pickup stop
     * @return Displayable time in minutes
     */
    private fun getArrivalTime(pickup: Stop?): String {
        return if (pickup != null && pickup.duration > DIGIT_ZERO)
            (pickup.duration / DIGIT_SIXTY).toString() + StringUtils.SPACE + getString(R.string.min)
        else
            "$DIGIT_ONE"
    }

    /**
     * Get the drop-off zone name to show
     *
     * @param dropoff Drop-off stop
     * @return Displayable zone name
     */
    private fun getDropOffZoneName(dropoff: Stop?): String {
        return if (dropoff != null)
            when {
                !dropoff.zone_ur.isNullOrEmpty() -> dropoff.zone_ur!!
                !dropoff.zone_en.isNullOrEmpty() -> dropoff.zone_en!!
                !dropoff.address.isNullOrEmpty() -> dropoff.address!!
                else -> getString(R.string.customer_btayega)
            } else getString(R.string.customer_btayega)
    }

    /**
     * Get the pickup distance from partner current location
     *
     * @param pickup Drop-off stop
     * @return Displayable distance in kilometer
     */
    private fun getPickUpDistance(pickup: Stop?): String {
        return if (pickup != null && pickup.distance != DIGIT_ZERO)
            (pickup.distance / DIGIT_THOUSAND).toString() + StringUtils.SPACE + getString(R.string.distanceUnit).toString().toLowerCase()
        else
            getString(R.string.question_mark) + StringUtils.SPACE + getString(R.string.distanceUnit).toString().toLowerCase()
    }

    /**
     * Get the drop-off distance from pickup stop
     *
     * @param dropoff Drop-off stop
     * @return Displayable distance in kilometer
     */
    private fun getDropOffDistance(dropoff: Stop?): String {
        return if (dropoff != null && dropoff.distance != DIGIT_ZERO)
            (dropoff.distance / DIGIT_THOUSAND).toString() + StringUtils.SPACE + getString(R.string.distanceUnit).toString().toLowerCase()
        else
            getString(R.string.question_mark) + StringUtils.SPACE + getString(R.string.distanceUnit).toString().toLowerCase()
    }

    /**
     * Get the drop-off address
     *
     * @param dropoff Drop-off stop
     * @return Displayable drop off address
     */
    private fun getDropOffAddress(dropoff: Stop?): String {
        return if (dropoff != null && !dropoff.address.isNullOrEmpty())
            return dropoff.address.toString();
        else
            StringUtils.EMPTY
    }

    /**
     * Inform server to accept the job request
     */
    private fun acceptJob() {
        jobsRepo.acceptJob(jobCall.trip_id, secondsEclipsed.toInt(), object : JobsDataSource.AcceptJobCallback {
            override fun onJobAccepted() {
                Dialogs.INSTANCE.dismissDialog()
                if (!isCancelledByPassenger) {
                    onAcceptSuccess(true, "Job Accepted")
                } else {
                    onJobCallCancelled()
                }
            }

            override fun onJobAcceptFailed() {
                Dialogs.INSTANCE.dismissDialog()
                onAcceptFailed("Job Accept Failed")
            }
        })
    }

    /**
     * Inform server to assign job from loadboard
     */
    private fun assignJobFromLoadboard() {
        val job = Job(jobCall.booking_id!!)
        jobsRepo.pickJob(job, true,object : JobsDataSource.AcceptJobRequestCallback {
            override fun onJobRequestAccepted() {
                Dialogs.INSTANCE.dismissDialog()
                AppPreferences.clearTripDistanceData()
                AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL)
                AppPreferences.setTripAcceptTime(System.currentTimeMillis())
                Aog.onJobCallAndJobAccept(jobCall, true, secondsEclipsed.toInt())
                AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude())
                AppPreferences.setIsOnTrip(true)
                AppPreferences.setDeliveryType(CallType.SINGLE)

                val callData = NormalCallData()
                callData.status = TripStatus.ON_ACCEPT_CALL
                callData.tripNo = jobCall.booking_no
                AppPreferences.setCallData(callData)

                ActivityStackManager.getInstance().startJobActivity(this@JobCallActivity)
                stopSound()
                finishActivity()
            }

            override fun onJobRequestAcceptFailed(code: Int, subCode: Int?, message: String?) {
                Dialogs.INSTANCE.dismissDialog()
                message?.let { onAcceptFailed(it) } ?: run { onAcceptFailed("On Accept Failed") }
            }
        })
    }

    /**
     * Acknowledge server to skip the job request
     */
    private fun skipJob() {
        jobsRepo.skipJob(jobCall.trip_id, object : JobsDataSource.SkipJobCallback {
            override fun onJobSkip() {
                //NOTHING TO HANDLE BECAUSE, ACTIVITY HAS BEEN DESTROYED
            }

            override fun onJobSkipFailed() {
                //NOTHING TO HANDLE BECAUSE, ACTIVITY HAS BEEN DESTROYED
            }
        })
    }

    /**
     * On success of job call accept
     *
     * @param success Success status
     * @param message Success message
     */
    private fun onAcceptSuccess(success: Boolean, message: String) {
        runOnUiThread {
            Dialogs.INSTANCE.showTempToast(message)
            if (!isCancelledByPassenger) {
                if (success) {
                    AppPreferences.clearTripDistanceData()
                    AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL)
                    AppPreferences.setTripAcceptTime(System.currentTimeMillis())
                    Aog.onJobCallAndJobAccept(jobCall, true, secondsEclipsed.toInt())
                    AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude())
                    AppPreferences.setIsOnTrip(true)
                    AppPreferences.setDeliveryType(CallType.SINGLE)

                    val callData = NormalCallData()
                    callData.status = TripStatus.ON_ACCEPT_CALL
                    callData.tripNo = jobCall.booking_no
                    AppPreferences.setCallData(callData)

                    ActivityStackManager.getInstance().startJobActivity(this@JobCallActivity)
                    stopSound()
                    finishActivity()
                } else {
                    Utils.setCallIncomingState()
                    Dialogs.INSTANCE.showToast(message)
                }
            } else {
                onJobCallCancelled()
            }
        }

    }

    /**
     * On failure of accept call
     *
     * @param message Failure message
     */
    private fun onAcceptFailed(message: String) {
        runOnUiThread {
            Dialogs.INSTANCE.showToast(message)
            if (AppPreferences.isOnTrip()) {
                AppPreferences.setIncomingCall(false)
                AppPreferences.setTripStatus(TripStatus.ON_FREE)
            } else {
                AppPreferences.setIncomingCall(true)
            }
            ActivityStackManager.getInstance().startHomeActivity(true, this@JobCallActivity)
            stopSound()
            finishActivity()
        }
    }

    /**
     * On job call cancelled during job calling
     */
    private fun onJobCallCancelled() {
        Utils.setCallIncomingState()
        AppPreferences.setTripStatus(TripStatus.ON_FREE)
        stopSound()
        ActivityStackManager.getInstance().startHomeActivityFromCancelTrip(false, this)
        finishActivity()
    }

    /**
     * Response handler for rest calls
     */
    private val handler = object : UserDataHandler() {

        override fun onAck(msg: String) {
            runOnUiThread { Utils.appToastDebug(msg) }
        }

        override fun onFreeDriver(freeDriverResponse: FreeDriverResponse) {
            this@JobCallActivity.runOnUiThread {
                ActivityStackManager.getInstance().startHomeActivity(true, this@JobCallActivity)
                stopSound()
                finishActivity()
            }

        }

        override fun onAcceptCall(acceptCallResponse: AcceptCallResponse) {
            onAcceptSuccess(acceptCallResponse.isSuccess, acceptCallResponse.message)
        }

        override fun onRejectCall(rejectCallResponse: RejectCallResponse) {
            onAcceptFailed(rejectCallResponse.message)
        }

        override fun onError(errorCode: Int, errorMessage: String) {
            runOnUiThread {
                Dialogs.INSTANCE.dismissDialog()
                Dialogs.INSTANCE.showToast(errorMessage)
                ActivityStackManager.getInstance().startHomeActivity(true, this@JobCallActivity)
                stopSound()
                finishActivity()
            }
        }
    }

    companion object {
        var TAG: String = JobCallActivity::class.java.simpleName
        var KEY_ACTION = "action"
        var KEY_CALL_DATA = "KEY_CALL_DATA"
        var KEY_IS_FROM_PUSH = "isGcm"
    }
}
