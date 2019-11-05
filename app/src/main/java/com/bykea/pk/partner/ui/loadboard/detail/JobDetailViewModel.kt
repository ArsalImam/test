package com.bykea.pk.partner.ui.loadboard.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.DriverApp
import com.bykea.pk.partner.R
import com.bykea.pk.partner.analytics.AnalyticsEventsJsonObjects
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.ui.common.Event
import com.bykea.pk.partner.ui.helpers.AppPreferences
import com.bykea.pk.partner.utils.Constants
import com.bykea.pk.partner.utils.Utils
import com.google.android.gms.maps.model.LatLng

/**
 * The ViewModel used in [JobDetailActivity].
 *
 * @Author: Yousuf Sohail
 */
class JobDetailViewModel(private val jobsRepository: JobsRepository) : ViewModel(), JobsDataSource.GetJobRequestCallback, JobsDataSource.AcceptJobRequestCallback {

    private val _currentLatLng = MutableLiveData<LatLng>()
    val currentLatLng: LiveData<LatLng>
        get() = _currentLatLng

    private val _job = MutableLiveData<Job>()
    val job: LiveData<Job>
        get() = _job

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean>
        get() = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _acceptBookingCommand = MutableLiveData<Event<Unit>>()
    val acceptBookingCommand: LiveData<Event<Unit>>
        get() = _acceptBookingCommand

    private val _bookingTakenCommand = MutableLiveData<Event<Unit>>()
    val bookingTakenCommand: LiveData<Event<Unit>>
        get() = _bookingTakenCommand

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val jobId: Long?
        get() = _job.value?.id


    /**
     * Start the ViewModel by fetching the [Job] id
     *
     * @param jobId [Job.id]
     */
    fun start(jobId: Long) {
        _dataLoading.value = true
        jobsRepository.getJob(jobId, this)
        _currentLatLng.value = LatLng(AppPref.getLat(jobsRepository.pref), AppPref.getLng(jobsRepository.pref))
    }

    /**
     * Accept current booking
     *
     */
    fun accept() {
        job.value?.let {
            jobsRepository.pickJob(it, this)
        }
    }

    /**
     * Refresh by loading the [Job] details all over again
     */
    fun onRefresh() {
        jobId?.let { start(it) }
    }

    /**
     * Set hold [Job] to be shown
     *
     * @param job Updated [Job] object
     */
    private fun renderDetails(job: Job?) {
        this._job.value = job
        _isDataAvailable.value = job != null
    }

    override fun onJobLoaded(job: Job) {
        renderDetails(job)
        _dataLoading.value = false

        if (job.isComplete)
            Utils.logEvent(DriverApp.getContext(), AppPreferences.getDriverId(),
                    Constants.AnalyticsEvents.ON_LB_BOOKING_DETAIL,
                    AnalyticsEventsJsonObjects.getEventLoadBoardJson(Constants.AnalyticsEvents.ON_LB_BOOKING_DETAIL, job),
                    true)
    }

    override fun onDataNotAvailable(message: String?) {
        _job.value = null
        _dataLoading.value = false
        _isDataAvailable.value = false
    }

    override fun onJobRequestAccepted() {
        _acceptBookingCommand.value = Event(Unit)
    }

    override fun onJobRequestAcceptFailed(code: Int, message: String?) {
        if (code == 422) {
            _bookingTakenCommand.value = Event(Unit)
        } else {
            showSnackbarMessage(R.string.error_try_again)
        }
    }

    /**
     * Show snackbar
     *
     * @param message String resource id to be shown in snackbar
     */
    fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }

}
