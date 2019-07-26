package com.bykea.pk.partner.dal.source

import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.LocCoordinatesInTrip
import com.bykea.pk.partner.dal.source.remote.response.FinishJobResponseData

/**
 * Main entry point for accessing job requests data.
 *
 *
 * @Author: Yousuf Sohail
 */
interface JobsDataSource {

    /**
     * Get list of jobs
     *
     * @param callback Callback to executed
     */
    fun getJobs(callback: LoadJobsCallback)

    /**
     * Fetch JobRequest details
     *
     * @param jobId Id of JobRequest to be fetched
     * @param callback Callback to executed
     */
    fun getJob(jobId: Long, callback: GetJobRequestCallback)

    /**
     * Save JobRequest to data source
     *
     * @param job
     */
    fun saveJob(job: Job)

    /**
     * Re-fetch jobRequest listing
     *
     */
    fun refreshJobRequestList()

    /**
     * Delete all jobRequest from data source
     *
     */
    fun deleteAllJobRequests()

    /**
     * Delete jobRequest from data source
     *
     * @param jobRequestId Id of jobRequest to be deleted
     */
    fun deleteJobRequest(jobRequestId: Long)

    /**
     * Accept jobRequest
     *
     * @param jobRequestId Id of JobRequest to be accepted
     * @param callback Response callback
     */
    fun acceptJobRequest(jobRequestId: Long, callback: AcceptJobRequestCallback)

    /**
     * Finish active job
     *
     * @param jobId Job Id
     * @param route Route taken for job
     * @param callback Response callback
     */
    fun finishJob(jobId: String, route: ArrayList<LocCoordinatesInTrip>, callback: FinishJobCallback)

    /**
     * Conclude active job
     *
     * @param callback Response callback
     */
    fun concludeJob(callback: ConcludeJobCallback)

    /**
     * Callback interface used for fetch JobRequest listing
     *
     */
    interface LoadJobsCallback {

        /**
         * On successfully JobRequest listing loaded
         *
         * @param jobs
         */
        fun onJobsLoaded(jobs: List<Job>)

        /**
         * On data not available on data source
         *
         * @param errorMsg
         */
        fun onDataNotAvailable(errorMsg: String? = "Data Not Available")
    }

    /**
     * Callback interface used for fetch JobRequest details
     *
     */
    interface GetJobRequestCallback {

        /**
         * On successfully JobRequest detail loaded
         *
         * @param job
         */
        fun onJobLoaded(job: Job)

        /**
         * On data not available on data source
         *
         * @param message
         */
        fun onDataNotAvailable(message: String?)
    }

    /**
     * Callback interface used for accepting job
     *
     */
    interface AcceptJobRequestCallback {

        fun onJobRequestAccepted()

        fun onJobRequestAcceptFailed(message: String?, taken: Boolean)
    }

    /**
     * Callback interface used for job finish
     */
    interface FinishJobCallback {

        /**
         * On job finish success
         */
        fun onJobFinished(data: FinishJobResponseData)

        /**
         * On job finish failed
         */
        fun onJobFinishFailed(message: String?)
    }

    /**
     * Callback interface used for job finish
     */
    interface ConcludeJobCallback {

        /**
         * On job conclude success
         */
        fun onJobConcluded()

        /**
         * On job conclude success
         */
        fun onJobConcludeFailed()
    }

    /**
     * Get Email Update
     */
    fun getEmailUpdate(emailId: String, callback: EmailUpdateCallback) {}

    /**
     * Callback interface for email update
     */
    interface EmailUpdateCallback {
        fun onSuccess()

        fun onFail(message: String?)
    }

    /**
     * Check Email Update
     */
    fun checkEmailUpdate(callback: EmailUpdateCheckCallback) {}

    /**
     * Callback interface to check if email is updated
     */
    interface EmailUpdateCheckCallback {
        fun onSuccess(isEmailUpdated: Boolean)

        fun onFail(message: String?)
    }
}