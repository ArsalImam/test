package com.bykea.pk.partner.communication.rest;


import com.bykea.pk.partner.models.data.RankingResponse;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.models.data.SignUpAddNumberResponse;
import com.bykea.pk.partner.models.data.SignUpCompleteResponse;
import com.bykea.pk.partner.models.data.SignUpOptionalDataResponse;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.models.data.SignupUplodaImgResponse;
import com.bykea.pk.partner.models.request.DeletePlaceRequest;
import com.bykea.pk.partner.models.request.DriverAvailabilityRequest;
import com.bykea.pk.partner.models.request.DriverLocationRequest;
import com.bykea.pk.partner.models.request.LoadBoardRideCancelRequest;
import com.bykea.pk.partner.models.response.AcceptLoadboardBookingResponse;
import com.bykea.pk.partner.models.response.AddSavedPlaceResponse;
import com.bykea.pk.partner.models.response.BankAccountListResponse;
import com.bykea.pk.partner.models.response.BankDetailsResponse;
import com.bykea.pk.partner.models.response.BiometricApiResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.DeleteSavedPlaceResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.DriverPerformanceResponse;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.GetProfileResponse;
import com.bykea.pk.partner.models.response.GetSavedPlacesResponse;
import com.bykea.pk.partner.models.response.GetZonesResponse;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.models.response.LoadBoardListingResponse;
import com.bykea.pk.partner.models.response.LoadBoardResponse;
import com.bykea.pk.partner.models.response.LoadboardBookingDetailResponse;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.PlaceAutoCompleteResponse;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.SettingsResponse;
import com.bykea.pk.partner.models.response.ShahkarResponse;
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateAppVersionResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UpdateRegIDResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadImageFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.models.response.ZoneAreaResponse;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Fields;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


interface IRestClient {


    @FormUrlEncoded
    @POST(ApiTags.USER_LOGIN_API)
    Call<LoginResponse> login(@Field(Fields.Login.PHONE_NUMBER) String number,
                              @Field(Fields.Login.OTP_CODE) String otpCode,
                              @Field(Fields.Login.DEVICE_TYPE) String deviceType,
                              @Field(Fields.Login.REG_ID) String gcmId,
                              @Field(Fields.Login.LAT) String lat,
                              @Field(Fields.Login.LNG) String lng,
                              @Field(Fields.Login.APP_VERSION) String version,
                              @Field(Fields.Login.ONE_SIGNAL_PLAYER_ID) String one_signal_p_id,
                              @Field(Fields.Login.ADID) String advertising_id,
                              @Field(Fields.Login.IMEI_NUMBER) String imei);


    @FormUrlEncoded
    @POST(ApiTags.DRIVER_OTP_SEND)
    Call<VerifyNumberResponse> sendDriverOTP(@Field(Fields.Login.PHONE_NUMBER) String phone,
                                             @Field("type") String type,
                                             @Field(Fields.Login.DEVICE_TYPE) String device,
                                             @Field(Fields.Login.LAT) double lat,
                                             @Field(Fields.Login.LNG) double lng,
                                             @Field(Fields.Login.APP_VERSION) String version);

    @FormUrlEncoded
    @POST(ApiTags.LOGOUT_API)
    Call<LogoutResponse> logout(@Field(Fields.Logout.id) String Id,
                                @Field(Fields.Logout.tokenId) String tokenId,
                                @Field("driver_id") String driverId);

    @GET(ApiTags.CHECK_RUNNING_TRIP)
    Call<CheckDriverStatusResponse> checkRunningTrip(@Query(Fields.Logout.id) String Id,
                                                     @Query(Fields.Logout.tokenId) String tokenId);

    @GET(ApiTags.GET_HISTORY_LIST)
    Call<TripHistoryResponse> getTripHistory(@Query(Fields.id) String Id,
                                             @Query(Fields.tokenId) String tokenId,
                                             @Query(Fields.USER_TYPE) String userType,
                                             @Query("page") String pageNo);

    @GET(ApiTags.GET_MISSED_TRIPS_HISTORY_LIST)
    Call<TripMissedHistoryResponse> getMissedTripHistory(@Query(Fields.id) String Id,
                                                         @Query(Fields.tokenId) String tokenId,
                                                         @Query(Fields.USER_TYPE) String userType,
                                                         @Query("page") String pageNo);

    @GET(ApiTags.GET_SETTINGS)
    Call<SettingsResponse> getSettings(@Query(Fields.USER_TYPE) String userTyp,
                                       @Query("ctId") String city,
                                       @Query("s_ver") String settingsVersion);

    @GET(ApiTags.GET_SETTINGS)
    Call<SettingsResponse> getSettings(@Query(Fields.USER_TYPE) String userTyp);

    @GET(ApiTags.SIGN_UP_SETTINGS)
    Call<SignUpSettingsResponse> requestSignUpSettings(@Header("key") String key);

    @FormUrlEncoded
    @POST(ApiTags.SIGN_UP_ADD_NUMBER)
    Call<SignUpAddNumberResponse> requestRegisterNumber(@Header("key") String key,
                                                        @Field("phone") String phone,
                                                        @Field("imei") String imei,
                                                        @Field("mobile_brand") String mobile_brand,
                                                        @Field("mobile_model") String mobile_model,
                                                        @Field("geoloc") String geoloc,
                                                        @Field("cnic") String cnic,
                                                        @Field("city") String city);

    //    @POST(ApiTags.SIGN_UP_ADD_NUMBER)
//    Call<SignUpAddNumberResponse> requestRegisterNumber(@Header("key") String key,
//                                                        @Body SignupAddRequest body);
    @FormUrlEncoded
    @POST(ApiTags.SIGN_UP_COMPLETE)
    Call<SignUpOptionalDataResponse> postOptionalSignupData(@Header("key") String key,
                                                            @Field("_id") String id,
                                                            @Field("email") String email,
                                                            @Field("ref_number") String ref_number);

    @FormUrlEncoded
    @POST(ApiTags.SIGN_UP_BIOMETRIC_VERIFICATION)
    Call<BiometricApiResponse> postBiometricVerification(@Header("key") String key,
                                                         @Field("_id") String id,
                                                         @Field("verification_status") boolean verification_status);

    @FormUrlEncoded
    @POST(ApiTags.SIGN_UP_OPTIONAL_DATA)
    Call<SignUpOptionalDataResponse> postOptionalSignupDataJustRefNo(@Header("key") String key,
                                                                     @Field("_id") String id,
                                                                     @Field("ref_number") String ref_number);

    @FormUrlEncoded
    @POST(ApiTags.SIGN_UP_OPTIONAL_DATA)
    Call<SignUpOptionalDataResponse> postOptionalSignupDataJustEmail(@Header("key") String key,
                                                                     @Field("_id") String id,
                                                                     @Field("email") String email);

    @FormUrlEncoded
    @POST(ApiTags.SIGN_UP_COMPLETE)
    Call<SignUpCompleteResponse> requestCompleteSignupData(@Header("key") String key,
                                                           @Field("_id") String id);


    @Multipart
    @POST(ApiTags.SIGN_UP_UPLOAD_DOCUMENT)
    Call<SignupUplodaImgResponse> uplodaDocumentImage(@Header("key") String key,
                                                      @Part("_id") RequestBody description,
                                                      @Part("image_type") RequestBody image_type,
                                                      @Part("image\"; filename=\"BykeaDocument" + Constants.UPLOAD_IMG_EXT + "\" ")
                                                              RequestBody file);


    @FormUrlEncoded
    @POST(ApiTags.REGISTER_USER_API)
    Call<RegisterResponse> register(@Field(Fields.Register.PHONE_NUMBER) String number,
                                    @Field(Fields.Register.PIN_CODE) String pincode,
                                    @Field(Fields.Register.DEVICE_TYPE) String deviceType,
                                    @Field(Fields.Register.REG_ID) String regId,
                                    @Field(Fields.Register.FULL_NAME) String fullName,
                                    @Field(Fields.Register.CITY) String city,
                                    @Field(Fields.Register.ADDRESS) String address,
                                    @Field(Fields.Register.AGREE_CHECK) String agreeCheck,
                                    @Field(Fields.Register.PLATE_NO) String plateNo,
                                    @Field(Fields.Register.LICENSE_NO) String licenseNo,
                                    @Field(Fields.Register.EXPIRY_DATE) String licenseExpire,
                                    @Field(Fields.Register.VEHICLE_TYPE) String vehicleType,
                                    @Field(Fields.Register.CNIC) String cnic,
                                    @Field(Fields.Register.EMAIL) String email,
                                    @Field(Fields.Register.LICENSE_NO_IMAGE) String licenseImage,
                                    @Field(Fields.Register.REGISTER_LAT) String lat,
                                    @Field(Fields.Register.REGISTER_LNG) String lng,
                                    @Field(Fields.Register.PILOT_IMAGE) String pilotImage);

    @FormUrlEncoded
    @POST(ApiTags.UPDATE_PROFILE_API)
    Call<UpdateProfileResponse> updateProfile(@Field("full_name") String name,
                                              @Field("city") String city,
                                              @Field("address") String address,
                                              @Field("email") String email,
                                              @Field("_id") String driverId,
                                              @Field("token_id") String id,
                                              @Field("user_type") String userType,
                                              @Field("pin_code") String pinCode);

    @FormUrlEncoded
    @POST(ApiTags.PHONE_NUMBER_VERIFICATION_API)
    Call<VerifyNumberResponse> phoneNumberVerification(@Field(Fields.NumberVerification.PHONE_NUMBER) String phone,
                                                       @Field(Fields.OtpVerification.USER_TYPE) String userType);

    @FormUrlEncoded
    @POST(ApiTags.CODE_VERIFICATION_API)
    Call<VerifyCodeResponse> codeVerification(@Field(Fields.OtpVerification.PHONE_NUMBER) String phone,
                                              @Field(Fields.OtpVerification.PHONE_CODE) String otpCode,
                                              @Field(Fields.OtpVerification.USER_TYPE)
                                                      String userType);

    @FormUrlEncoded
    @POST(ApiTags.FORGOT_PASSWORD_API)
    Call<ForgotPasswordResponse> forgotPassword(@Field("phone") String phoneNumber);


    @Multipart
    @POST(ApiTags.UPLOAD_AUDIO_FILE_API)
    Call<UploadAudioFile> uploadAudioFile(@Part("file\"; filename=\"audio.wav\" ")
                                                  RequestBody file);

    @Multipart
    @POST(ApiTags.UPLOAD_AUDIO_FILE_API)
    Call<UploadImageFile> uploadImageFile(@Part("file\"; filename=\"image.webp\" ")
                                                  RequestBody file);


    @GET(ApiTags.GET_SERVICE_TYPE_API)
    Call<ServiceTypeResponse> getServiceTypes();

    @GET(ApiTags.GET_WALLET_LIST)
    Call<WalletHistoryResponse> getWalletHistory(@Query("_id") String Id,
                                                 @Query("token_id") String tokenId,
                                                 @Query("user_type") String userType,
                                                 @Query("page") String pageNo);

    @GET(ApiTags.GET_CONTACTS_NUMBERS)
    Call<ContactNumbersResponse> getContactNumbers(@Query("_id") String Id,
                                                   @Query("token_id") String tokenId,
                                                   @Query("user_type") String userType);

    @GET(ApiTags.GET_BANK_ACCOUNT_LIST)
    Call<BankAccountListResponse> getBankAccounts(@Query("_id") String Id,
                                                  @Query("token_id") String tokenId,
                                                  @Query("lat") String lat,
                                                  @Query("lng") String lng);

    @PUT(ApiTags.DRIVER_STATUS_ONLINE_OFFLINE)
    Call<PilotStatusResponse> updateDriverStatus(@Body DriverAvailabilityRequest availabilityRequest);

    @GET(ApiTags.GET_BANK_ACCOUNT_DETAILS)
    Call<BankDetailsResponse> getBankAccountDetails(@Query("_id") String Id,
                                                    @Query("token_id") String tokenId,
                                                    @Query("lat") String lat,
                                                    @Query("lng") String lng,
                                                    @Query("bId") String bankId);

    @GET(ApiTags.PLACES_GEOCODER_EXT_URL)
    Call<GeocoderApi> callGeoCoderApi(@Query("latlng") String latLng,
                                      @Query("key") String key);

    @FormUrlEncoded
    @POST(ApiTags.CHANGE_PIN)
    Call<ChangePinResponse> requestChangePin(@Field("_id") String _id,
                                             @Field("token_id") String token_id,
                                             @Field("newPinCode") String newPinCode,
                                             @Field("oldPinCode") String oldPinCode,
                                             @Field("user_type") String userType);

    @FormUrlEncoded
    @POST(ApiTags.SET_DRIVER_DROP_OFF)
    Call<DriverDestResponse> setDriverDroppOff(@Field("_id") String _id,
                                               @Field("token_id") String token_id,
                                               @Field("eLat") String lat,
                                               @Field("eLng") String lng,
                                               @Field("eAdd") String address);

    @FormUrlEncoded
    @POST(ApiTags.UPDATE_REG_ID)
    Call<UpdateRegIDResponse> updateRegid(@Field(Fields.PushNotificationFCM.ID) String id,
                                          @Field(Fields.PushNotificationFCM._ID) String _id,
                                          @Field(Fields.PushNotificationFCM.TOKEN_ID) String tokenId,
                                          @Field(Fields.PushNotificationFCM.REG_ID) String reg_id,
                                          @Field(Fields.PushNotificationFCM.DEVICE_TYPE) String device_type,
                                          @Field(Fields.PushNotificationFCM.TYPE) String type);

    @GET(ApiTags.GET_PROFILE_API)
    Call<GetProfileResponse> requestProfileData(@Query("_id") String _id,
                                                @Query("token_id") String token_id,
                                                @Query("user_type") String userType);

    @GET(ApiTags.GET_CITIES)
    Call<GetCitiesResponse> getCities();

    @GET
    Call<ArrayList<HeatMapUpdatedResponse>> getHeatMap(@Header("x-key") String key,
                                                       @Url String url);


    @GET(ApiTags.PLACES_DISTANCEMATRIX_EXT_URL)
    Call<GoogleDistanceMatrixApi> callDistanceMatrixApi(@Query(Fields.GoogleDirectionApi.ORIGIN) String origin,
                                                        @Query(Fields.GoogleDirectionApi.DESTINATION) String destination,
                                                        @Query(Fields.GoogleDirectionApi.KEY) String key);


    @FormUrlEncoded
    @POST(ApiTags.POST_PROBLEM)
    Call<ProblemPostResponse> postProblem(@Field("_id") String _id,
                                          @Field("token_id") String token_id,
                                          @Field("reason") String reason,
                                          @Field("trip_id") String tripID,
                                          @Field("email") String email,
                                          @Field("cType") String contactType,
                                          @Field("name") String name,
                                          @Field("ph") String phone,
                                          @Field("details") String details,
                                          @Field("con") boolean isFromReport,
                                          @Field("user_type") String type);


    @GET
    Call<ResponseBody> downloadAudioFile(@Url String fileUrl);

    @GET(ApiTags.EXTENDED_URL_GOOGLE_PLACE_DETAILS_API)
    Call<PlaceDetailsResponse> getPlaceDetails(@Query("placeid") String placeid,
                                               @Query("key") String googleMapApiKey);

    @GET(ApiTags.EXTENDED_URL_GOOGLE_PLACE_AUTOCOMPLETE_API)
    Call<PlaceAutoCompleteResponse> getAutoCompletePlaces(@Query("input") String input,
                                                          @Query("location") String location,
                                                          @Query("components") String component,
                                                          @Query("radius") String radius,
                                                          @Query("key") String googleMapApiKey);


    @POST(ApiTags.ADD_SAVED_PLACE)
    Call<AddSavedPlaceResponse> addSavedPlace(@Body SavedPlaces body);

    @POST(ApiTags.UPDATE_SAVED_PLACE)
    Call<AddSavedPlaceResponse> updateSavedPlace(@Body SavedPlaces body);

    @POST(ApiTags.DELETE_SAVED_PLACE)
    Call<DeleteSavedPlaceResponse> deleteSavedPlace(@Body DeletePlaceRequest body);


    @GET(ApiTags.GET_SAVED_PLACES)
    Call<GetSavedPlacesResponse> getSavedPlaces(@Query("_id") String userId,
                                                @Query("token_id") String tripId);


    @GET(ApiTags.GET_AREAS)
    Call<GetZonesResponse> requestZones(@Query("lat") String lat, @Query("lng") String lng);

    @GET(ApiTags.GET_ADDRESSES)
    Call<ZoneAreaResponse> requestZoneAreas(@Query("zid") String cityId);


    @FormUrlEncoded
    @POST(ApiTags.TOP_UP_PASSENGER_WALLET)
    Call<TopUpPassWalletResponse> topUpPassengerWallet(@Field("_id") String _id,
                                                       @Field("token_id") String token_id,
                                                       @Field("tId") String tripNo,
                                                       @Field("amount") String amount,
                                                       @Field("pId") String passId);

    @GET(ApiTags.GET_SHAHKAR)
    Call<ShahkarResponse> requestShahkar(@Query("_id") String id, @Query("token_id") String accessToken);

    @GET(ApiTags.GET_BONUS_CHART)
    Call<RankingResponse> requestBonusStats(@Query("_id") String id, @Query("token_id") String accessToken,
                                            @Query("city") String city_id);

    @GET(ApiTags.GET_DRIVER_PERFORMANCE)
    Call<DriverPerformanceResponse> requestDriverPerformance(@Query("_id") String id, @Query("token_id") String accessToken,
                                                             @Query("date") int dateCode); // 0 for current week data || -1 for previus week

    @GET(ApiTags.GET_LOAD_BOARD)
    Call<LoadBoardResponse> requestLoadBoard(@Query("_id") String id, @Query("token_id") String accessToken,
                                             @Query("lat") String lat, @Query("lng") String lng);


    @PUT(ApiTags.SET_DRIVER_LOCATION)
    Call<LocationResponse> updateDriverLocation(@Body DriverLocationRequest driverLocation);

    @FormUrlEncoded
    @PUT(ApiTags.UPDATE_APP_VERSION)
    Call<UpdateAppVersionResponse> updateAppVersion(@Field("_id") String id,
                                                    @Field("token_id") String token,
                                                    @Field("version") double version);
    /**
     * Getting loadboard list in home screen when partener is active.
     * @param driver_id Driver id
     * @param token_id Driver access token
     * @param lat Driver current lat
     * @param lng Driver current lng
     * @param limit jobs limit - OPTIONAL
     * @param pickup_zone driver's selected pickup zone - OPTIONAL
     * @param dropoff_zone driver's selected dropoff zone - OPTIONAL
     * @return Loadboard jobs list
     */
    @GET(ApiTags.GET_LOAD_BOARD_LISTING)
    Call<LoadBoardListingResponse> requestLoadBoardListing(@Query("_id") String driver_id,
                                                           @Query("token_id") String token_id,
                                                           @Query("lat") String lat,
                                                           @Query("lng") String lng,
                                                           @Query("limit") String limit,
                                                           @Query("pickup_zone") String pickup_zone /*id*/,
                                                           @Query("dropoff_zone") String dropoff_zone /*id*/);

    /**
     * Accept a booking
     * @param bookingId selected booking Id
     * @param driver_id driver's id
     * @param token_id driver's access token
     * @param lat driver's current lat
     * @param lng driver's current lng
     * @return Booking accept response
     */
    @FormUrlEncoded
    @POST(ApiTags.ACCEPT_LOAD_BOARD_BOOKING)
    Call<AcceptLoadboardBookingResponse> acceptLoadboardBooking(@Path("id") String bookingId,
                                                                @Field("_id") String driver_id,
                                                                @Field("token_id") String token_id,
                                                                @Field("lat") String lat,
                                                                @Field("lng") String lng);

    /**
     * Getting selected booking's detail
     * @param bookingId selected booking id
     * @param driver_id driver's id
     * @param lat driver's current lat
     * @param lng driver's current lng
     * @param token_id driver's access token
     * @return booking detail response
     */
    @GET(ApiTags.GET_LOAD_BOARD_BOOKING_DETAIL)
    Call<LoadboardBookingDetailResponse> requestLoadBoardBookingDetail(@Path("id") String bookingId,
                                                                       @Query("_id") String driver_id,
                                                                       @Query("lat") String lat,
                                                                       @Query("lng") String lng,
                                                                       @Query("token_id") String token_id);


    /**
     * Cancel selected booking
     *
     * @return booking detail response
     */
    @GET(ApiTags.CANCEL_LOAD_BOARD_BOOKING)
    Call<CancelRideResponse> cancelLoadBoardBooking(@Body LoadBoardRideCancelRequest body);

//    @GET("/news")
//    Call<GenericRetrofitCallBackSuccess<News>> requestHttp(
//            @QueryMap Map<String, String> params);


  /*  @FormUrlEncoded
    @Multipart
    @POST(Tags.UPLOAD_DRIVER_DOCUMENTS_API)
    Call<UploadDocumentFile> uploadDocumentFile(@Part("file\"; filename=\"file.jpg\" ")
                                                        RequestBody file);

    @Multipart
    @POST(Tags.UPLOAD_AUDIO_FILE_API)
    Call<UploadAudioFile> uploadAudioFile(@Part("file\"; filename=\"audio.wav\" ")
                                                  RequestBody file);


    @GET(Tags.IS_USER_REGISTER_API)
    Call<IsUserRegistered> isUserRegistered(@Query("email") String email,
                                            @Query("user_name") String username,
                                            @Query("sponser_id") String sponsorId);

    @FormUrlEncoded
    @POST(Tags.REGISTER_USER_API)
    Call<RegisterResponse> registerUser(@Field("user") String userData);

    @FormUrlEncoded
    @POST(Tags.CONTACT_US_API)
    Call<ContactUsResponse> contactUs(@Field("data") String contactData);


    @FormUrlEncoded
    @POST(Tags.UPDATE_REG_ID_API)
    Call<UpdateRegIdResponse> updateRegId(@Field(Fields.UpdateRegID.ID) String id,
                                          @Field(Fields.UpdateRegID.REG_ID) String regId,
                                          @Field(Fields.UpdateRegID.DEVICE_TYPE) String deviceType,
                                          @Field(Fields.UpdateRegID.NOTIFICATION_STATUS) String notStatus,
                                          @Field(Fields.UpdateRegID.TOKEN_ID) String tokenId);
*/
}
