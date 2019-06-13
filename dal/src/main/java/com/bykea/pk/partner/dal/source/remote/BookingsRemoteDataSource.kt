package com.bykea.pk.partner.dal.source.remote

import android.util.Log
import com.bykea.pk.partner.dal.source.BookingsDataSource
import com.bykea.pk.partner.dal.source.remote.request.RequestBodyAcceptBooking
import com.bykea.pk.partner.dal.source.remote.response.GetLoadboardDetailResponse
import com.bykea.pk.partner.dal.source.remote.response.GetLoadboardListingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingsRemoteDataSource {

    fun getBookings(driverId: String, token: String, lat: Double, lng: Double, limit: Int, callback: BookingsDataSource.LoadBookingsCallback) {

        val call = ApiClient.build()?.getLoadboardList(driverId, token, lat, lng)
        call?.enqueue(object : Callback<GetLoadboardListingResponse> {

            override fun onFailure(call: Call<GetLoadboardListingResponse>, t: Throwable) {
                callback.onDataNotAvailable(t.message)
            }

            override fun onResponse(call: Call<GetLoadboardListingResponse>, response: Response<GetLoadboardListingResponse>) {
                response.body()?.let {
                    if (response.isSuccessful && it.isSuccess()) {
                        Log.v(BookingsRemoteDataSource::class.java.simpleName, "data ${it.data}")
                        callback.onBookingsLoaded(it.data)
                    } else {
                        callback.onDataNotAvailable(it.message)
                    }
                }
            }
        })
    }

    fun getBooking(bookingId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: BookingsDataSource.GetBookingCallback) {

        val call = ApiClient.build()?.getLoadboardDetail(driverId, token, bookingId, lat, lng)
        call?.enqueue(object : Callback<GetLoadboardDetailResponse> {

            override fun onFailure(call: Call<GetLoadboardDetailResponse>, t: Throwable) {
                callback.onDataNotAvailable(t.message)
            }

            override fun onResponse(call: Call<GetLoadboardDetailResponse>, response: Response<GetLoadboardDetailResponse>) {
                response.body()?.let {
                    if (response.isSuccessful && it.isSuccess()) {
                        Log.v(BookingsRemoteDataSource::class.java.simpleName, "data ${it.data}")
                        callback.onBookingLoaded(it.data)
                    } else {
                        callback.onDataNotAvailable(it.message)
                    }
                }
            }
        })
    }

    fun acceptBooking(bookingId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: BookingsDataSource.AcceptBookingCallback) {
        val call = ApiClient.build()?.acceptLoadboardBooking(bookingId, driverId, token, RequestBodyAcceptBooking(lat, lng))
        call?.enqueue(object : Callback<GetLoadboardDetailResponse> {

            override fun onFailure(call: Call<GetLoadboardDetailResponse>, t: Throwable) {
                callback.onBookingAcceptFailed(t.message)
            }

            override fun onResponse(call: Call<GetLoadboardDetailResponse>, response: Response<GetLoadboardDetailResponse>) {
                response.body()?.let {
                    if (response.isSuccessful && it.isSuccess()) {
                        callback.onBookingAccepted()
                    } else {
                        callback.onBookingAcceptFailed(it.message)
                    }
                }
            }
        })
    }
}