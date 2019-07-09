package com.bykea.pk.partner.dal.source.remote

import android.util.Log
import com.bykea.pk.partner.dal.source.JobRequestsDataSource
import com.bykea.pk.partner.dal.source.remote.request.AcceptJobRequestRequest
import com.bykea.pk.partner.dal.source.remote.response.AcceptJobRequestResponse
import com.bykea.pk.partner.dal.source.remote.response.GetJobRequestDetailResponse
import com.bykea.pk.partner.dal.source.remote.response.GetJobRequestListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobRequestsRemoteDataSource {

    /**
     * Fetch Booking listing from remote data source
     *
     * @param driverId Driver Id
     * @param token User access token
     * @param lat User's location latitude
     * @param lng User's location longitude
     * @param limit Number of items to be fetched
     * @param callback Callback to be executed on response from remote data source
     */
    fun getJobRequests(driverId: String, token: String, lat: Double, lng: Double, serviceCode: Int?, limit: Int, callback: JobRequestsDataSource.LoadJobRequestsCallback) {

        Backend.loadboard.getJobRequestList(driverId, token, lat, lng, serviceCode).enqueue(object : Callback<GetJobRequestListResponse> {

            override fun onFailure(call: Call<GetJobRequestListResponse>, t: Throwable) {
                callback.onDataNotAvailable(t.message)
            }

            override fun onResponse(call: Call<GetJobRequestListResponse>, response: Response<GetJobRequestListResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (response.isSuccessful && it.isSuccess()) {
                            Log.v(JobRequestsRemoteDataSource::class.java.simpleName, "data ${it.data}")
                            callback.onJobRequestsLoaded(it.data)
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
     * Fetch Booking details from remote data source
     *
     * @param bookingId Booking id whose detail to be fetched
     * @param driverId Driver Id
     * @param token User access token
     * @param lat User's location latitude
     * @param lng User's location longitude
     * @param callback Callback to be executed on response from remote data source
     */
    fun getJobRequest(bookingId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: JobRequestsDataSource.GetJobRequestCallback) {

        Backend.loadboard.getJobRequestDetail(driverId, token, bookingId, lat, lng).enqueue(object : Callback<GetJobRequestDetailResponse> {

            override fun onFailure(call: Call<GetJobRequestDetailResponse>, t: Throwable) {
                callback.onDataNotAvailable(t.message)
            }

            override fun onResponse(call: Call<GetJobRequestDetailResponse>, response: Response<GetJobRequestDetailResponse>) {
                response.body()?.let {
                    if (response.isSuccessful && it.isSuccess()) {
                        Log.v(JobRequestsRemoteDataSource::class.java.simpleName, "data ${it.data}")
                        callback.onJobRequestLoaded(it.data)
                    } else {
                        callback.onDataNotAvailable(it.message)
                    }
                }
            }
        })
    }


    /**
     * Accept Job Request
     *
     * @param jobRequestId Id of Booking to be accepted
     */
    fun acceptJobRequest(jobRequestId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: JobRequestsDataSource.AcceptJobRequestCallback) {
        Backend.loadboard.acceptJobRequest(jobRequestId, driverId, token, AcceptJobRequestRequest(lat, lng)).enqueue(object : Callback<AcceptJobRequestResponse> {

            override fun onFailure(call: Call<AcceptJobRequestResponse>, t: Throwable) {
                callback.onJobRequestAcceptFailed(t.message, false)
            }

            override fun onResponse(call: Call<AcceptJobRequestResponse>, response: Response<AcceptJobRequestResponse>) {
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
}
