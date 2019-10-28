package com.bykea.pk.partner.dal.source

import android.content.SharedPreferences
import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.LocCoordinatesInTrip
import com.bykea.pk.partner.dal.source.local.JobsLocalDataSource
import com.bykea.pk.partner.dal.source.pref.AppPref
import com.bykea.pk.partner.dal.source.remote.JobsRemoteDataSource
import com.bykea.pk.partner.dal.source.remote.request.ChangeDropOffRequest
import com.bykea.pk.partner.dal.source.remote.request.ConcludeJobRequest
import com.bykea.pk.partner.dal.source.remote.request.FinishJobRequest
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateRequestObject
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
        private val jobsLocalDataSource: JobsLocalDataSource,
        val pref: SharedPreferences) : JobsDataSource {

    private val limit: Int = 20

    /**
     * This variable has public visibility so it can be accessed from tests.
     */
    var cachedJobs: LinkedHashMap<Long, Job> = LinkedHashMap()

    /**
     * Gets jobRequests from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     *
     *
     * Note: [LoadJobRequestsCallback.onDataNotAvailable] is fired if all data sources fail to
     * get the data.
     */
    override fun getJobs(callback: JobsDataSource.LoadJobsCallback) {

        if (cachedJobs.isNotEmpty()) {
            callback.onJobsLoaded(ArrayList(cachedJobs.values))
        }

        var serviceCode: Int? = null
        if (!AppPref.getIsCash(pref)) serviceCode = SERVICE_CODE_SEND

        jobsRemoteDataSource.getJobs(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), serviceCode, limit, object : JobsDataSource.LoadJobsCallback {
            override fun onJobsLoaded(jobs: List<Job>) {
                callback.onJobsLoaded(jobs)
                refreshCache(jobs)
            }

            override fun onDataNotAvailable(errorMsg: String?) {
                callback.onDataNotAvailable(errorMsg)
            }
        })
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

        val jobInCache = getJobWithId(jobId)

        // Respond immediately with cache if available
        if (jobInCache != null) callback.onJobLoaded(jobInCache)

        // Or continue to fetch latest details
        if (jobInCache == null || !jobInCache.isComplete) getJobFromRemote(jobId, callback)
    }

    override fun saveJob(job: Job) {
        jobsLocalDataSource.saveJob(job)
    }

    override fun deleteAllJobRequests() {
        jobsLocalDataSource.deleteAllJobRequests()
    }

    override fun deleteJobRequest(jobRequestId: Long) {
        jobsLocalDataSource.deleteJobRequest(jobRequestId)
    }

    override fun pickJob(job: Job, callback: JobsDataSource.AcceptJobRequestCallback) {
        jobsRemoteDataSource.pickJob(job.id, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), object : JobsDataSource.AcceptJobRequestCallback {
            override fun onJobRequestAccepted() {
                saveJob(job)
                callback.onJobRequestAccepted()
            }

            override fun onJobRequestAcceptFailed(code: Int, message: String?) {
                callback.onJobRequestAcceptFailed(code, message)
            }

        })
    }

    override fun ackJobCall(jobId: String, callback: JobsDataSource.AckJobCallCallback) {
        jobsRemoteDataSource.acknowledgeJobCall(jobId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), callback)
    }

    override fun acceptJob(jobId: String, timeEclipsed: Int, callback: JobsDataSource.AcceptJobCallback) {
        jobsRemoteDataSource.acceptJob(jobId, timeEclipsed, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), callback)
    }

    override fun changeDropOff(jobId: String, dropOff: ChangeDropOffRequest.Stop, callback: JobsDataSource.DropOffChangeCallback) {
        jobsRemoteDataSource.changeDropOff(jobId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), dropOff, callback)
    }

    override fun arrivedAtJob(jobId: String, route: ArrayList<LocCoordinatesInTrip>, callback: JobsDataSource.ArrivedAtJobCallback) {
        jobsRemoteDataSource.arrivedAtJob(jobId, route, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), callback)
    }

    override fun startJob(jobId: String, address: String, callback: JobsDataSource.StartJobCallback) {
        jobsRemoteDataSource.startJob(jobId, address, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), callback)
    }

    override fun cancelJob(jobId: String, reason: String, callback: JobsDataSource.CancelJobCallback) {
        jobsRemoteDataSource.cancelJob(jobId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), reason, callback)
    }

    override fun finishJob(jobId: String, route: ArrayList<LocCoordinatesInTrip>, callback: JobsDataSource.FinishJobCallback) {
        val body = FinishJobRequest(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), route)
        jobsRemoteDataSource.finishJob(jobId, body, callback)
    }

    override fun concludeJob(jobId: String, rate: Int, receivedAmount: Int, callback: JobsDataSource.ConcludeJobCallback, deliveryMessage: String?, deliveryStatus: Boolean?, purchaseAmount: Int?, receiverName: String?, receiverPhone: String?) {
        val body = ConcludeJobRequest(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), rate, receivedAmount, deliveryMessage, deliveryStatus, purchaseAmount, receiverName, receiverPhone, "Good customer he was")
        jobsRemoteDataSource.concludeJob(jobId, body, callback)
    }

    override fun checkEmailUpdate(callback: JobsDataSource.EmailUpdateCheckCallback) {
        jobsRemoteDataSource.getCheckIsEmailUpdatedRequest(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), callback)
    }

    override fun getEmailUpdate(emailId: String, callback: JobsDataSource.EmailUpdateCallback) {
        jobsRemoteDataSource.getEmailUpdateRequest(emailId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), callback)
    }

    override fun getFairEstimation(startLat: String, startLng: String, endLat: String, endLng: String, type: String, rideType: String, callback: JobsDataSource.FareEstimationCallback) {
        jobsRemoteDataSource.requestFairEstimation(AppPref.getDriverId(pref), AppPref.getAccessToken(pref),
                startLat, startLng, endLat, endLng, type, rideType, callback)
    }

    override fun requestOtpGenerate(phone: String, type: String, callback: JobsDataSource.OtpGenerateCallback) {
        jobsRemoteDataSource.requestOtpGenerate(AppPref.getDriverId(pref), AppPref.getAccessToken(pref), phone, type, callback)
    }

    override fun createTrip(rideCreateRequestObject: RideCreateRequestObject, callback: JobsDataSource.CreateTripCallback) {
        jobsRemoteDataSource.createTrip(rideCreateRequestObject, callback)
    }

    private fun getJobFromRemote(jobRequestId: Long, callback: JobsDataSource.GetJobRequestCallback) {
        jobsRemoteDataSource.getJob(jobRequestId, AppPref.getDriverId(pref), AppPref.getAccessToken(pref), AppPref.getLat(pref), AppPref.getLng(pref), object : JobsDataSource.GetJobRequestCallback {
            override fun onJobLoaded(job: Job) {
                // Do in memory cache update to keep the app UI up to date
                job.isComplete = true
                cacheAndPerform(job) {
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
            cachedJobs[it.id] = it
        }
    }

    /**
     * Fetch [Job] from in memory cache
     *
     * @param id JobRequest Id to fetch
     */
    private fun getJobWithId(id: Long) = cachedJobs[id]

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
        fun getInstance(jobsRemoteDataSource: JobsRemoteDataSource, jobsLocalDataSource: JobsLocalDataSource, preferences: SharedPreferences) =
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
