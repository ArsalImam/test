package com.bykea.pk.partner.ui.loadboard.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.JobsRepository
import com.bykea.pk.partner.ui.common.Event
import java.util.*

/**
 * The ViewModel for [BookingListFragment].
 *
 * @Author: Yousuf Sohail
 */
class JobListViewModel internal constructor(private val jobsRepository: JobsRepository) : ViewModel() {

    private val _items = MutableLiveData<List<Job>>().apply { value = emptyList() }
    val items: LiveData<List<Job>>
        get() = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _noBookingsLabel = MutableLiveData<Int>()
    val noBookingsLabel: LiveData<Int>
        get() = _noBookingsLabel

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    private val _openBookingEvent = MutableLiveData<Event<Long>>()
    val openBookingEvent: LiveData<Event<Long>>
        get() = _openBookingEvent

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    // This LiveData depends on another so we can use a transformation.
    val showArray: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    private var _isExpended = MutableLiveData<Boolean>()
    var isExpended: MutableLiveData<Boolean>
        get() = _isExpended
        set(value) {
            _isExpended = value
        }

    /**
     * Start the ViewModel by fetching the booking listing to be shown on loadboard
     *
     */
    fun start() {
        loadBookings()
        isExpended.value = false
    }

    /**
     * Refresh by loading the fresh booking listing
     *
     */
    fun refresh() {
        loadBookings()
    }

    /**
     * Open Booking Detail screen. Called by the [JobListAdapter].
     *
     * @param bookingId [Job] id
     */
    internal fun openBooking(bookingId: Long) {
        _openBookingEvent.value = Event(bookingId)
    }

    /**
     * Load Booking list from Repository
     */
    private fun loadBookings() {
        _dataLoading.value = true
        jobsRepository.getJobs(object : JobsDataSource.LoadJobsCallback {
            override fun onJobsLoaded(jobs: List<Job>) {
                _dataLoading.value = false
                jobs[0].service_code = 100
                _items.value = ArrayList(jobs)
            }

            override fun onDataNotAvailable(errorMsg: String?) {
                _dataLoading.value = false
            }
        })
    }
}
