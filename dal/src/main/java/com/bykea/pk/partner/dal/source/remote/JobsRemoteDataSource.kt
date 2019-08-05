package com.bykea.pk.partner.dal.source.remote

import android.util.Log
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.remote.request.AcceptJobRequest
import com.bykea.pk.partner.dal.source.remote.request.FinishJobRequest
import com.bykea.pk.partner.dal.source.remote.response.AcceptJobResponse
import com.bykea.pk.partner.dal.source.remote.response.FinishJobResponse
import com.bykea.pk.partner.dal.source.remote.response.GetJobRequestDetailResponse
import com.bykea.pk.partner.dal.source.remote.response.GetJobRequestListResponse
import com.bykea.pk.partner.dal.source.remote.response.*
import retrofit2.Call
import retrofit2.Callback
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

            override fun onFailure(call: Call<GetJobRequestListResponse>, t: Throwable) {
                callback.onDataNotAvailable(t.message)
            }

            override fun onResponse(call: Call<GetJobRequestListResponse>, response: Response<GetJobRequestListResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess()) {
                            Log.v(JobsRemoteDataSource::class.java.simpleName, "data ${it.data}")
                            callback.onJobsLoaded(it.data)
                        } else {
                            callback.onDataNotAvailable(it.message)
                        }
                    }
                } else {
                    callback.onDataNotAvailable()
                }
            }
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

            override fun onFailure(call: Call<GetJobRequestDetailResponse>, t: Throwable) {
                callback.onDataNotAvailable(t.message)
            }

            override fun onResponse(call: Call<GetJobRequestDetailResponse>, response: Response<GetJobRequestDetailResponse>) {
                response.body()?.let {
                    if (response.isSuccessful && it.isSuccess()) {
                        callback.onJobLoaded(it.data)
                    } else {
                        callback.onDataNotAvailable(it.message)
                    }
                }
            }
        })
    }

    /**
     * Accept job request
     *
     * @param jobRequestId Id of Booking to be accepted
     */
    fun acceptJob(jobRequestId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.AcceptJobRequestCallback) {
        Backend.loadboard.acceptJob(jobRequestId, driverId, token, AcceptJobRequest(lat, lng)).enqueue(object : Callback<AcceptJobResponse> {

            override fun onFailure(call: Call<AcceptJobResponse>, t: Throwable) {
                callback.onJobRequestAcceptFailed(t.message, false)
            }

            override fun onResponse(call: Call<AcceptJobResponse>, response: Response<AcceptJobResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess()) {
                            callback.onJobRequestAccepted()
                        } else {
                            callback.onJobRequestAcceptFailed(it.message, it.data.isTaken())
                        }
                    }
                } else {
                    callback.onJobRequestAcceptFailed("Booking is no longer available", true)
                }
            }
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
        Backend.telos.finishJob(jobId, requestBody).enqueue(object : Callback<FinishJobResponse> {
            override fun onResponse(call: Call<FinishJobResponse>, response: Response<FinishJobResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess()) {
                            callback.onJobFinished(it.data)
                        } else {
                            callback.onJobFinishFailed(it.message)
                        }
                    }
                } else {
                    callback.onJobFinishFailed(response.message())
                }
            }

            override fun onFailure(call: Call<FinishJobResponse>, t: Throwable) {
                callback.onJobFinishFailed(t.message)
            }
        })
    }

    /**
     * Check If Email Id Is Updated from remote data source
     * @param driverId Driver Id
     * @param token User access token
     * @param callback Callback to be executed on response from remote data source
     */
    fun getCheckIsEmailUpdatedRequest(driverId: String, token: String, callback: JobsDataSource.EmailUpdateCheckCallback) {
        Backend.telos.checkIsEmailUpdated(driverId, token).enqueue(object : Callback<CheckEmailUpdateResponse> {
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

    /**
     * Update Email Id from remote data source
     * @param emailId Email id to update
     * @param driverId Driver Id
     * @param token User access token
     * @param callback Callback to be executed on response from remote data source
     */
    fun getEmailUpdateRequest(emailId: String, driverId: String, token: String, callback: JobsDataSource.EmailUpdateCallback) {
        Backend.telos.getEmailUpdate(emailId, driverId, token).enqueue(object : Callback<GetEmailUpdateResponse> {
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

}