package com.bykea.pk.partner.ui.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import butterknife.ButterKnife
import butterknife.OnClick
import com.bykea.pk.partner.BuildConfig
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.Stop
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.socket.payload.JobCall
import com.bykea.pk.partner.dal.util.Injection
import com.bykea.pk.partner.models.response.AcceptCallResponse
import com.bykea.pk.partner.models.response.FreeDriverResponse
import com.bykea.pk.partner.models.response.RejectCallResponse
import com.bykea.pk.partner.repositories.UserDataHandler
import com.bykea.pk.partner.repositories.UserRepository
import com.bykea.pk.partner.ui.helpers.ActivityStackManager
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.*
import com.bykea.pk.partner.utils.Constants.ServiceType.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_job_call.*
import org.greenrobot.eventbus.Subscribe
import org.json.JSONException
import org.json.JSONObject


class JobCallActivity : BaseActivity() {

    private var _mpSound: MediaPlayer? = null
    private var tripId: String? = null
    private var isCancelledByPassenger: Boolean = false
    private var mLastClickTime: Long = 0
    private val RIDE_ACCEPTANCE_TIMEOUT = 20
    internal var timerDuration = RIDE_ACCEPTANCE_TIMEOUT
    private var acceptSeconds = "0"
    private var jobCall: JobCall? = null
    private var userRepo: UserRepository? = null
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_call)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        ButterKnife.bind(this)

        if (intent != null) {
            jobCall = intent.getSerializableExtra(KEY_CALL_DATA) as JobCall
            timerDuration = jobCall!!.timer

            if (intent.getBooleanExtra(KEY_IS_FROM_PUSH, false)) {
                DriverApp.getApplication().connect()
                DriverApp.startLocationService(this)
            }
        }

        userRepo = UserRepository()
        Utils.unlockScreen(this)
        AppPreferences.setStatsApiCallRequired(true)
        //To inactive driver during passenger calling state
        AppPreferences.setTripStatus(TripStatus.ON_IN_PROGRESS)

        if (Utils.isConnected(this@JobCallActivity, false))
            userRepo!!.requestLocationUpdate(this, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude())


        setInitialData()
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

    @OnClick(R.id.acceptCallBtn)
    fun onClick(view: View) {
        when (view.id) {
            R.id.acceptCallBtn -> {
                if (mLastClickTime != 0L && SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime()
                stopSound()
                Dialogs.INSTANCE.showLoader(this@JobCallActivity)
                acceptSeconds = counterTv!!.text.toString()
                acceptJob()
                timer!!.cancel()
            }
        }
    }

    @Subscribe
    fun onEvent(intent: Intent?) {
        isCancelledByPassenger = true
        this@JobCallActivity.runOnUiThread {
            if (null != intent && null != intent.extras) {
                if (intent.getStringExtra("action").equals(Keys.BROADCAST_CANCEL_RIDE, ignoreCase = true) || intent.getStringExtra("action").equals(Keys.BROADCAST_CANCEL_BY_ADMIN, ignoreCase = true)) {
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
            userRepo!!.requestLocationUpdate(this, handler, AppPreferences.getLatitude(), AppPreferences.getLongitude())
        finish()
    }

    /**
     * Log call data to mix panel
     *
     * @param callData   Job call data
     * @param isOnAccept is post accepted
     */
    private fun logMixPanelEvent(callData: JobCall, isOnAccept: Boolean) {
        try {

            val data = JSONObject()
            data.put("PassengerID", callData.customer_id)
            data.put("DriverID", AppPreferences.getPilotData().id)
            data.put("TripID", callData.trip_id)
            data.put("TripNo", callData.booking_no)
            data.put("PickUpLocation", callData.pickup.lat.toString() + "," + callData.pickup.lng)
            data.put("timestamp", Utils.getIsoDate())
            if (callData.dropoff != null) {
                data.put("DropOffLocation", callData.dropoff!!.lat.toString() + "," + callData.dropoff!!.lng)
            }
            data.put("ETA", "" + callData.pickup.duration)
            data.put("EstimatedDistance", AppPreferences.getEstimatedDistance())
            data.put("CurrentLocation", Utils.getCurrentLocation())
            data.put("DriverName", AppPreferences.getPilotData().fullName)
            data.put("SignUpCity", AppPreferences.getPilotData().city.name)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * Start timer with dynamic timing, also play the calling sound
     */
    private fun startTimer() {

        _mpSound = MediaPlayer.create(this, R.raw.ringtone)
        _mpSound!!.start()

        donut_progress!!.max = timerDuration
        donut_progress!!.progress = 0f

        timer = object : CountDownTimer((timerDuration * 1000).toLong(), 1000) {
            internal var progress = 0

            override fun onTick(millisUntilFinished: Long) {
                progress++
                if (progress < timerDuration) {
                    donut_progress!!.progress = progress.toFloat()
                    counterTv!!.text = (timerDuration - progress).toString()
                } else {
                    timer!!.onFinish()
                }
            }

            override fun onFinish() {
                donut_progress!!.progress = 0f
                counterTv!!.text = "0"
                acceptCallBtn!!.isEnabled = false
                stopSound()
                Utils.setCallIncomingStateWithoutRestartingService()
                ActivityStackManager.getInstance().startHomeActivity(true, this@JobCallActivity)
                finishActivity()
            }
        }
        timer!!.start()
    }

    /**
     * Stop calling sound
     */
    private fun stopSound() {
        if (null != _mpSound && _mpSound!!.isPlaying) {
            _mpSound!!.stop()
        }
        if (null != timer) timer!!.cancel()
    }

    /**
     * Render job calling data
     */
    private fun setInitialData() {
        tripId = jobCall!!.trip_id
        Utils.redLog(TAG, "Call Data: " + Gson().toJson(jobCall))
        logMixPanelEvent(jobCall!!, false)
        counterTv!!.text = timerDuration.toString()

        ivCallType!!.setImageDrawable(ContextCompat.getDrawable(this, getJobImage(jobCall!!.service_code)))
        estArrivalTimeTV!!.text = getArrivalTime(jobCall!!.pickup)
        dropZoneNameTV!!.text = getDropOffZoneName(jobCall!!.dropoff) + ""
        estDistanceTV!!.text = getDropOffDistance(jobCall!!.dropoff)
    }

    /**
     * Get job service image
     *
     * @param service_code Job service code
     * @return Drawable ID
     */
    private fun getJobImage(service_code: Int): Int {
        return if (service_code == RIDE_CODE)
            R.drawable.ride
        else if (service_code == SEND_CODE || service_code == SEND_COD_CODE)
            R.drawable.bhejdo_no_caption
        else
            R.drawable.ride
    }

    /**
     * Get arrival time from current location to pickup
     *
     * @param pickup Pickup stop
     * @return Displayable time in minutes
     */
    private fun getArrivalTime(pickup: Stop?): String {
        return if (pickup != null && pickup.duration > 0)
            (pickup.duration / 60).toString()
        else
            "1"
    }

    /**
     * Get the drop-off zone name to show
     *
     * @param dropoff Drop-off stop
     * @return Displayable zone name
     */
    private fun getDropOffZoneName(dropoff: Stop?): String? {
        return if (dropoff != null) {
            if (dropoff.zone_ur != null && !dropoff.zone_ur!!.isEmpty()) {
                dropoff.zone_ur
            } else if (dropoff.zone_en != null && !dropoff.zone_en!!.isEmpty())
                dropoff.zone_en
            else if (dropoff.address != null && !dropoff.address!!.isEmpty())
                dropoff.address
            else
                getString(R.string.customer_btayega)
        } else
            getString(R.string.customer_btayega)
    }

    /**
     * Get the drop-off distance from pickup stop
     *
     * @param dropoff Drop-off stop
     * @return Displayable distance in kilometer
     */
    private fun getDropOffDistance(dropoff: Stop?): String {
        return if (dropoff != null && dropoff.distance != 0)
            (dropoff.distance / 1000).toString()
        else
            "?"
    }

    /**
     * Inform server to accept the job request
     */
    private fun acceptJob() {
        val jobsRepo = Injection.provideJobsRepository(application.applicationContext)
        jobsRepo.acceptJob(tripId!!, Integer.valueOf(acceptSeconds), object : JobsDataSource.AcceptJobCallback {
            override fun onJobAccepted() {
                if (!isCancelledByPassenger) {
                    onAcceptSuccess(true, "Job Accepted")
                } else {
                    onJobCallCancelled()
                }
                if (BuildConfig.DEBUG)
                    Toast.makeText(application.applicationContext, "Job Accepted", Toast.LENGTH_SHORT).show()
            }

            override fun onJobAcceptFailed() {
                onAcceptFailed("Job Accept Failed")
                if (BuildConfig.DEBUG)
                    Toast.makeText(application.applicationContext, "Job Accept Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * On success of job call accept
     *
     * @param success Success status
     * @param message Success message
     */
    private fun onAcceptSuccess(success: Boolean?, message: String) {
        runOnUiThread {
            Dialogs.INSTANCE.dismissDialog()
            Dialogs.INSTANCE.showTempToast(message)
            if (!isCancelledByPassenger) {
                if (success!!) {
                    AppPreferences.clearTripDistanceData()
                    AppPreferences.setTripStatus(TripStatus.ON_ACCEPT_CALL)
                    AppPreferences.setTripAcceptTime(System.currentTimeMillis())
                    logMixPanelEvent(jobCall!!, true)
                    AppPreferences.addLocCoordinateInTrip(AppPreferences.getLatitude(), AppPreferences.getLongitude())
                    AppPreferences.setIsOnTrip(true)
                    AppPreferences.setDeliveryType(Constants.CallType.SINGLE)
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
            Dialogs.INSTANCE.dismissDialog()
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
            runOnUiThread { Utils.appToastDebug(this@JobCallActivity, msg) }
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
        var KEY_CALL_DATA = "KEY_CALL_DATA"
        var KEY_IS_FROM_PUSH = "isGcm"
    }
}
