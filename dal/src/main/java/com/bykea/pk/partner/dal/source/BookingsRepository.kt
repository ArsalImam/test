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
import com.bykea.pk.partner.dal.source.remote.BookingsRemoteDataSource
import java.util.*

/**
 * Concrete implementation to load bookings from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
class BookingsRepository(
        val bookingsRemoteDataSource: BookingsRemoteDataSource,
        private val bookingsLocalDataSource: BookingsDataSource
) : BookingsDataSource {

    //TODO: fetch from app preference
    private val driverId: String = "23"
    private val token: String = "23"
    private val lat: Double = 23.5
    private val lng: Double = 23.3
    private val limit: Int = 23

    /**
     * This variable has public visibility so it can be accessed from tests.
     */
    var cachedBookings: LinkedHashMap<Long, Booking> = LinkedHashMap()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var cacheIsDirty = false

    /**
     * Gets bookings from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     *
     *
     * Note: [LoadBookingsCallback.onDataNotAvailable] is fired if all data sources fail to
     * get the data.
     */
    override fun getBookings(callback: BookingsDataSource.LoadBookingsCallback) {
        // Respond immediately with cache if available and not dirty
        if (cachedBookings.isNotEmpty() && !cacheIsDirty) {
            callback.onBookingsLoaded(ArrayList(cachedBookings.values))
            return
        }

        if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getBookingsFromRemoteDataSource(callback)
        } else {
            // Query the local storage if available. If not, query the network.
            bookingsLocalDataSource.getBookings(object : BookingsDataSource.LoadBookingsCallback {
                override fun onBookingsLoaded(bookings: List<Booking>) {
                    refreshCache(bookings)
                    callback.onBookingsLoaded(ArrayList(cachedBookings.values))
                }

                override fun onDataNotAvailable(errorMsg: String?) {
                    getBookingsFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun saveBooking(booking: Booking) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(booking) {
            bookingsLocalDataSource.saveBooking(it)
        }
    }

    override fun acceptBooking(booking: Booking) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(booking) {
            bookingsRemoteDataSource.acceptBooking(it.id)
            bookingsLocalDataSource.acceptBooking(it)
        }
    }

    override fun acceptBooking(bookingId: Long) {
        getBookingWithId(bookingId)?.let {
            acceptBooking(it)
        }
    }

    /**
     * Gets bookings from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     *
     *
     * Note: [GetBookingCallback.onDataNotAvailable] is fired if both data sources fail to
     * get the data.
     */
    override fun getBooking(bookingId: Long, callback: BookingsDataSource.GetBookingCallback) {
        val bookingInCache = getBookingWithId(bookingId)

        // Respond immediately with cache if available
        if (bookingInCache != null) {
            callback.onBookingLoaded(bookingInCache)
            return
        }

        // Is the booking in the local data source? If not, query the network.
        bookingsLocalDataSource.getBooking(bookingId, object : BookingsDataSource.GetBookingCallback {
            override fun onBookingLoaded(booking: Booking) {
                // Do in memory cache update to keep the app UI up to date
                cacheAndPerform(booking) {
                    //                    EspressoIdlingResource.decrement() // Set app as idle.
                    callback.onBookingLoaded(it)
                }
            }

            override fun onDataNotAvailable(message: String?) {
                bookingsRemoteDataSource.getBooking(driverId, token, bookingId, object : BookingsDataSource.GetBookingCallback {
                    override fun onBookingLoaded(booking: Booking) {
                        // Do in memory cache update to keep the app UI up to date
                        cacheAndPerform(booking) {
                            //                            EspressoIdlingResource.decrement() // Set app as idle.
                            callback.onBookingLoaded(it)
                        }
                    }

                    override fun onDataNotAvailable(message: String?) {
//                        EspressoIdlingResource.decrement() // Set app as idle.
                        callback.onDataNotAvailable(message)
                    }
                })
            }
        })
    }

    override fun refreshBookings() {
        cacheIsDirty = true
    }

    override fun deleteAllBookings() {
//        bookingsRemoteDataSource.deleteAllBookings()
        bookingsLocalDataSource.deleteAllBookings()
        cachedBookings.clear()
    }

    override fun deleteBooking(bookingId: Long) {
//        bookingsRemoteDataSource.deleteBooking(bookingId)
        bookingsLocalDataSource.deleteBooking(bookingId)
        cachedBookings.remove(bookingId)
    }

    private fun getBookingsFromRemoteDataSource(callback: BookingsDataSource.LoadBookingsCallback) {

        bookingsRemoteDataSource.getBookings(driverId, token, lat, lng, limit, object : BookingsDataSource.LoadBookingsCallback {
            override fun onBookingsLoaded(bookings: List<Booking>) {
                refreshCache(bookings)
                refreshLocalDataSource(bookings)
//                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onBookingsLoaded(ArrayList(cachedBookings.values))
            }

            override fun onDataNotAvailable(errorMsg: String?) {
//                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onDataNotAvailable(errorMsg)
            }
        })
    }

    private fun refreshCache(bookings: List<Booking>) {
        cachedBookings.clear()
        bookings.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    private fun refreshLocalDataSource(bookings: List<Booking>) {
        bookingsLocalDataSource.deleteAllBookings()
        for (booking in bookings) {
            bookingsLocalDataSource.saveBooking(booking)
        }
    }

    private fun getBookingWithId(id: Long) = cachedBookings[id]

    private inline fun cacheAndPerform(booking: Booking, perform: (Booking) -> Unit) {
        cachedBookings[booking.id] = booking
        perform(booking)
    }

    companion object {

        private var INSTANCE: BookingsRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.

         * @param bookingsRemoteDataSource the backend data source
         * *
         * @param bookingsLocalDataSource  the device storage data source
         * *
         * @return the [BookingsRepository] instance
         */
        @JvmStatic
        fun getInstance(bookingsRemoteDataSource: BookingsRemoteDataSource, bookingsLocalDataSource: BookingsDataSource) =
                INSTANCE ?: synchronized(BookingsRepository::class.java) {
                    INSTANCE
                            ?: BookingsRepository(bookingsRemoteDataSource, bookingsLocalDataSource)
                                    .also { INSTANCE = it }
                }


        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
