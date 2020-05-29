package com.bykea.pk.partner.dal.source.remote

import com.bykea.pk.partner.dal.BuildConfig
import com.bykea.pk.partner.dal.source.Fields
import com.bykea.pk.partner.dal.source.remote.request.*
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.BatchUpdateReturnRunRequest
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.source.remote.request.ride.RideCreateRequestObject
import com.bykea.pk.partner.dal.source.remote.response.*
import com.bykea.pk.partner.dal.util.DIGIT_TWENTY
import com.bykea.pk.partner.dal.util.RequestParams
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 * Interface to communicate to Bykea's REST server
 *
 * @author Yousuf Sohail
 */
interface Backend {

    // region User related endpoints

    @GET("/api/v1/driver/getProfile")
    fun getDriverProfile(@Query("_id") _id: String,
                         @Query("token_id") token_id: String,
                         @Query("user_type") userType: String): Call<GetDriverProfile>

    /**
     * Get Driver Email Update
     * @param email Driver email
     * @param _id Driver id
     * @param token_id Driver access token
     * @return Email is successfully update or not
     */
    @FormUrlEncoded
    @PUT("/api/v1/driver/update/email")
    fun getEmailUpdate(
            @Field("email") emailId: String,
            @Field("_id") driverId: String,
            @Field("token_id") token: String): Call<GetEmailUpdateResponse>

    /**
     * Get Is Driver Email Update
     * @param _id Driver id
     * @param token_id Driver access token
     * @return true if updated
     */
    @GET("/api/v1/driver/check/email")
    fun checkIsEmailUpdated(
            @Query("_id") driverId: String,
            @Query("token_id") token: String): Call<CheckEmailUpdateResponse>

    //endregion

    // region Location related endpoints

    fun iAmHere()

    // endregion

    //region Job related endpoints

    /**
     * Requests the list of job request
     * @param driverId Driver ID
     * @param token Driver access token
     * @param lat Driver's current lat
     * @param lng Driver current lng
     * @param serviceCode Service code to filter list with
     * @param distance Radius within which job request to be fetched
     * @param sort Sorting filter
     * @return Call<GetJobRequestListResponse>
     */
    @GET("/v1/bookings")
    fun getJobs(
            @Header("x-lb-user-id") driverId: String,
            @Header("x-lb-user-token") token: String,
            @Query("lat") lat: Double,
            @Query("lng") lng: Double,
            @Query("f_service_code") serviceCode: Int? = null,
            @Query("f_distance") distance: Int = 5,
            @Query("sort") sort: String = "nearby"): Call<GetJobRequestListResponse>

    /**
     * Requests job request detail
     * @param driverId Driver ID
     * @param token Driver access token
     * @param jobRequestId Job Request ID to be fetched
     * @param lat Driver's current lat
     * @param lng Driver current lng
     * @return Call<GetJobRequestDetailResponse>
     */
    @GET("/v1/bookings/{job_request_id}")
    fun getJob(
            @Header("x-lb-user-id") driverId: String,
            @Header("x-lb-user-token") token: String,
            @Path("job_request_id") jobRequestId: Long,
            @Query("lat") lat: Double,
            @Query("lng") lng: Double): Call<GetJobRequestDetailResponse>

    /**
     * Requests Picking/self-assigning job request
     * @param driverId Driver ID
     * @param token Driver access token
     * @param jobRequestId Job Request ID
     * @param body AcceptJobRequest
     * @return Call<AcceptJobResponse>
     */
    @POST("/v1/bookings/{job_request_id}/assign")
    fun pickJob(
            @Header("x-lb-user-id") driverId: String,
            @Header("x-lb-user-token") token: String,
            @Path("job_request_id") jobRequestId: Long,
            @Body body: PickJobRequest): Call<PickJobResponse>

    /**
     * Requests acknowledgement on receiving job call
     * @param jobId Job ID
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @POST("/api/v1/trips/{job_id}/acknowledgement")
    fun acknowledgeJobCall(@Path("job_id") jobId: String, @Body body: AckJobCallRequest): Call<AckJobCallResponse>

    /**
     * Requests to accept job call
     * @param jobId Job ID
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @POST("/api/v1/trips/{job_id}/accept")
    fun acceptJobCall(@Path("job_id") jobId: String, @Body body: AcceptJobRequest): Call<AcceptJobCallResponse>

    /**
     * Requests active job details
     * @param driverId Driver ID
     * @param token Driver access token
     */
//    @GET("/api/v1/driver/activeTrip")
//    fun getActiveJob(@Query("_id") driverId: String, @Query("token_id") token: String): Call<GetActiveJobResponse>

    /**
     * Requests to change job drop-off
     * @param jobId Job ID
     * @param body ChangeDropOffRequest
     * @return Call<AcceptJobCallResponse>
     */
    @PUT("/api/v1/trips/{job_id}/dropoff/partner")
    fun changeDropOff(@Path("job_id") jobId: String, @Body body: ChangeDropOffRequest): Call<AcceptJobCallResponse>

    /**
     * Requests to mark arrived for active job
     * @param jobId Job ID
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @POST("/api/v1/trips/{job_id}/arrived")
    fun arrivedForJob(@Path("job_id") jobId: String, @Body body: ArrivedAtJobRequest): Call<ArriveAtJobResponse>

    /**
     * Requests to mark arrived for active job
     * @param jobId Job ID
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @POST("/v2/batch/{batch_id}/arrived")
    fun arrivedAtJobForBatch(@Header("x-app-partner-id") driverId: String,
                             @Header("x-app-partner-token") token: String,
                             @Path("batch_id") batchId: String, @Body body: ArrivedAtJobRequest): Call<ArriveAtJobResponse>

    /**
     * Requests to start active job
     * @param jobId Job ID
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @POST("/api/v1/trips/{job_id}/start")
    fun startJob(@Path("job_id") jobId: String, @Body body: StartJobRequest): Call<StartJobResponse>

    /**
     * Requests to start active job
     * @param jobId Job ID
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @POST("/v2/batch/{batch_id}/start")
    fun startJobForBatch(@Header("x-app-partner-id") driverId: String,
                         @Header("x-app-partner-token") token: String,
                         @Path("batch_id") batchId: String, @Body body: StartJobRequest): Call<StartJobResponse>

    /**
     * Requests to cancel active job
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @POST("/api/v1/driver/cancel")
    fun cancelJob(@Body body: CancelJobRequest): Call<CancelJobBadResponse>

    /**
     * Requests to cancel active job
     * @param batchId batch id of the job
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @PUT("/v2/batch/{batch_id}/partner/cancel")
    fun cancelJobForBatch(
            @Header("x-app-partner-id") driverId: String, @Header("x-app-partner-token") token: String,
            @Path("batch_id") batchId: String, @Body body: CancelJobRequest): Call<CancelJobBadResponse>

    /**
     * Requests to finish active job
     * @param jobId Job ID
     * @param body FinishJobRequest
     * @return Call<FinishJobResponse>
     */
    @POST("/api/v1/trips/{job_id}/finish")
    fun finishJob(@Path("job_id") jobId: String, @Body body: FinishJobRequest): Call<FinishJobResponse>

    /**
     * Requests to conclude active job including feedback
     * @param jobId Job ID
     * @param body AcceptJobRequest
     * @return Call<BaseResponse>
     */
    @POST("/api/v1/trips/{job_id}/feedback")
    fun concludeJob(@Path("job_id") jobId: String, @Body body: ConcludeJobRequest): Call<ConcludeJobBadResponse>

    //endregion

    // region Withdraw related endpoints

    @GET("/api/v1/driver/paymentmethods")
    fun getWithdrawalPaymentMethods(
            @Query("token_id") token: String,
            @Query("_id") driverId: String
    ): Call<GetWithdrawalPaymentMethods>

    @PUT("/api/v1/driver/withdrawal")
    @FormUrlEncoded
    fun getPerformWithdraw(
            @Field("token_id") token: String,
            @Field("_id") driverId: String,
            @Field("payment_method") paymentMethod: Number,
            @Field("amount") amount: Number
    ): Call<WithdrawPostResponse>

    //endregion

    // region Mock endpoints

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

    //endregion


    @GET("/api/v1/fare/estimate/partner")
    fun requestFareEstimation(@Query(Fields.FareEstimation.ID) id: String,
                              @Query(Fields.FareEstimation.TOKEN_ID) tokenId: String,
                              @Query(Fields.FareEstimation.START_LAT) startLat: String,
                              @Query(Fields.FareEstimation.START_LNG) startLng: String,
                              @Query(Fields.FareEstimation.END_LAT) endLat: String,
                              @Query(Fields.FareEstimation.END_LNG) endLng: String,
                              @Query(Fields.FareEstimation.SERVICE_CODE) serviceCode: Int): Call<FareEstimationResponse>

    @FormUrlEncoded
    @POST("/api/v1/driver/offline/ride/otp")
    fun generateDriverOTP(@Field(Fields.OtpSend.ID) id: String,
                          @Field(Fields.OtpSend.TOKEN_ID) tokenId: String,
                          @Field(Fields.OtpSend.PHONE_NUMBER) phone: String,
                          @Field(Fields.OtpSend.TYPE) type: String): Call<VerifyNumberResponse>

    @POST("/api/v1/trips/create")
    fun initiateRide(
            @Body bodyObject: RideCreateRequestObject): Call<RideCreateResponse>

    @PUT("/api/v1/trips/{trip_id}/partner")
    fun updateBookingDetails(
            @Path("trip_id") jobRequestId: String,
            @Body bodyObject: UpdateBykeaCashBookingRequest): Call<UpdateBykeaCashBookingResponse>


    /**
     * this method can be used to fetch invoice details against the booking id
     *
     * [invoiceUrl] url of the api, will be received from settings
     */
    @GET
    fun getInvoiceDetails(@Url invoiceUrl: String, @Query(RequestParams.TYPE) type: String,
                          @Query(RequestParams.STATE) state: String): Call<FeedbackInvoiceResponse>

    @GET("/api/v1/common/cancel/messages")
    fun getJobComplainReasons(@Query("user_type") userType: String?,
                              @Query("type") type: String?,
                              @Query("lang") lang: String?): Call<ComplainReasonResponse>

    @GET
    fun getBookingDetailsById(@Url bookingUrl: String): Call<BookingDetailResponse>

    @POST("/api/v1/trips/{job_id}/skip")
    fun skipJobRequest(@Path("job_id") jobId: String, @Body bodyObject: SkipJobRequest): Call<SkipJobResponse>

    @PUT("/api/v1/trips/{job_id}/details")
    fun pushTripDetails(@Path("job_id") jobId: String, @Body bodyObject: PushJobDetailsRequest): Call<BaseResponse>

    @PUT("/api/v1/batch/{batch_id}/cancel/partner")
    fun cancelMultiDeliveryBatchJob(@Path("batch_id") batch: String, @Body bodyObject: CancelBatchJobRequest): Call<BaseResponse>

    @POST("/api/v1/partner/temperature")
    fun submitTemperature(@Body bodyObject: TemperatureSubmitRequest): Call<TemperatureSubmitResponse>

    @GET("/v2/batch/{batch_id}/bookings")
    fun getAllDeliveryDetails(
            @Header("x-app-partner-id") driverId: String,
            @Header("x-app-partner-token") token: String,
            @Path("batch_id") batchId: String,
            @Query("limit") limit: Int = DIGIT_TWENTY
    ): Call<DeliveryDetailListResponse>

    @GET("/v2/batch/{batch_id}/bookings/{booking_id}")
    fun getSingleBatchDeliveryDetails(
            @Header("x-app-partner-id") driverId: String,
            @Header("x-app-partner-token") token: String,
            @Path("batch_id") batchId: String,
            @Path("booking_id") bookingId: String
    ): Call<DeliveryDetailSingleTripResponse>

    @POST("/v2/batch/{batch_id}/bookings")
    fun addDeliveryDetail(
            @Header("x-app-partner-id") driverId: String,
            @Header("x-app-partner-token") token: String,
            @Path("batch_id") batchId: String,
            @Body deliveryDetails: DeliveryDetails
    ): Call<DeliveryDetailAddEditResponse>

    @PUT("/v2/batch/{batch_id}/bookings/{booking_id}")
    fun updateDeliveryDetail(
            @Header("x-app-partner-id") driverId: String,
            @Header("x-app-partner-token") token: String,
            @Path("batch_id") batchId: String,
            @Path("booking_id") bookingId: String,
            @Body deliveryDetails: DeliveryDetails
    ): Call<DeliveryDetailAddEditResponse>

    @DELETE("/v2/batch/{batch_id}/bookings/{booking_id}")
    fun removeDeliveryDetail(
            @Header("x-app-partner-id") driverId: String,
            @Header("x-app-partner-token") token: String,
            @Path("batch_id") batchId: String,
            @Path("booking_id") bookingId: String
    ): Call<DeliveryDetailRemoveResponse>

    @PATCH("/v2/batch/{batch_id}/update-return-run")
    fun updateBatchReturnRun(@Header("x-app-partner-id") driverId: String,
                             @Header("x-app-partner-token") token: String,
                             @Path("batch_id") batchId: String,
                             @Body batchUpdateReturnRunRequest: BatchUpdateReturnRunRequest
    ): Call<BatchUpdateReturnRunResponse>

    @FormUrlEncoded
    @POST("/api/v1/driver/topupToPassenger")
    abstract fun topUpPassengerWallet(@Field("_id") _id: String,
                                      @Field("token_id") token_id: String,
                                      @Field("tId") tripNo: String,
                                      @Field("amount") amount: String,
                                      @Field("pId") passId: String
    ): Call<TopUpPassengerWalletResponse>

    companion object {

        var FLAVOR_URL_TELOS: String = ""
        var FLAVOR_URL_LOADBOARD: String = ""

        val talos by lazy {
            invoke(if (FLAVOR_URL_TELOS != "") FLAVOR_URL_TELOS else BuildConfig.FLAVOR_URL_TELOS)
        }

        val loadboard by lazy {
            invoke(if (FLAVOR_URL_LOADBOARD != "") FLAVOR_URL_LOADBOARD else BuildConfig.FLAVOR_URL_LOADBOARD)
        }

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = NetworkUtil.enableTls12OnPreLollipop().apply {
            if (BuildConfig.DEBUG) addNetworkInterceptor(loggingInterceptor)
        }.build()

        operator fun invoke(baseUrl: String): Backend {
            return Retrofit.Builder()
                    .client(NetworkUtil.enableTls12OnPreLollipop().build())
                    .baseUrl(baseUrl)
//                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(Backend::class.java)
        }
    }
}
