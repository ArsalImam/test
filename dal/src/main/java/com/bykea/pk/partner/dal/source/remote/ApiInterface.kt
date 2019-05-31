package com.bykea.pk.partner.dal.source.remote

import com.bykea.pk.partner.dal.source.remote.response.GetLoadboardDetailResponse
import com.bykea.pk.partner.dal.source.remote.response.GetLoadboardListingResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 */

interface ApiInterface {

    /**
     * Getting loadboard list of all types in home screen when partner is active.
     * @param driverId Driver id
     * @param token Driver access token
     * @param lat Driver current lat
     * @param lng Driver current lng
     * @param limit jobs limit - OPTIONAL
     * @return Loadboard jobs list
     */
    @GET("/api/v1/driver/loadboard?")
    fun getLoadboardList(@Query("_id") driverId: String,
                         @Query("token_id") token: String,
                         @Query("lat") lat: Double,
                         @Query("lng") lng: Double,
                         @Query("limit") limit: Int): Call<GetLoadboardListingResponse>

    /**
     * Getting loadboard job details .
     * @param driverId Driver id
     * @param token Driver access token
     * @return Loadboard job details
     */
    @GET("/api/v1/driver/loadboard?")
    fun getLoadboardDetail(@Query("_id") driverId: String,
                           @Query("token_id") token: String,
                           @Query("booking_id") bookingId: Long): Call<GetLoadboardDetailResponse>


}