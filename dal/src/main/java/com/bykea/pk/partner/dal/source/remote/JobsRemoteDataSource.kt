package com.bykea.pk.partner.dal.source.remote

import com.bykea.pk.partner.dal.LocCoordinatesInTrip
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.remote.request.*
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailAddEditRequest
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateRequestObject
import com.bykea.pk.partner.dal.source.remote.response.*
import com.bykea.pk.partner.dal.util.AvailableTripStatus
import com.bykea.pk.partner.dal.util.RolesByName
import retrofit2.Call
import retrofit2.Response

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
            override fun onFail(code: Int, message: String?) = callback.onDataNotAvailable(code, message)
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
    fun arrivedAtJob(jobId: String, route: ArrayList<LocCoordinatesInTrip>, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.ArrivedAtJobCallback) {
        Backend.talos.arrivedForJob(jobId, ArrivedAtJobRequest(driverId, token, lat, lng, route)).enqueue(object : Callback<ArriveAtJobResponse> {
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
            override fun onFail(code: Int, message: String?) = callback.onJobStartFailed(message)
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
        Backend.talos.finishJob(jobId, requestBody).enqueue(object : LoggerCallback<FinishJobResponse> {
            override fun onSuccess(response: FinishJobResponse, request: String, resp: String) =
                    callback.onJobFinished(response.data, request, resp)

            override fun onFail(code: Int, message: String?) = callback.onJobFinishFailed(message, code)
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

    //TODO: move this method to user repo
    /**
     * Check If Email Id Is Updated from remote data source
     * @param driverId Driver Id
     * @param token User access token
     * @param callback Callback to be executed on response from remote data source
     */
    fun getCheckIsEmailUpdatedRequest(driverId: String, token: String, callback: JobsDataSource.EmailUpdateCheckCallback) {
        Backend.talos.checkIsEmailUpdated(driverId, token).enqueue(object : retrofit2.Callback<CheckEmailUpdateResponse> {
            override fun onResponse(call: Call<CheckEmailUpdateResponse>, response: Response<CheckEmailUpdateResponse>) {
                response.body()?.let {
                    if (response.isSuccessful && it.isSuccess()) {
                        callback.onSuccess(it.email_updated)
                    } else {
                        callback.onFail(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<CheckEmailUpdateResponse>, t: Throwable) {
                callback.onFail("Email not updated")
            }
        })
    }

    //TODO: move this method to user repo
    /**
     * Update Email Id from remote data source
     * @param emailId Email id to update
     * @param driverId Driver Id
     * @param token User access token
     * @param callback Callback to be executed on response from remote data source
     */
    fun getEmailUpdateRequest(emailId: String, driverId: String, token: String, callback: JobsDataSource.EmailUpdateCallback) {
        Backend.talos.getEmailUpdate(emailId, driverId, token).enqueue(object : retrofit2.Callback<GetEmailUpdateResponse> {
            override fun onResponse(call: Call<GetEmailUpdateResponse>, response: Response<GetEmailUpdateResponse>) {
                response.body()?.let {
                    if (response.isSuccessful && it.isSuccess()) {
                        callback.onSuccess()
                    } else {
                        callback.onFail(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<GetEmailUpdateResponse>, t: Throwable) {
                callback.onFail("Email not updated")
            }
        })
    }


    /**
     * @param userId Logged In Driver ID
     * @param tokenId Logged In User
     * @param startLat Start Location - Latitude
     * @param startLng Start Location - Longitude
     * @param endLat End Location - Latitude
     * @param endLng End Location - Longitude
     * @param distance Distance Between Start Location To End Location
     * @param time Time Start Location To End Location
     * @param type
     * @param rideType Ride Type
     * @param callback to get results in case of failure or success
     */
    fun requestFairEstimation(driverId: String, accessToken: String, startLat: String, startLng: String, endLat: String, endLng: String, serviceCode: Int, callback: JobsDataSource.FareEstimationCallback) {
        Backend.talos.requestFareEstimation(driverId, accessToken,
                startLat, startLng, endLat, endLng, serviceCode).enqueue(object : Callback<FareEstimationResponse> {
            override fun onSuccess(response: FareEstimationResponse) {
                callback.onSuccess(response)
            }

            override fun onFail(code: Int, subCode: Int?, message: String?) {
                callback.onFail(code, subCode, message)
            }
        })
    }

    fun requestOtpGenerate(_id: String, tokenId: String, phone: String, type: String, callback: JobsDataSource.OtpGenerateCallback) {
        Backend.talos.generateDriverOTP(_id, tokenId, phone, type).enqueue(object : Callback<VerifyNumberResponse> {
            override fun onSuccess(response: VerifyNumberResponse) {
                callback.onSuccess(response)
            }

            override fun onFail(code: Int, subCode: Int?, message: String?) {
                callback.onFail(code, subCode, message)
            }
        })
    }

    fun createTrip(rideCreateRequestObject: RideCreateRequestObject, callback: JobsDataSource.CreateTripCallback) {
        Backend.talos.initiateRide(rideCreateRequestObject).enqueue(object : Callback<RideCreateResponse> {
            override fun onSuccess(response: RideCreateResponse) {
                callback.onSuccess(response)
            }

            override fun onFail(code: Int, subCode: Int?, message: String?) {
                callback.onFail(code, subCode, message)
            }
        })
    }


    /**
     * Finish job to remote data source
     *
     * @param jobId Job Id
     * @param requestBodyBykeaCash Request body
     * @param callbackBykeaCash Response callbackBykeaCash
     */
    fun updateBookingDetails(tripId: String, requestBodyBykeaCash: UpdateBykeaCashBookingRequest, callbackBykeaCash: JobsDataSource.UpdateBykeaCashBookingCallback) {
        Backend.talos.updateBookingDetails(tripId, requestBodyBykeaCash).enqueue(object : Callback<UpdateBykeaCashBookingResponse> {
            override fun onSuccess(responseBykeaCash: UpdateBykeaCashBookingResponse) = callbackBykeaCash.onSuccess(responseBykeaCash)
            override fun onFail(code: Int, subCode: Int?, message: String?) = callbackBykeaCash.onFail(code, subCode, message)
        })
    }

    fun getJobComplainReasons(userType: String, messageType: String, lang: String, callback: JobsDataSource.ComplainReasonsCallback) {
        Backend.talos.getJobComplainReasons(userType, messageType, lang).enqueue(object : Callback<ComplainReasonResponse> {
            override fun onSuccess(complainReasonResponse: ComplainReasonResponse) = callback.onSuccess(complainReasonResponse)
            override fun onFail(code: Int, subCode: Int?, message: String?) = callback.onFail(code, subCode, message)
        })
    }

    /**
     * Get Booking Details By Id
     *
     * @param bookingUrl for kronos
     * @param callback to return data after API call succeed
     */
    fun getBookingDetailsById(bookingUrl: String, callback: JobsDataSource.GetBookingDetailCallback) {
        Backend.talos.getBookingDetailsById(bookingUrl).enqueue(object : Callback<BookingDetailResponse> {
            override fun onSuccess(responseBykeaCash: BookingDetailResponse) = callback.onSuccess(responseBykeaCash)
            override fun onFail(code: Int, message: String?) = callback.onFail(code, message)
        })
    }


    /**
     * Requests to cancel active job
     * @param jobId String
     * @param driverId String
     * @param token String
     * @param callback CancelJobCallback
     */
    fun skipJob(jobId: String, driverId: String, token: String, callback: JobsDataSource.SkipJobCallback) {
        Backend.talos.skipJobRequest(jobId, SkipJobRequest(driverId, token)).enqueue(object : Callback<SkipJobResponse> {
            override fun onSuccess(response: SkipJobResponse) = callback.onJobSkip()
            override fun onFail(code: Int, message: String?) = callback.onJobSkipFailed()
        })
    }

    fun pushTripDetails(jobId: String, filePath: String, driverId: String, accessToken: String, callback: JobsDataSource.PushTripDetailCallback) {
        Backend.talos.pushTripDetails(jobId, PushJobDetailsRequest(driverId, accessToken, arrayOf(filePath))).enqueue(object : Callback<BaseResponse> {
            override fun onSuccess(response: BaseResponse) = callback.onSuccess()
            override fun onFail(code: Int, message: String?) = callback.onFail(code, message)
        })
    }

    fun cancelMultiDeliveryBatchJob(jobId: String, reason: String, driverId: String, accessToken: String, lat: Double, lng: Double, callback: JobsDataSource.CancelBatchCallback) {
        Backend.talos.cancelMultiDeliveryBatchJob(jobId, CancelBatchJobRequest(driverId, accessToken, lat, lng, reason)).enqueue(object : Callback<BaseResponse> {
            override fun onSuccess(response: BaseResponse) = callback.onJobCancel()
            override fun onFail(code: Int, message: String?) = callback.onJobCancelFailed()
        })
    }

    /**
     * this method can be used to fetch invoice details against the booking id
     *
     * [invoiceUrl] url of the api, will be received from settings
     * [callback] this will response back on data/error received
     */
    fun getInvoiceDetails(invoiceUrl: String, callback: JobsDataSource.GetInvoiceCallback) {
        Backend.talos.getInvoiceDetails(invoiceUrl, RolesByName.CANCEL_BY_PARTNER.toLowerCase(),
                AvailableTripStatus.STATUS_FINISH).enqueue(object : Callback<FeedbackInvoiceResponse> {
            override fun onSuccess(response: FeedbackInvoiceResponse) = callback.onInvoiceDataLoaded(response)
            override fun onFail(code: Int, message: String?) = callback.onInvoiceDataFailed(message)
        })
    }

    fun submitTemperature(submitTemperatureSubmitRequest: TemperatureSubmitRequest, callback: JobsDataSource.LoadDataCallback<TemperatureSubmitResponse>) {
        Backend.talos.submitTemperature(submitTemperatureSubmitRequest).enqueue(object : Callback<TemperatureSubmitResponse> {
            override fun onSuccess(response: TemperatureSubmitResponse) = callback.onDataLoaded(response)
            override fun onFail(code: Int, message: String?) = callback.onDataNotAvailable(code, message.toString())

        })
    }

    fun getAllDeliveryDetails(driverId: String, accessToken: String, batchID: String, callback: JobsDataSource.LoadDataCallback<DeliveryDetailListResponse>) {
        Backend.talos.getAllDeliveryDetails(driverId, accessToken, batchID).enqueue(object : Callback<DeliveryDetailListResponse> {
            override fun onSuccess(response: DeliveryDetailListResponse) = callback.onDataLoaded(response)
            override fun onFail(code: Int, message: String?) = callback.onDataNotAvailable(code, message.toString())
        })
    }

    fun addDeliveryDetails(driverId: String, accessToken: String, batchID: String, deliveryDetailAddEditRequest: DeliveryDetailAddEditRequest, callback: JobsDataSource.LoadDataCallback<DeliveryDetailAddEditResponse>) {
        Backend.talos.addDeliveryDetail(driverId, accessToken, batchID, deliveryDetailAddEditRequest).enqueue(object : Callback<DeliveryDetailAddEditResponse> {
            override fun onSuccess(response: DeliveryDetailAddEditResponse) = callback.onDataLoaded(response)
            override fun onFail(code: Int, message: String?) = callback.onDataNotAvailable(code, message.toString())
        })
    }

    fun updateDeliveryDetails(driverId: String, accessToken: String, batchID: String, bookingId: String, deliveryDetailAddEditRequest: DeliveryDetailAddEditRequest, callback: JobsDataSource.LoadDataCallback<DeliveryDetailAddEditResponse>) {
        Backend.talos.updateDeliveryDetail(driverId, accessToken, batchID, bookingId, deliveryDetailAddEditRequest).enqueue(object : Callback<DeliveryDetailAddEditResponse> {
            override fun onSuccess(response: DeliveryDetailAddEditResponse) = callback.onDataLoaded(response)
            override fun onFail(code: Int, message: String?) = callback.onDataNotAvailable(code, message.toString())
        })
    }

    fun removeDeliveryDetails(driverId: String, accessToken: String, batchID: String, bookingId: String, callback: JobsDataSource.LoadDataCallback<DeliveryDetailRemoveResponse>) {
        Backend.talos.removeDeliveryDetail(driverId, accessToken, batchID, bookingId).enqueue(object : Callback<DeliveryDetailRemoveResponse> {
            override fun onSuccess(response: DeliveryDetailRemoveResponse) = callback.onDataLoaded(response)
            override fun onFail(code: Int, message: String?) = callback.onDataNotAvailable(code, message.toString())
        })
    }
}