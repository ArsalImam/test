package com.bykea.pk.partner.dal.source.remote

import com.bykea.pk.partner.dal.source.remote.request.AcceptJobRequestRequest
import com.bykea.pk.partner.dal.source.remote.response.AcceptJobRequestResponse
import com.bykea.pk.partner.dal.source.remote.response.GetJobRequestDetailResponse
import com.bykea.pk.partner.dal.source.remote.response.GetJobRequestListResponse
import retrofit2.Call
import retrofit2.http.*

/**
 * API interface for Load Board
 *
 * @author Yousuf Sohail
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
    @GET("/v1/bookings")
    fun getJobRequestList(
            @Header("x-lb-user-id") driverId: String,
            @Header("x-lb-user-token") token: String,
            @Query("lat") lat: Double,
            @Query("lng") lng: Double,
            @Query("f_service_code") serviceCode: Int? = null,
            @Query("f_distance") distance: Int = 5,
            @Query("sort") sort: String = "nearby"): Call<GetJobRequestListResponse>

    /**
     * Getting loadboard job details
     * @param driverId Driver id
     * @param token Driver access token
     * @return Loadboard job details
     */
    @GET("/v1/bookings/{booking_id}")
    fun getJobRequestDetail(
            @Header("x-lb-user-id") driverId: String,
            @Header("x-lb-user-token") token: String,
            @Path("booking_id") bookingId: Long,
            @Query("lat") lat: Double,
            @Query("lng") lng: Double): Call<GetJobRequestDetailResponse>

    /**
     * Getting loadboard job details
     * @param driverId Driver id
     * @param token Driver access token
     * @return Loadboard job details
     */
    @POST("/v1/bookings/{booking_id}/assign")
    fun acceptJobRequest(
            @Path("booking_id") bookingId: Long,
            @Header("x-lb-user-id") driverId: String,
            @Header("x-lb-user-token") token: String,
            @Body body: AcceptJobRequestRequest): Call<AcceptJobRequestResponse>


    @GET
    fun getMockJobRequestList(
            @Url url: String = "http://www.mocky.io/v2/5cfe4cb33200000f0045efe2",
            @Query("_id") driverId: String,
            @Query("token_id") token: String,
            @Query("lat") lat: Double,
            @Query("lng") lng: Double,
            @Query("limit") limit: Int): Call<GetJobRequestListResponse>

    @GET
    fun getMockJobRequestDetail(
            @Url url: String = "http://www.mocky.io/v2/5cf0fee8300000c86c00bc06",
            @Query("booking_id") bookingId: Long,
            @Query("_id") driverId: String,
            @Query("token_id") token: String): Call<GetJobRequestDetailResponse>

}