package com.bykea.pk.partner.dal.source.remote

import com.bykea.pk.partner.dal.source.remote.response.GetLoadboardDetailResponse
import com.bykea.pk.partner.dal.source.remote.response.GetLoadboardListingResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

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
    @GET("/api/v1/driver/loadboard")
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
    @GET("/api/v1/driver/loadboard/{booking_id}")
    fun getLoadboardDetail(@Path("booking_id") bookingId: Long,
                           @Query("_id") driverId: String,
                           @Query("token_id") token: String): Call<GetLoadboardDetailResponse>

    @GET
    fun getLoadboardListMock(
            @Url url: String = "http://www.mocky.io/v2/5cfe47333200000f0045efbb",
            @Query("_id") driverId: String,
            @Query("token_id") token: String,
            @Query("lat") lat: Double,
            @Query("lng") lng: Double,
            @Query("limit") limit: Int
    ): Call<GetLoadboardListingResponse>

    @GET
    fun getLoadboardDetailMock(
            @Url url: String = "http://www.mocky.io/v2/5cf0fee8300000c86c00bc06",
            @Query("booking_id") bookingId: Long,
            @Query("_id") driverId: String,
            @Query("token_id") token: String
    ): Call<GetLoadboardDetailResponse>


}