package com.bykea.pk.partner.loadboard

import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.source.BookingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The ViewModel used in [BookingDetailFragment].
 *
 * @Author: Yousuf Sohail
 */
class BookingDetailViewModel(bookingRepository: BookingsRepository) : ViewModel() {

//    val booking: Booking = bookingRepository.getBooking(bookingId)

    /**
     * Cancel all coroutines when the ViewModel is cleared.
     */
    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
//        viewModelScope.cancel()
    }

}
