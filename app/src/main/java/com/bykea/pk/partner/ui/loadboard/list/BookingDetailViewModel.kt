package com.bykea.pk.partner.ui.loadboard.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.local.Booking
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The ViewModel used in [BookingDetailFragment].
 *
 * @Author: Yousuf Sohail
 */
class BookingDetailViewModel(bookingRepository: BookingRepository, bookingId: Long) : ViewModel() {

    val booking: LiveData<Booking> = bookingRepository.getBooking(bookingId)

    /**
     * Cancel all coroutines when the ViewModel is cleared.
     */
    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
//        viewModelScope.cancel()
    }

}
