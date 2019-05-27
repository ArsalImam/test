package com.bykea.pk.partner.ui.loadboard.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bykea.pk.partner.dal.local.Booking

/**
 * The ViewModel for [BookingListFragment].
 *
 * @Author: Yousuf Sohail
 */
class BookingListViewModel internal constructor(bookingRepository: BookingRepository) : ViewModel() {
    val bookings: LiveData<List<Booking>> = bookingRepository.getBookings()
}
