package com.bykea.pk.partner.ui.loadboard.detail

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.R
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.dal.source.BookingsDataSource
import com.bykea.pk.partner.dal.source.BookingsRepository
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.ui.loadboard.common.Event
import com.google.android.gms.maps.model.LatLng

/**
 * The ViewModel used in [BookingDetailFragment].
 *
 * @Author: Yousuf Sohail
 */
class BookingDetailViewModel(private val bookingsRepository: BookingsRepository) : ViewModel(), BookingsDataSource.GetBookingCallback, BookingsDataSource.AcceptBookingCallback {

    private val _currentLatLng = MutableLiveData<LatLng>()
    val currentLatLng: LiveData<LatLng>
        get() = _currentLatLng

    private val _booking = MutableLiveData<Booking>()
    val booking: LiveData<Booking>
        get() = _booking

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
        _currentLatLng.value = LatLng(AppPref.getLat(bookingsRepository.pref), AppPref.getLng(bookingsRepository.pref))
    }

    /**
     * Accept current booking
     *
     */
    fun accept() {
        bookingId?.let {
            bookingsRepository.acceptBooking(it, this)
        }
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

    override fun onBookingAccepted() {
        _acceptBookingCommand.value = Event(Unit)
    }

    override fun onBookingAcceptFailed(message: String?, taken: Boolean) {
        if (taken) {
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
