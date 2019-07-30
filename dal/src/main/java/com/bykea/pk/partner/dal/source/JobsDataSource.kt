package com.bykea.pk.partner.dal.source

import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.LocCoordinatesInTrip
import com.bykea.pk.partner.dal.source.remote.response.ConcludeJobBadResponse
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
     * Picks job request
     *
     * @param jobId Id of JobRequest to be accepted
     * @param callback Response callback
     */
    fun pickJob(jobId: Long, callback: AcceptJobRequestCallback)

    /**
     * Post acknowledgement of job call
     * @param jobId Job ID
     * @param callback AckJobCallCallback
     */
    fun ackJobCall(jobId: String, callback: AckJobCallCallback)

    /**
     * Requests accept job call
     * @param jobId Job ID
     * @param timeEclipsed Time eclipsed since job call received
     * @param callback AcceptJobCallback
     */
    fun acceptJob(jobId: String, timeEclipsed: Int, callback: AcceptJobCallback)

    /**
     * Requests arrived at job
     * @param jobId Job ID
     * @param callback ArrivedAtJobCallback
     */
    fun arrivedAtJob(jobId: String, callback: ArrivedAtJobCallback)

    /**
     * Requests start job
     * @param jobId Job ID
     * @param address Address at which started the job
     * @param callback StartJobCallback
     */
    fun startJob(jobId: String, address: String, callback: StartJobCallback)

    /**
     * Requests to cancel job
     * @param jobId String
     * @param reason String
     * @param callback CancelJobCallback
     */
    fun cancelJob(jobId: String, reason: String, callback: CancelJobCallback)

    /**
     * Requests to finish job
     *
     * @param jobId Job Id
     * @param route Route taken for job
     * @param callback Response callback
     */
    fun finishJob(jobId: String, route: ArrayList<LocCoordinatesInTrip>, callback: FinishJobCallback)

    /**
     * Requests to conclude job
     * @param jobId Job ID
     * @param rate Rating from driver
     * @param receivedAmount Received amount in rupees
     * @param callback ConcludeJobCallback
     * @param deliveryMessage Delivery message
     * @param deliveryStatus Delivery status
     * @param purchaseAmount Purchase amount
     * @param receiverName Receiver name
     * @param receiverPhone Receiver phone
     */
    fun concludeJob(
            jobId: String,
            rate: Int,
            receivedAmount: Int,
            callback: ConcludeJobCallback,
            deliveryMessage: String?,
            deliveryStatus: Boolean?,
            purchaseAmount: Int?,
            receiverName: String?,
            receiverPhone: String?
    )

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
     * Callback interface used for acknowledgement of receiving of job call
     */
    interface AckJobCallCallback {
        fun onJobCallAcknowledged()
        fun onJobCallAcknowledgeFailed()
    }

    /**
     * Callback interface for accept job
     */
    interface AcceptJobCallback {
        fun onJobAccepted()
        fun onJobAcceptFailed()
    }

    /**
     * Callback interface for arrived at job
     */
    interface ArrivedAtJobCallback {
        fun onJobArrived()
        fun onJobArriveFailed()
    }

    /**
     * Callback interface for start job
     */
    interface StartJobCallback {
        fun onJobStarted()
        fun onJobStartFailed()
    }

    /**
     * Callback interface for cancel job
     */
    interface CancelJobCallback {
        fun onJobCancelled()
        fun onJobCancelFailed()
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
        fun onJobConcluded(it: ConcludeJobBadResponse)

        /**
         * On job conclude success
         */
        fun onJobConcludeFailed(message: String?, code: Int?)
    }
}
