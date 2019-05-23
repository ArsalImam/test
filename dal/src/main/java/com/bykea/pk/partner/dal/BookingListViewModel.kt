package com.bykea.pk.partner.dal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/**
 * The ViewModel for [BookingListFragment].
 *
 * @Author: Yousuf Sohail
 */
class BookingListViewModel internal constructor(bookingRepository: BookingRepository) : ViewModel() {
    val bookings: LiveData<List<Booking>> = bookingRepository.getBookings()
}
