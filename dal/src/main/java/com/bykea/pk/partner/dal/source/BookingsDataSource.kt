package com.bykea.pk.partner.dal.source

import com.bykea.pk.partner.dal.Booking

/**
 * Main entry point for accessing bookings data.
 *
 *
 * For simplicity, only getBookings() and getBooking() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new booking is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 *
 * @Author: Yousuf Sohail
 */
interface BookingsDataSource {

    fun getBookings(callback: LoadBookingsCallback)

    fun getBooking(bookingId: Long, callback: GetBookingCallback)

    fun saveBooking(booking: Booking)

    fun acceptBooking(bookingId: Long, callback: AcceptBookingCallback)

    fun refreshBookings()

    fun deleteAllBookings()

    fun deleteBooking(bookingId: Long)

    interface LoadBookingsCallback {

        fun onBookingsLoaded(bookings: List<Booking>)

        fun onDataNotAvailable(errorMsg: String?)
    }

    interface GetBookingCallback {

        fun onBookingLoaded(booking: Booking)

        fun onDataNotAvailable(message: String?)
    }

    interface AcceptBookingCallback {

        fun onBookingAccepted()

        fun onBookingAcceptFailed(message: String?)
    }
}