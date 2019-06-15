package com.bykea.pk.partner.dal.source.remote

import android.util.Log
import com.bykea.pk.partner.dal.source.BookingsDataSource
import com.bykea.pk.partner.dal.source.remote.response.GetLoadboardDetailResponse
import com.bykea.pk.partner.dal.source.remote.response.GetLoadboardListingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingsRemoteDataSource {

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
    fun getBooking(bookingId: Long, driverId: String, token: String, lat: Double, lng: Double, callback: BookingsDataSource.GetBookingCallback) {

        val call = ApiClient.build()?.getLoadboardDetail(bookingId, driverId, token, lat, lng)
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
                        callback.onDataNotAvailable(it.msg)
                    }
                }
            }
        })
    }

    /**
     * Accept Booking
     *
     * @param bookingId Id of Booking to be accepted
     */
    fun acceptBooking(bookingId: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}