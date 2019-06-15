package com.bykea.pk.partner.ui.loadboard.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.dal.source.BookingsDataSource
import com.bykea.pk.partner.dal.source.BookingsRepository
import com.bykea.pk.partner.ui.loadboard.common.Event

/**
 * The ViewModel used in [BookingDetailFragment].
 *
 * @Author: Yousuf Sohail
 */
class BookingDetailViewModel(private val bookingsRepository: BookingsRepository) : ViewModel(), BookingsDataSource.GetBookingCallback {

    private val _booking = MutableLiveData<Booking>()
    val booking: LiveData<Booking>
        get() = _booking

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean>
        get() = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _editBookingCommand = MutableLiveData<Event<Unit>>()
    val editBookingCommand: LiveData<Event<Unit>>
        get() = _editBookingCommand

    private val _deleteBookingCommand = MutableLiveData<Event<Unit>>()
    val deleteBookingCommand: LiveData<Event<Unit>>
        get() = _deleteBookingCommand

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    val bookingId: Long?
        get() = _booking.value?.id


    /**
     * Start the ViewModel by fetching the [Booking] id
     *
     * @param bookingId [Booking.id]
     */
    fun start(bookingId: Long) {
        _dataLoading.value = true
        bookingsRepository.getBooking(bookingId, this)
    }

    /**
     * Refresh by loading the [Booking] details all over again
     */
    fun onRefresh() {
        bookingId?.let { start(it) }
    }

    /**
     * Set hold [Booking] to be shown
     *
     * @param booking Updated [Booking] object
     */
    private fun setBooking(booking: Booking?) {
        this._booking.value = booking
        _isDataAvailable.value = booking != null
    }

    override fun onBookingLoaded(booking: Booking) {
        setBooking(booking)
        _dataLoading.value = false
    }

    override fun onDataNotAvailable(message: String?) {
        _booking.value = null
        _dataLoading.value = false
        _isDataAvailable.value = false
    }

    /**
     * Show snackbar
     *
     * @param message String resource id to be shown in snackbar
     */
    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }

}
