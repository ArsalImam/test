package com.bykea.pk.partner.ui.loadboard.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating a [BookingDetailViewModel] with a constructor that takes a [BookingRepository]
 * and an ID for the current [Booking].
 *
 * @Author: Yousuf Sohail
 */
class BookingDetailViewModelFactory(
        private val bookingRepository: BookingRepository,
        private val bookingId: Long
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookingDetailViewModel(bookingRepository, bookingId) as T
    }
}
