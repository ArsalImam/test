package com.bykea.pk.partner.dal.source

import android.content.SharedPreferences
import com.bykea.pk.partner.dal.Booking
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.dal.source.remote.JobRequestsRemoteDataSource
import com.bykea.pk.partner.dal.util.SERVICE_CODE_SEND
import java.util.*

/**
 * Concrete implementation to load bookings from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
class JobRequestsRepository(
        private val jobRequestsRemoteDataSource: JobRequestsRemoteDataSource,
        private val jobRequestsLocalDataSource: JobRequestsDataSource,
        val pref: SharedPreferences) : JobRequestsDataSource {

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
    override fun getJobRequests(callback: JobRequestsDataSource.LoadJobRequestsCallback) {

        cacheIsDirty = true //Cache disabled

        // Respond immediately with cache if available and not dirty
        if (cachedBookings.isNotEmpty() && !cacheIsDirty) {
            callback.onJobRequestsLoaded(ArrayList(cachedBookings.values))
        }

        if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getBookingsFromRemoteDataSource(callback)
        } else {
            // Query the local storage if available. If not, query the network.
            jobRequestsLocalDataSource.getJobRequests(object : JobRequestsDataSource.LoadJobRequestsCallback {
                override fun onJobRequestsLoaded(jobRequests: List<Booking>) {
                    refreshCache(jobRequests)
                    callback.onJobRequestsLoaded(ArrayList(cachedBookings.values))
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
    override fun getJobRequest(bookingId: Long, callback: JobRequestsDataSource.GetJobRequestCallback) {

        val bookingInCache = getBookingWithId(bookingId)

        // Respond immediately with cache if available
        if (bookingInCache != null) {
            callback.onBookingLoaded(bookingInCache)
        }

        if (bookingInCache == null || !bookingInCache.isComplete) {
            jobRequestsLocalDataSource.getJobRequest(bookingId, object : JobRequestsDataSource.GetJobRequestCallback {
                override fun onBookingLoaded(jobRequest: Booking) {
                    if (jobRequest.isComplete) {
                        // Do in memory cache update to keep the app UI up to date
                        cacheAndPerform(jobRequest) {
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

    override fun saveJobRequest(booking: Booking) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(booking) {
            jobRequestsLocalDataSource.saveJobRequest(it)
        }
    }

    override fun acceptJobRequest(bookingId: Long, callback: JobRequestsDataSource.AcceptJobRequestCallback) {
        jobRequestsRemoteDataSource.acceptBooking(bookingId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), callback)
    }

    override fun refreshJobRequestList() {
        cacheIsDirty = true
    }

    override fun deleteAllJobRequests() {
//        jobRequestsRemoteDataSource.deleteAllJobRequests()
        jobRequestsLocalDataSource.deleteAllJobRequests()
        cachedBookings.clear()
    }

    override fun deleteJobRequest(bookingId: Long) {
//        jobRequestsRemoteDataSource.deleteJobRequest(bookingId)
        jobRequestsLocalDataSource.deleteJobRequest(bookingId)
        cachedBookings.remove(bookingId)
    }

    private fun getBookingsFromRemoteDataSource(callback: JobRequestsDataSource.LoadJobRequestsCallback) {

        var serviceCode: Int? = null
        if (!AppPref.getIsCash(pref)) serviceCode = SERVICE_CODE_SEND

        jobRequestsRemoteDataSource.getBookings(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), serviceCode, limit, object : JobRequestsDataSource.LoadJobRequestsCallback {
            override fun onJobRequestsLoaded(jobRequests: List<Booking>) {
                refreshCache(jobRequests)
                refreshLocalDataSource(jobRequests)
//                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onJobRequestsLoaded(ArrayList(cachedBookings.values))
            }

            override fun onDataNotAvailable(errorMsg: String?) {
//                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onDataNotAvailable(errorMsg)
            }
        })
    }

    private fun getBookingFromRemoteDataSource(bookingId: Long, callback: JobRequestsDataSource.GetJobRequestCallback) {
        jobRequestsRemoteDataSource.getBooking(bookingId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), object : JobRequestsDataSource.GetJobRequestCallback {
            override fun onBookingLoaded(jobRequest: Booking) {
                // Do in memory cache update to keep the app UI up to date
                jobRequest.isComplete = true
                cacheAndPerform(jobRequest) {
                    saveJobRequest(jobRequest)
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
        jobRequestsLocalDataSource.deleteAllJobRequests()
        for (booking in bookings) {
            jobRequestsLocalDataSource.saveJobRequest(booking)
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

        private var INSTANCE: JobRequestsRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.

         * @param jobRequestsRemoteDataSource the backend data source
         * *
         * @param jobRequestsLocalDataSource  the device storage data source
         * *
         * @return the [JobRequestsRepository] instance
         */
        @JvmStatic
        fun getInstance(jobRequestsRemoteDataSource: JobRequestsRemoteDataSource, jobRequestsLocalDataSource: JobRequestsDataSource, preferences: SharedPreferences) =
                INSTANCE ?: synchronized(JobRequestsRepository::class.java) {
                    INSTANCE
                            ?: JobRequestsRepository(jobRequestsRemoteDataSource, jobRequestsLocalDataSource, preferences)
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
