package com.bykea.pk.partner.dal.source

import android.content.SharedPreferences
import com.bykea.pk.partner.dal.JobRequest
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.dal.source.remote.JobRequestsRemoteDataSource
import com.bykea.pk.partner.dal.util.SERVICE_CODE_SEND
import java.util.*

/**
 * Concrete implementation to load jobRequests from the data sources into a cache.
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
    var cachedJobRequests: LinkedHashMap<Long, JobRequest> = LinkedHashMap()

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    var cacheIsDirty = false

    /**
     * Gets jobRequests from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     *
     *
     * Note: [LoadJobRequestsCallback.onDataNotAvailable] is fired if all data sources fail to
     * get the data.
     */
    override fun getJobRequests(callback: JobRequestsDataSource.LoadJobRequestsCallback) {

        cacheIsDirty = true //Cache disabled

        // Respond immediately with cache if available and not dirty
        if (cachedJobRequests.isNotEmpty() && !cacheIsDirty) {
            callback.onJobRequestsLoaded(ArrayList(cachedJobRequests.values))
        }

        if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getJobRequestsFromRemoteDataSource(callback)
        } else {
            // Query the local storage if available. If not, query the network.
            jobRequestsLocalDataSource.getJobRequests(object : JobRequestsDataSource.LoadJobRequestsCallback {
                override fun onJobRequestsLoaded(jobRequests: List<JobRequest>) {
                    refreshCache(jobRequests)
                    callback.onJobRequestsLoaded(ArrayList(cachedJobRequests.values))
                }

                override fun onDataNotAvailable(errorMsg: String?) {
                    getJobRequestsFromRemoteDataSource(callback)
                }
            })
        }
    }

    /**
     * Gets jobRequests from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     *
     *
     * Note: [GetJobRequestCallback.onDataNotAvailable] is fired if both data sources fail to
     * get the data.
     */
    override fun getJobRequest(jobRequestId: Long, callback: JobRequestsDataSource.GetJobRequestCallback) {

        val jobRequestInCache = getJobRequestWithId(jobRequestId)

        // Respond immediately with cache if available
        if (jobRequestInCache != null) {
            callback.onJobRequestLoaded(jobRequestInCache)
        }

        if (jobRequestInCache == null || !jobRequestInCache.isComplete) {
            jobRequestsLocalDataSource.getJobRequest(jobRequestId, object : JobRequestsDataSource.GetJobRequestCallback {
                override fun onJobRequestLoaded(jobRequest: JobRequest) {
                    if (jobRequest.isComplete) {
                        // Do in memory cache update to keep the app UI up to date
                        cacheAndPerform(jobRequest) {
                            callback.onJobRequestLoaded(it)
                        }
                    } else {
                        getJobRequestFromRemoteDataSource(jobRequestId, callback)
                    }
                }

                override fun onDataNotAvailable(message: String?) {
                    getJobRequestFromRemoteDataSource(jobRequestId, callback)
                }
            })
        }
    }

    override fun saveJobRequest(jobRequest: JobRequest) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(jobRequest) {
            jobRequestsLocalDataSource.saveJobRequest(it)
        }
    }

    override fun acceptJobRequest(jobRequestId: Long, callback: JobRequestsDataSource.AcceptJobRequestCallback) {
        jobRequestsRemoteDataSource.acceptJobRequest(jobRequestId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), callback)
    }

    override fun refreshJobRequestList() {
        cacheIsDirty = true
    }

    override fun deleteAllJobRequests() {
//        jobRequestsRemoteDataSource.deleteAllJobRequests()
        jobRequestsLocalDataSource.deleteAllJobRequests()
        cachedJobRequests.clear()
    }

    override fun deleteJobRequest(jobRequestId: Long) {
//        jobRequestsRemoteDataSource.deleteJobRequest(jobRequestId)
        jobRequestsLocalDataSource.deleteJobRequest(jobRequestId)
        cachedJobRequests.remove(jobRequestId)
    }

    private fun getJobRequestsFromRemoteDataSource(callback: JobRequestsDataSource.LoadJobRequestsCallback) {

        var serviceCode: Int? = null
        if (!AppPref.getIsCash(pref)) serviceCode = SERVICE_CODE_SEND

        jobRequestsRemoteDataSource.getJobRequests(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), serviceCode, limit, object : JobRequestsDataSource.LoadJobRequestsCallback {
            override fun onJobRequestsLoaded(jobRequests: List<JobRequest>) {
                refreshCache(jobRequests)
                refreshLocalDataSource(jobRequests)
//                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onJobRequestsLoaded(ArrayList(cachedJobRequests.values))
            }

            override fun onDataNotAvailable(errorMsg: String?) {
//                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onDataNotAvailable(errorMsg)
            }
        })
    }

    private fun getJobRequestFromRemoteDataSource(jobRequestId: Long, callback: JobRequestsDataSource.GetJobRequestCallback) {
        jobRequestsRemoteDataSource.getJobRequest(jobRequestId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), object : JobRequestsDataSource.GetJobRequestCallback {
            override fun onJobRequestLoaded(jobRequest: JobRequest) {
                // Do in memory cache update to keep the app UI up to date
                jobRequest.isComplete = true
                cacheAndPerform(jobRequest) {
                    saveJobRequest(jobRequest)
                    callback.onJobRequestLoaded(it)
                }
            }

            override fun onDataNotAvailable(message: String?) {
                callback.onDataNotAvailable(message)
            }
        })
    }


    /**
     * Delete and save new list of JobRequest list in memory cache
     *
     * @param jobRequests List of [JobRequest] to update
     */
    private fun refreshCache(jobRequests: List<JobRequest>) {
        cachedJobRequests.clear()
        jobRequests.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    /**
     * Delete and save new list of JobRequest list in local data source
     *
     * @param jobRequests List of [JobRequest] to update
     */
    private fun refreshLocalDataSource(jobRequests: List<JobRequest>) {
        jobRequestsLocalDataSource.deleteAllJobRequests()
        for (jobRequest in jobRequests) {
            jobRequestsLocalDataSource.saveJobRequest(jobRequest)
        }
    }

    /**
     * Fetch [JobRequest] from in memory cache
     *
     * @param id JobRequest Id to fetch
     */
    private fun getJobRequestWithId(id: Long) = cachedJobRequests[id]

    /**
     * Update cache with [JobRequest] and then perform given task
     *
     * @param jobRequest [JobRequest] to cache
     * @param perform Task to perform after cache
     */
    private inline fun cacheAndPerform(jobRequest: JobRequest, perform: (JobRequest) -> Unit) {
        cachedJobRequests[jobRequest.id] = jobRequest
        perform(jobRequest)
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

    /**
     * Get Email Update From Remote Data
     */
    override fun getEmailUpdate(emailId: String, callback: JobRequestsDataSource.EmailUpdateCallback) {
        jobRequestsRemoteDataSource.getEmailUpdateRequest(emailId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), callback)
    }

    /**
     * Check Is Email Updated From Remote Data
     */
    override fun checkEmailUpdate(callback: JobRequestsDataSource.EmailUpdateCheckCallback) {
        jobRequestsRemoteDataSource.getCheckIsEmailUpdatedRequest(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), callback)
    }
}
