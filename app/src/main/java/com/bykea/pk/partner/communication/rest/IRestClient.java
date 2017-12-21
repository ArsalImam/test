package com.bykea.pk.partner.communication.rest;


import com.bykea.pk.partner.models.response.AccountNumbersResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.GetProfileResponse;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.SettingsResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.Fields;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.http.Url;


interface IRestClient {

    @FormUrlEncoded
    @POST(ApiTags.USER_LOGIN_API)
    Call<LoginResponse> login(@Field(Fields.Login.PHONE_NUMBER) String number,
                              @Field(Fields.Login.PIN_CODE) String pincode,
                              @Field(Fields.Login.DEVICE_TYPE) String deviceType,
                              @Field(Fields.Login.USER_STATUS) String userStatus,
                              @Field(Fields.Login.REG_ID) String gcmId,
                              @Field(Fields.Login.LAT) String lat,
                              @Field(Fields.Login.LNG) String lng,
                              @Field(Fields.Login.APP_VERSION) String version,
                              @Field(Fields.Login.ONE_SIGNAL_PLAYER_ID) String one_signal_p_id,
                              @Field(Fields.Login.ADID) String advertising_id,
                              @Field(Fields.Login.IMEI_NUMBER) String imei);

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
                                       @Query("city") String city,
                                       @Query("s_ver") String settingsVersion);


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
    @POST(ApiTags.UPLOAD_DRIVER_DOCUMENTS_API)
    Call<UploadDocumentFile> uploadDocumentFile(@Part("file\"; filename=\"file.jpg\" ")
                                                        RequestBody file);

    @Multipart
    @POST(ApiTags.UPLOAD_AUDIO_FILE_API)
    Call<UploadAudioFile> uploadAudioFile(@Part("file\"; filename=\"audio.wav\" ")
                                                  RequestBody file);


    @GET(ApiTags.GET_SERVICE_TYPE_API)
    Call<ServiceTypeResponse> getServiceTypes();

    @FormUrlEncoded
    @POST(ApiTags.UPDATE_STATUS)
    Call<PilotStatusResponse> updateStatus(@Field("driver_id") String driverId,
                                           @Field("is_available") String status);

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
    Call<AccountNumbersResponse> getAccountNumbers(@Query("_id") String Id,
                                                   @Query("token_id") String tokenId,
                                                   @Query("user_type") String userType,
                                                   @Query("page") String pageNo);

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
                                          @Field("name") String name,
                                          @Field("ph") String phone,
                                          @Field("details") String details,
                                          @Field("con") boolean isFromReport,
                                          @Field("user_type") String type);


    @GET
    Call<ResponseBody> downloadAudioFile(@Url String fileUrl);

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
