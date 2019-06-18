package com.bykea.pk.partner.dal.source

import android.content.SharedPreferences
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.dal.source.pref.AppPref
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
        private val bookingsRemoteDataSource: BookingsRemoteDataSource,
        private val bookingsLocalDataSource: BookingsDataSource,
        val pref: SharedPreferences) : BookingsDataSource {

    private val limit: Int = 20

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

        cacheIsDirty = true //Cache disabled

        // Respond immediately with cache if available and not dirty
        if (cachedBookings.isNotEmpty() && !cacheIsDirty) {
            callback.onBookingsLoaded(ArrayList(cachedBookings.values))
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
        }

        if (bookingInCache == null || !bookingInCache.isComplete) {
            bookingsLocalDataSource.getBooking(bookingId, object : BookingsDataSource.GetBookingCallback {
                override fun onBookingLoaded(booking: Booking) {
                    if (booking.isComplete) {
                        // Do in memory cache update to keep the app UI up to date
                        cacheAndPerform(booking) {
                            callback.onBookingLoaded(it)
                        }
                    } else {
                        getBookingFromRemoteDataSource(bookingId, callback)
                    }
                }

                override fun onDataNotAvailable(message: String?) {
                    getBookingFromRemoteDataSource(bookingId, callback)
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

    override fun acceptBooking(bookingId: Long, callback: BookingsDataSource.AcceptBookingCallback) {
        bookingsRemoteDataSource.acceptBooking(bookingId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), callback)
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

        var serviceCode: Int? = null
        if (!AppPref.getIsCash(pref)) serviceCode = 21

        bookingsRemoteDataSource.getBookings(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), serviceCode, limit, object : BookingsDataSource.LoadBookingsCallback {
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

    private fun getBookingFromRemoteDataSource(bookingId: Long, callback: BookingsDataSource.GetBookingCallback) {
        bookingsRemoteDataSource.getBooking(bookingId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), object : BookingsDataSource.GetBookingCallback {
            override fun onBookingLoaded(booking: Booking) {
                // Do in memory cache update to keep the app UI up to date
                booking.isComplete = true
                cacheAndPerform(booking) {
                    saveBooking(booking)
                    callback.onBookingLoaded(it)
                }
            }

            override fun onDataNotAvailable(message: String?) {
                callback.onDataNotAvailable(message)
            }
        })
    }


    /**
     * Delete and save new list of Booking list in memory cache
     *
     * @param bookings List of [Booking] to update
     */
    private fun refreshCache(bookings: List<Booking>) {
        cachedBookings.clear()
        bookings.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    /**
     * Delete and save new list of Booking list in local data source
     *
     * @param bookings List of [Booking] to update
     */
    private fun refreshLocalDataSource(bookings: List<Booking>) {
        bookingsLocalDataSource.deleteAllBookings()
        for (booking in bookings) {
            bookingsLocalDataSource.saveBooking(booking)
        }
    }

    /**
     * Fetch [Booking] from in memory cache
     *
     * @param id Booking Id to fetch
     */
    private fun getBookingWithId(id: Long) = cachedBookings[id]

    /**
     * Update cache with [Booking] and then perform given task
     *
     * @param booking [Booking] to cache
     * @param perform Task to perform after cache
     */
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
        fun getInstance(bookingsRemoteDataSource: BookingsRemoteDataSource, bookingsLocalDataSource: BookingsDataSource, preferences: SharedPreferences) =
                INSTANCE ?: synchronized(BookingsRepository::class.java) {
                    INSTANCE
                            ?: BookingsRepository(bookingsRemoteDataSource, bookingsLocalDataSource, preferences)
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
