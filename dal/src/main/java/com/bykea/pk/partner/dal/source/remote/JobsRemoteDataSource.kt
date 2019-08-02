package com.bykea.pk.partner.dal.source.remote

import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.remote.request.*
import com.bykea.pk.partner.dal.source.remote.response.*

class JobsRemoteDataSource {

    /**
     * Fetch job listing from remote data source
     *
     * @param driverId Driver Id
     * @param token User access token
     * @param lat User's location latitude
     * @param lng User's location longitude
     * @param limit Number of items to be fetched
     * @param callback Callback to be executed on response from remote data source
     */
    fun getJobs(driverId: String, token: String, lat: Double, lng: Double, serviceCode: Int?, limit: Int, callback: JobsDataSource.LoadJobsCallback) {
        Backend.loadboard.getJobs(driverId, token, lat, lng, serviceCode).enqueue(object : Callback<GetJobRequestListResponse> {
            override fun onSuccess(response: GetJobRequestListResponse) = callback.onJobsLoaded(response.data)
            override fun onFail(code: Int, message: String?) = callback.onDataNotAvailable(message)
        })
    }

    /**
     * Fetch job details from remote data source
     *
     * @param bookingId Booking id whose detail to be fetched
     * @param driverId Driver Id
     * @param token User access token
     * @param lat User's location latitude
     * @param lng User's location longitude
     * @param callback Callback to be executed on response from remote data source
     */
    fun getJob(bookingId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.GetJobRequestCallback) {
        Backend.loadboard.getJob(driverId, token, bookingId, lat, lng).enqueue(object : Callback<GetJobRequestDetailResponse> {
            override fun onSuccess(response: GetJobRequestDetailResponse) = callback.onJobLoaded(response.data)
            override fun onFail(code: Int, message: String?) = callback.onDataNotAvailable(message)
        })
    }

    /**
     * Accept job request
     *
     * @param jobRequestId Id of Booking to be accepted
     */
    fun pickJob(jobRequestId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.AcceptJobRequestCallback) {
        Backend.loadboard.pickJob(driverId, token, jobRequestId, PickJobRequest(lat, lng)).enqueue(object : Callback<PickJobResponse> {
            override fun onSuccess(response: PickJobResponse) = callback.onJobRequestAccepted()
            override fun onFail(code: Int, message: String?) = callback.onJobRequestAcceptFailed(code, message)
        })
    }

    /**
     * Requests acknowledgement on receiving job call
     * @param jobId Job ID
     * @param driverId Driver ID
     * @param token Session token
     * @param lat Driver's current lat
     * @param lng Driver's current lng
     * @param callback AckJobCallCallback
     */
    fun acknowledgeJobCall(jobId: String, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.AckJobCallCallback) {
        Backend.talos.acknowledgeJobCall(jobId, AckJobCallRequest(driverId, token, lat, lng)).enqueue(object : Callback<AckJobCallResponse> {
            override fun onSuccess(response: AckJobCallResponse) = callback.onJobCallAcknowledged()
            override fun onFail(code: Int, message: String?) = callback.onJobCallAcknowledgeFailed()
        })
    }

    /**
     * Requests accept job call
     * @param jobId Job ID
     * @param timeEclipsed Int
     * @param driverId Driver ID
     * @param token Session token
     * @param lat Driver's current lat
     * @param lng Driver's current lng
     * @param callback AcceptJobCallback
     */
    fun acceptJob(jobId: String, timeEclipsed: Int, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.AcceptJobCallback) {
        Backend.talos.acceptJobCall(jobId, AcceptJobRequest(driverId, token, lat, lng, timeEclipsed)).enqueue(object : Callback<AcceptJobCallResponse> {
            override fun onSuccess(response: AcceptJobCallResponse) = callback.onJobAccepted()
            override fun onFail(code: Int, message: String?) = callback.onJobAcceptFailed()
        })
    }

    /**
     * Requests to change drop-off location of current job
     * @param jobId Job ID
     * @param driverId Driver ID
     * @param token Driver access token
     * @param dropOff Updated drop-off location
     */
    fun changeDropOff(jobId: String, driverId: String, token: String, dropOff: ChangeDropOffRequest.Stop, callback: JobsDataSource.DropOffChangeCallback) {
        Backend.talos.changeDropOff(jobId, ChangeDropOffRequest(driverId, token, dropOff)).enqueue(object : Callback<AcceptJobCallResponse> {
            override fun onSuccess(response: AcceptJobCallResponse) = callback.onDropOffChanged()
            override fun onFail(code: Int, message: String?) = callback.onDropOffChangeFailed()
        })
    }

    /**
     * Requests to mark arrived for active job
     * @param jobId String
     * @param driverId String
     * @param token String
     * @param lat Double
     * @param lng Double
     * @param callback ArrivedAtJobCallback
     */
    fun arrivedAtJob(jobId: String, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.ArrivedAtJobCallback) {
        Backend.talos.arrivedForJob(jobId, ArrivedAtJobRequest(driverId, token, lat, lng)).enqueue(object : Callback<ArriveAtJobResponse> {
            override fun onSuccess(response: ArriveAtJobResponse) = callback.onJobArrived()
            override fun onFail(code: Int, message: String?) = callback.onJobArriveFailed()
        })
    }

    /**
     * Requests to start active job
     * @param jobId String
     * @param address String
     * @param driverId String
     * @param token String
     * @param lat Double
     * @param lng Double
     * @param callback StartJobCallback
     */
    fun startJob(jobId: String, address: String, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.StartJobCallback) {
        Backend.talos.startJob(jobId, StartJobRequest(driverId, token, lat, lng, address)).enqueue(object : Callback<StartJobResponse> {
            override fun onSuccess(response: StartJobResponse) = callback.onJobStarted()
            override fun onFail(code: Int, message: String?) = callback.onJobStartFailed()
        })
    }

    /**
     * Requests to cancel active job
     * @param jobId String
     * @param driverId String
     * @param token String
     * @param lat Double
     * @param lng Double
     * @param reason String
     * @param callback CancelJobCallback
     */
    fun cancelJob(jobId: String, driverId: String, token: String, lat: Double, lng: Double, reason: String, callback: JobsDataSource.CancelJobCallback) {
        Backend.talos.cancelJob(CancelJobRequest(driverId, token, lat, lng, jobId, reason)).enqueue(object : Callback<CancelJobBadResponse> {
            override fun onSuccess(response: CancelJobBadResponse) = callback.onJobCancelled()
            override fun onFail(code: Int, message: String?) = callback.onJobCancelFailed()
        })
    }

    /**
     * Finish job to remote data source
     *
     * @param jobId Job Id
     * @param requestBody Request body
     * @param callback Response callback
     */
    fun finishJob(jobId: String, requestBody: FinishJobRequest, callback: JobsDataSource.FinishJobCallback) {
        Backend.talos.finishJob(jobId, requestBody).enqueue(object : Callback<FinishJobResponse> {
            override fun onSuccess(response: FinishJobResponse) = callback.onJobFinished(response.data)
            override fun onFail(code: Int, message: String?) = callback.onJobFinishFailed(message)
        })
    }

    /**
     * Finish job to remote data source
     *
     * @param jobId Job Id
     * @param requestBody Request body
     * @param callback Response callback
     */
    fun concludeJob(jobId: String, requestBody: ConcludeJobRequest, callback: JobsDataSource.ConcludeJobCallback) {
        Backend.talos.concludeJob(jobId, requestBody).enqueue(object : Callback<ConcludeJobBadResponse> {
            override fun onSuccess(response: ConcludeJobBadResponse) = callback.onJobConcluded(response)
            override fun onFail(code: Int, message: String?) = callback.onJobConcludeFailed(message, hashCode())
        })
    }
}
