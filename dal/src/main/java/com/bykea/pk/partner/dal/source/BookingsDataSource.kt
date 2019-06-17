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

    /**
     * Get Booking Listing
     *
     * @param callback Callback to executed
     */
    fun getBookings(callback: LoadBookingsCallback)

    /**
     * Fetch Booking details
     *
     * @param bookingId Id of Booking to be fetched
     * @param callback Callback to executed
     */
    fun getBooking(bookingId: Long, callback: GetBookingCallback)

    /**
     * Save Booking to data source
     *
     * @param booking
     */
    fun saveBooking(booking: Booking)

    /**
     * Accept booking
     *
     * @param bookingId Id of Booking to be accepted
     */
    fun acceptBooking(bookingId: Long, callback: AcceptBookingCallback)

    /**
     * Re-fetch booking listing
     *
     */
    fun refreshBookings()

    /**
     * Delete all booking from data source
     *
     */
    fun deleteAllBookings()

    /**
     * Delete booking from data source
     *
     * @param bookingId Id of booking to be deleted
     */
    fun deleteBooking(bookingId: Long)

    /**
     * Callback interface used for fetch Booking listing
     *
     */
    interface LoadBookingsCallback {

        /**
         * On successfully Booking listing loaded
         *
         * @param bookings
         */
        fun onBookingsLoaded(bookings: List<Booking>)

        /**
         * On data not available on data source
         *
         * @param errorMsg
         */
        fun onDataNotAvailable(errorMsg: String?)
    }

    /**
     * Callback interface used for fetch Booking details
     *
     */
    interface GetBookingCallback {

        /**
         * On successfully Booking detail loaded
         *
         * @param booking
         */
        fun onBookingLoaded(booking: Booking)

        /**
         * On data not available on data source
         *
         * @param message
         */
        fun onDataNotAvailable(message: String?)
    }

    /**
     * Callback interface used for accepting Booking
     *
     */
    interface AcceptBookingCallback {

        fun onBookingAccepted()

        fun onBookingAcceptFailed(message: String?, taken: Boolean)
    }
}