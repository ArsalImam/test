package com.bykea.pk.partner.dal.source

import com.bykea.pk.partner.dal.JobRequest

/**
 * Main entry point for accessing job requests data.
 *
 *
 * @Author: Yousuf Sohail
 */
interface JobRequestsDataSource {

    /**
     * Get JobRequest Listing
     *
     * @param callback Callback to executed
     */
    fun getJobRequests(callback: LoadJobRequestsCallback)

    /**
     * Fetch JobRequest details
     *
     * @param jobRequestId Id of JobRequest to be fetched
     * @param callback Callback to executed
     */
    fun getJobRequest(jobRequestId: Long, callback: GetJobRequestCallback)

    /**
     * Save JobRequest to data source
     *
     * @param jobRequest
     */
    fun saveJobRequest(jobRequest: JobRequest)

    /**
     * Accept jobRequest
     *
     * @param jobRequestId Id of JobRequest to be accepted
     */
    fun acceptJobRequest(jobRequestId: Long, callback: AcceptJobRequestCallback)

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
     * Callback interface used for fetch JobRequest listing
     *
     */
    interface LoadJobRequestsCallback {

        /**
         * On successfully JobRequest listing loaded
         *
         * @param jobRequests
         */
        fun onJobRequestsLoaded(jobRequests: List<JobRequest>)

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
         * @param jobRequest
         */
        fun onJobRequestLoaded(jobRequest: JobRequest)

        /**
         * On data not available on data source
         *
         * @param message
         */
        fun onDataNotAvailable(message: String?)
    }

    /**
     * Callback interface used for accepting JobRequest
     *
     */
    interface AcceptJobRequestCallback {

        fun onJobRequestAccepted()

        fun onJobRequestAcceptFailed(message: String?, taken: Boolean)
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