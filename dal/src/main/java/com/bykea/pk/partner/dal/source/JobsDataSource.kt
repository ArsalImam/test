package com.bykea.pk.partner.dal.source

import com.bykea.pk.partner.dal.Job
import com.bykea.pk.partner.dal.LocCoordinatesInTrip
import com.bykea.pk.partner.dal.source.remote.request.ChangeDropOffRequest
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateRequestObject
import com.bykea.pk.partner.dal.source.remote.response.*

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
     * Requests to change drop-off location
     * @param jobId Job ID
     * @param dropOff Drop off stop
     * @param callback DropOffChangeCallback
     */
    fun changeDropOff(jobId: String, dropOff: ChangeDropOffRequest.Stop, callback: DropOffChangeCallback)

    /**
     * Requests arrived at job
     * @param jobId Job ID
     * @param callback ArrivedAtJobCallback
     */
    fun arrivedAtJob(jobId: String, route: ArrayList<LocCoordinatesInTrip>, callback: ArrivedAtJobCallback)

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

        fun onJobRequestAcceptFailed(code: Int, message: String?)
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

        /**
         * Will be called on job accept success
         */
        fun onJobAccepted()

        /**
         * Will be called on job accept failure
         */
        fun onJobAcceptFailed()
    }

    /**
     * Callback interface for drop off change of job
     */
    interface DropOffChangeCallback {

        /**
         * Will be called on drop off change success
         */
        fun onDropOffChanged()

        /**
         * Will be called on drop off change failure
         */
        fun onDropOffChangeFailed()
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
         * On job conclude fail
         */
        fun onJobConcludeFailed(message: String?, code: Int?)
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

    /**
     * Get fair estimation
     * @param callback to get results in case of failure or success
     */
    fun getFairEstimation(startLat: String, startLng: String, endLat: String, endLng: String,
                          type: String, rideType: String, callback: FareEstimationCallback) {
    }

    /**
     * Callback interface to get fair estimation
     */
    interface FareEstimationCallback {
        fun onSuccess(fareEstimationResponse: FareEstimationResponse)

        fun onFail(code: Int, subCode: Int?, message: String?) {}
    }

    /**
     * Generate OTP
     * @param callback to get results in case of failure or success
     */
    fun requestOtpGenerate(phone: String, type: String, callback: OtpGenerateCallback) {}

    /**
     * Callback interface to otp generate
     */
    interface OtpGenerateCallback {
        fun onSuccess(verifyNumberResponse: VerifyNumberResponse)

        fun onFail(code: Int, subCode: Int?, message: String?) {}
    }

    /**
     * Create Trip and Verify OTP
     * @param callback to get results in case of failure or success
     */
    fun createTrip(rideCreateRequestObject: RideCreateRequestObject, callback: CreateTripCallback) {}

    /**
     * Callback interface to otp generate
     */
    interface CreateTripCallback {
        fun onSuccess(rideCreateResponse: RideCreateResponse)

        fun onFail(code: Int, subCode: Int?, message: String?) {}
    }

}
