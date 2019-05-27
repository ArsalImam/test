/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 */
interface BookingsDataSource {

    interface LoadBookingsCallback {

        fun onBookingsLoaded(bookings: List<Booking>)

        fun onDataNotAvailable()
    }

    interface GetBookingCallback {

        fun onBookingLoaded(booking: Booking)

        fun onDataNotAvailable()
    }

    fun getBookings(callback: LoadBookingsCallback)

    fun getBooking(bookingId: Long, callback: GetBookingCallback)

    fun saveBooking(booking: Booking)

    fun acceptBooking(booking: Booking)

    fun acceptBooking(bookingId: Long)

//    fun activateBooking(booking: Booking)
//
//    fun activateBooking(bookingId: String)

//    fun clearCompletedBookings()

    fun refreshBookings()

    fun deleteAllBookings()

    fun deleteBooking(bookingId: Long)
}