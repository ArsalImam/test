package com.bykea.pk.partner.dal.source.remote

import android.util.Log
import com.bykea.pk.partner.dal.source.JobsDataSource
import com.bykea.pk.partner.dal.source.remote.request.*
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
    fun pickJob(jobRequestId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.AcceptJobRequestCallback) {
        Backend.loadboard.pickJob(driverId, token, jobRequestId, PickJobRequest(lat, lng)).enqueue(object : Callback<PickJobResponse> {

            override fun onFailure(call: Call<PickJobResponse>, t: Throwable) {
                callback.onJobRequestAcceptFailed(t.message, false)
            }

            override fun onResponse(call: Call<PickJobResponse>, response: Response<PickJobResponse>) {
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
            override fun onFailure(call: Call<AckJobCallResponse>, t: Throwable) {
                callback.onJobCallAcknowledgeFailed()
            }

            override fun onResponse(call: Call<AckJobCallResponse>, response: Response<AckJobCallResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess()) callback.onJobCallAcknowledged()
                        else callback.onJobCallAcknowledgeFailed()
                    }
                } else {
                    callback.onJobCallAcknowledgeFailed()
                }
            }
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
            override fun onFailure(call: Call<AcceptJobCallResponse>, t: Throwable) {
                callback.onJobAcceptFailed()
            }

            override fun onResponse(call: Call<AcceptJobCallResponse>, response: Response<AcceptJobCallResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess()) callback.onJobAccepted()
                        else callback.onJobAcceptFailed()
                    }
                } else {
                    callback.onJobAcceptFailed()
                }
            }
        })
    }

    fun arrivedAtJob(jobId: String, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.ArrivedAtJobCallback) {
        Backend.talos.arrivedForJob(jobId, ArrivedAtJobRequest(driverId, token, lat, lng)).enqueue(object : Callback<ArriveAtJobResponse> {
            override fun onFailure(call: Call<ArriveAtJobResponse>, t: Throwable) {
                callback.onJobArriveFailed()
            }

            override fun onResponse(call: Call<ArriveAtJobResponse>, response: Response<ArriveAtJobResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess()) callback.onJobArrived()
                        else callback.onJobArriveFailed()
                    }
                } else {
                    callback.onJobArriveFailed()
                }
            }
        })
    }

    fun startJob(jobId: String, address: String, driverId: String, token: String, lat: Double, lng: Double, callback: JobsDataSource.StartJobCallback) {
        Backend.talos.startJob(jobId, StartJobRequest(driverId, token, lat, lng, address)).enqueue(object : Callback<StartJobResponse> {
            override fun onFailure(call: Call<StartJobResponse>, t: Throwable) {
                callback.onJobStartFailed()
            }

            override fun onResponse(call: Call<StartJobResponse>, response: Response<StartJobResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess()) callback.onJobStarted()
                        else callback.onJobStartFailed()
                    }
                } else {
                    callback.onJobStartFailed()
                }
            }
        })
    }

    fun cancelJob() {
        TODO("not implemented")
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
     * Finish job to remote data source
     *
     * @param jobId Job Id
     * @param requestBody Request body
     * @param callback Response callback
     */
    fun concludeJob(jobId: String, requestBody: ConcludeJobRequest, callback: JobsDataSource.ConcludeJobCallback) {
        Backend.talos.concludeJob(jobId, requestBody).enqueue(object : Callback<ConcludeJobBadResponse> {
            override fun onResponse(call: Call<ConcludeJobBadResponse>, response: Response<ConcludeJobBadResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess()) {
                            callback.onJobConcluded(it)
                        } else {
                            callback.onJobConcludeFailed(it.message, it.code)
                        }
                    }
                } else {
                    callback.onJobConcludeFailed(response.message(), response.code())
                }
            }

            override fun onFailure(call: Call<ConcludeJobBadResponse>, t: Throwable) {
                callback.onJobConcludeFailed(t.message, t.hashCode())
            }
        })
    }
}
