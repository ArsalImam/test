package com.bykea.pk.partner.ui.loadboard.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.dal.source.BookingsDataSource
import com.bykea.pk.partner.dal.source.BookingsRepository
import com.bykea.pk.partner.ui.loadboard.common.Event
import java.util.*

/**
 * The ViewModel for [BookingListFragment].
 *
 * @Author: Yousuf Sohail
 */
class BookingListViewModel internal constructor(private val bookingsRepository: BookingsRepository) : ViewModel() {

    private val _items = MutableLiveData<List<Booking>>().apply { value = emptyList() }
    val items: LiveData<List<Booking>>
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

    fun start() {
        loadBookings(false)
    }

    fun refresh() {
        loadBookings(true)
    }

    private fun loadBookings(forceUpdate: Boolean) {
        loadBookings(forceUpdate, true)
    }

    /**
     * Called by the [BookingsAdapter].
     */
    internal fun openBooking(bookingId: Long) {
        _openBookingEvent.value = Event(bookingId)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [BookingsDataSource]
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadBookings(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            _dataLoading.setValue(true)
        }
        if (forceUpdate) {
            bookingsRepository.refreshBookings()
        }

        bookingsRepository.getBookings(object : BookingsDataSource.LoadBookingsCallback {
            override fun onBookingsLoaded(bookings: List<Booking>) {
                if (showLoadingUI) _dataLoading.value = false
                _items.value = ArrayList(bookings)
            }

            override fun onDataNotAvailable(errorMsg: String?) {
                //TODO: Show error if needed
            }
        })
    }
}
