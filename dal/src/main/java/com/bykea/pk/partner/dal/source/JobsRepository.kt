package com.bykea.pk.partner.dal.source

import android.content.SharedPreferences
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.LocCoordinatesInTrip
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.dal.source.remote.JobsRemoteDataSource
import com.bykea.pk.partner.dal.source.remote.request.ConcludeJobRequest
import com.bykea.pk.partner.dal.source.remote.request.FinishJobRequest
import com.bykea.pk.partner.dal.util.SERVICE_CODE_SEND
import java.util.*
import kotlin.collections.ArrayList

/**
 * Concrete implementation to load jobRequests from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
class JobsRepository(
        private val jobsRemoteDataSource: JobsRemoteDataSource,
        private val jobsLocalDataSource: JobsDataSource,
        val pref: SharedPreferences) : JobsDataSource {

    private val limit: Int = 20

    /**
     * This variable has public visibility so it can be accessed from tests.
     */
    var cachedJobs: LinkedHashMap<Long, Job> = LinkedHashMap()

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
    override fun getJobs(callback: JobsDataSource.LoadJobsCallback) {

        cacheIsDirty = true //Cache disabled

        // Respond immediately with cache if available and not dirty
        if (cachedJobs.isNotEmpty() && !cacheIsDirty) {
            callback.onJobsLoaded(ArrayList(cachedJobs.values))
        }

        if (cacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getJobsFromRemoteDataSource(callback)
        } else {
            // Query the local storage if available. If not, query the network.
            jobsLocalDataSource.getJobs(object : JobsDataSource.LoadJobsCallback {
                override fun onJobsLoaded(jobs: List<Job>) {
                    refreshCache(jobs)
                    callback.onJobsLoaded(ArrayList(cachedJobs.values))
                }

                override fun onDataNotAvailable(errorMsg: String?) {
                    getJobsFromRemoteDataSource(callback)
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
    override fun getJob(jobId: Long, callback: JobsDataSource.GetJobRequestCallback) {

        val jobRequestInCache = getJobRequestWithId(jobId)

        // Respond immediately with cache if available
        if (jobRequestInCache != null) {
            callback.onJobLoaded(jobRequestInCache)
        }

        if (jobRequestInCache == null || !jobRequestInCache.isComplete) {
            jobsLocalDataSource.getJob(jobId, object : JobsDataSource.GetJobRequestCallback {
                override fun onJobLoaded(job: Job) {
                    if (job.isComplete) {
                        // Do in memory cache update to keep the app UI up to date
                        cacheAndPerform(job) {
                            callback.onJobLoaded(it)
                        }
                    } else {
                        getJobRequestFromRemoteDataSource(jobId, callback)
                    }
                }

                override fun onDataNotAvailable(message: String?) {
                    getJobRequestFromRemoteDataSource(jobId, callback)
                }
            })
        }
    }

    override fun saveJob(job: Job) {
        // Do in memory cache update to keep the app UI up to date
        cacheAndPerform(job) {
            jobsLocalDataSource.saveJob(it)
        }
    }

    override fun refreshJobRequestList() {
        cacheIsDirty = true
    }

    override fun deleteAllJobRequests() {
//        jobsRemoteDataSource.deleteAllJobRequests()
        jobsLocalDataSource.deleteAllJobRequests()
        cachedJobs.clear()
    }

    override fun deleteJobRequest(jobRequestId: Long) {
//        jobsRemoteDataSource.deleteJobRequest(jobRequestId)
        jobsLocalDataSource.deleteJobRequest(jobRequestId)
        cachedJobs.remove(jobRequestId)
    }

    override fun acceptJobRequest(jobRequestId: Long, callback: JobsDataSource.AcceptJobRequestCallback) {
        jobsRemoteDataSource.acceptJob(jobRequestId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), callback)
    }

    override fun finishJob(jobId: String, route: ArrayList<LocCoordinatesInTrip>, callback: JobsDataSource.FinishJobCallback) {
        val body = FinishJobRequest(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), route)
        jobsRemoteDataSource.finishJob(jobId, body, callback)
    }

    override fun concludeJob(jobId: String, rate: Int, receivedAmount: Int, callback: JobsDataSource.ConcludeJobCallback, deliveryMessage: String?, deliveryStatus: Boolean?, purchaseAmount: Int?, receiverName: String?, receiverPhone: String?) {
        val body = ConcludeJobRequest(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), rate, receivedAmount, deliveryMessage, deliveryStatus, purchaseAmount, receiverName, receiverPhone)
        jobsRemoteDataSource.concludeJob(jobId, body, callback)
    }

    private fun getJobsFromRemoteDataSource(callback: JobsDataSource.LoadJobsCallback) {

        var serviceCode: Int? = null
        if (!AppPref.getIsCash(pref)) serviceCode = SERVICE_CODE_SEND

        jobsRemoteDataSource.getJobs(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), serviceCode, limit, object : JobsDataSource.LoadJobsCallback {
            override fun onJobsLoaded(jobs: List<Job>) {
                refreshCache(jobs)
                refreshLocalDataSource(jobs)
//                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onJobsLoaded(ArrayList(cachedJobs.values))
            }

            override fun onDataNotAvailable(errorMsg: String?) {
//                EspressoIdlingResource.decrement() // Set app as idle.
                callback.onDataNotAvailable(errorMsg)
            }
        })
    }

    private fun getJobRequestFromRemoteDataSource(jobRequestId: Long, callback: JobsDataSource.GetJobRequestCallback) {
        jobsRemoteDataSource.getJob(jobRequestId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), object : JobsDataSource.GetJobRequestCallback {
            override fun onJobLoaded(job: Job) {
                // Do in memory cache update to keep the app UI up to date
                job.isComplete = true
                cacheAndPerform(job) {
                    saveJob(job)
                    callback.onJobLoaded(it)
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
     * @param jobs List of [Job] to update
     */
    private fun refreshCache(jobs: List<Job>) {
        cachedJobs.clear()
        jobs.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }

    /**
     * Delete and save new list of JobRequest list in local data source
     *
     * @param jobs List of [Job] to update
     */
    private fun refreshLocalDataSource(jobs: List<Job>) {
        jobsLocalDataSource.deleteAllJobRequests()
        for (jobRequest in jobs) {
            jobsLocalDataSource.saveJob(jobRequest)
        }
    }

    /**
     * Fetch [Job] from in memory cache
     *
     * @param id JobRequest Id to fetch
     */
    private fun getJobRequestWithId(id: Long) = cachedJobs[id]

    /**
     * Update cache with [Job] and then perform given task
     *
     * @param job [Job] to cache
     * @param perform Task to perform after cache
     */
    private inline fun cacheAndPerform(job: Job, perform: (Job) -> Unit) {
        cachedJobs[job.id] = job
        perform(job)
    }

    companion object {

        private var INSTANCE: JobsRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.

         * @param jobsRemoteDataSource the backend data source
         * *
         * @param jobsLocalDataSource  the device storage data source
         * *
         * @return the [JobsRepository] instance
         */
        @JvmStatic
        fun getInstance(jobsRemoteDataSource: JobsRemoteDataSource, jobsLocalDataSource: JobsDataSource, preferences: SharedPreferences) =
                INSTANCE ?: synchronized(JobsRepository::class.java) {
                    INSTANCE
                            ?: JobsRepository(jobsRemoteDataSource, jobsLocalDataSource, preferences)
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
