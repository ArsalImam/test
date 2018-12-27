package com.bykea.pk.partner.communication.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.models.data.RankingResponse;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.models.data.SignUpAddNumberResponse;
import com.bykea.pk.partner.models.data.SignUpCompleteResponse;
import com.bykea.pk.partner.models.data.SignUpOptionalDataResponse;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.models.data.SignupUplodaImgResponse;
import com.bykea.pk.partner.models.data.ZoneData;
import com.bykea.pk.partner.models.request.DeletePlaceRequest;
import com.bykea.pk.partner.models.request.DriverAvailabilityRequest;
import com.bykea.pk.partner.models.request.DriverLocationRequest;
import com.bykea.pk.partner.models.response.AddSavedPlaceResponse;
import com.bykea.pk.partner.models.response.BankAccountListResponse;
import com.bykea.pk.partner.models.response.BankDetailsResponse;
import com.bykea.pk.partner.models.response.BiometricApiResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.DeleteSavedPlaceResponse;
import com.bykea.pk.partner.models.response.DownloadAudioFileResponse;
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
import com.bykea.pk.partner.models.response.LoadBoardResponse;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.PlaceAutoCompleteResponse;
import com.bykea.pk.partner.models.response.PlaceDetailsResponse;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.SettingsResponse;
import com.bykea.pk.partner.models.response.ShahkarResponse;
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UpdateRegIDResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadImageFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.models.response.ZoneAreaResponse;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.okhttp.ResponseBody;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class RestRequestHandler {

    private Context mContext;
    private IRestClient mRestClient;
    private IResponseCallback mResponseCallBack;

    private static String TAG = RestRequestHandler.class.getSimpleName();


    /***
     * Send Driver login request to API Server
     * @param context Calling context.
     * @param callback API response callback.
     * @param phoneNumber Driver phone number
     * @param deviceType Device type i.e. (Android/iOS)
     * @param latitude Current Driver latitude.
     * @param longitude Current driver longitude.
     * @param OtpType Method type for OTP call i.e (SMS/PHONE)
     */
    public void sendDriverLogin(Context context, final IResponseCallback callback,
                                String phoneNumber,
                                String deviceType,
                                double latitude,
                                double longitude,
                                String OtpType) {

        mContext = context;
        this.mResponseCallBack = callback;
        mRestClient = RestClient.getClient(mContext);
        Call<VerifyNumberResponse> numberResponseCall = mRestClient.sendDriverOTP(
                phoneNumber, OtpType, deviceType, latitude, longitude, Utils.getVersion(context));

        numberResponseCall.enqueue(new Callback<VerifyNumberResponse>() {
            @Override
            public void onResponse(Response<VerifyNumberResponse> response, Retrofit retrofit) {
                if (response == null || response.body() == null) {
                    if (response != null && response.errorBody() != null) {
                        VerifyNumberResponse verifyNumberResponse =
                                Utils.parseAPIErrorResponse(response,
                                        retrofit, VerifyNumberResponse.class);
                        if (verifyNumberResponse != null) {
                            mResponseCallBack.onResponse(verifyNumberResponse);
                        } else {
                            mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                    mContext.getString(R.string.error_try_again) + " ");
                        }
                    } else {
                        mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                mContext.getString(R.string.error_try_again) + " ");
                    }
                } else {
                    if (response.isSuccess()) {
                        if (null != mResponseCallBack) {
                            mResponseCallBack.onResponse(response.body());
                        }
                    } else {
                        mResponseCallBack.onError(response.body().getCode(),
                                response.body().getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    /***
     * Send driver location update request to API server.
     * @param context Calling context.
     * @param onResponseCallBack  Response Handler Callback
     * @param locationRequest driver location update request model
     */
    public void sendDriverLocationUpdate(Context context,
                                         final IResponseCallback onResponseCallBack,
                                         DriverLocationRequest locationRequest) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<LocationResponse> restCall = mRestClient.updateDriverLocation(locationRequest);
        restCall.enqueue(new GenericLocationRetrofitCallBack(onResponseCallBack));
    }

    /***
     * Send User login request to API server.
     * @param context Calling Context.
     * @param onResponseCallBack Response Handler Callback
     * @param driverNumber Driver number
     * @param otpCode OTP code which he received.
     * @param deviceType Device Type i.e. (Android/iOS)
     * @param regID FCM user ID
     */
    public void sendUserLogin(Context context,
                              final IResponseCallback onResponseCallBack,
                              String driverNumber,
                              String otpCode,
                              String deviceType,
                              String regID) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Utils.redLog("IMEI NUMBER", Utils.getDeviceId(context));
        Call<LoginResponse> restCall = mRestClient.login(
                driverNumber,
                otpCode,
                deviceType,
                regID,
                "" + AppPreferences.getLatitude(),
                "" + AppPreferences.getLongitude(),
                Utils.getVersion(context),
                AppPreferences.getOneSignalPlayerId(),
                AppPreferences.getADID(),
                Utils.getDeviceId(context)
        );

        restCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Response<LoginResponse> response, Retrofit retrofit) {
                if (response == null || response.body() == null) {
                    if (response != null && response.errorBody() != null) {
                        LoginResponse loginResponse =
                                Utils.parseAPIErrorResponse(response, retrofit, LoginResponse.class);
                        if (loginResponse != null) {
                            mResponseCallBack.onResponse(loginResponse);
                        } else {
                            mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                    mContext.getString(R.string.error_try_again) + " ");
                        }
                    } else {
                        mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                mContext.getString(R.string.error_try_again) + " ");
                    }
                } else {
                    if (response.isSuccess()) {
                        if (null != mResponseCallBack) {
                            mResponseCallBack.onResponse(response.body());
                        }
                    } else {
                        mResponseCallBack.onError(response.body().getCode(),
                                response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    public void sendLogout(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<LogoutResponse> restCall = mRestClient.logout(AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(), AppPreferences.getDriverId());
        restCall.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Response<LogoutResponse> response, Retrofit retrofit) {
                // Got success from server
                if (response != null && response.body() != null) {
                    if (response.body().isSuccess()) {
                        if (null != mResponseCallBack) {
                            mResponseCallBack.onResponse(response.body());
                        }
                    } else {
                        //this is case for error
                        mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                    }
                } else {
                    if (null != mResponseCallBack) {
                        mResponseCallBack.onResponse(new LogoutResponse());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    public void updateProfile(Context context, final IResponseCallback onResponseCallBack,
                              String fullName, String city, String address, String email, String pincode) {

        this.mResponseCallBack = onResponseCallBack;
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<UpdateProfileResponse> restCall = mRestClient.updateProfile(fullName,
                city, address, email, AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(), Constants.USER_TYPE, pincode);
        restCall.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Response<UpdateProfileResponse> response, Retrofit retrofit) {
                // Got success from server
                if (response.body().isSuccess()) {
                    if (null != mResponseCallBack) {
                        mResponseCallBack.onResponse(response.body());
                    }

                } else {
                    //this is case for error
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    // This method will get you a verification code
    public void sendPhoneNumberVerificationRequest(Context context, final IResponseCallback onResponseCallBack,
                                                   String phoneNumber, int userStatus) {
        this.mResponseCallBack = onResponseCallBack;
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<VerifyNumberResponse> restCall = mRestClient.phoneNumberVerification(phoneNumber, Constants.USER_TYPE);
        restCall.enqueue(new Callback<VerifyNumberResponse>() {
            @Override
            public void onResponse(Response<VerifyNumberResponse> response, Retrofit retrofit) {
                Gson gson = new Gson();

                // Got success from server
                if (response.body().isSuccess()) {
                    mResponseCallBack.onResponse(response.body());

                } else { //this is case for error
                    mResponseCallBack.onError(response.body().getCode(),
                            response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }


    // This method will authenticate the code
    public void sendCodeVerificationRequest(Context context, final IResponseCallback onResponseCallBack,
                                            String code, String phoneNumber) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<VerifyCodeResponse> restCall = mRestClient.codeVerification(phoneNumber, code,
                Constants.USER_TYPE);
        restCall.enqueue(new Callback<VerifyCodeResponse>() {
            @Override
            public void onResponse(Response<VerifyCodeResponse> response, Retrofit retrofit) {
                Gson gson = new Gson();

                // Got success from server
                if (response.body().isSuccess()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(),
                            response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });
    }

    //THIS METHOD IS TO CALL FORGOT PASSWORD API
    public void forgotPassword(Context context, final IResponseCallback onResponseCallback, String phone) {
        mContext = context;
        this.mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);
        Call<ForgotPasswordResponse> requestCall = mRestClient.forgotPassword(phone);
        requestCall.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Response<ForgotPasswordResponse> response, Retrofit retrofit) {
                if (response.body().getCode() == HTTPStatus.OK ||
                        response.body().getCode() == HTTPStatus.CREATED) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(),
                            response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));

            }
        });

    }

    public void getTripHistory(Context context, final IResponseCallback onResponseCallBack, String pageNo) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<TripHistoryResponse> restCall = mRestClient.getTripHistory(AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(), Constants.USER_TYPE, pageNo);
        restCall.enqueue(new Callback<TripHistoryResponse>() {
            @Override
            public void onResponse(Response<TripHistoryResponse> response, Retrofit retrofit) {
                // Got success from server
                if (null != response.body()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));

            }
        });


    }

    public void getMissedTripHistory(Context context, final IResponseCallback onResponseCallBack, String pageNo) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<TripMissedHistoryResponse> restCall = mRestClient.getMissedTripHistory(AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(), Constants.USER_TYPE, pageNo);
        restCall.enqueue(new Callback<TripMissedHistoryResponse>() {
            @Override
            public void onResponse(Response<TripMissedHistoryResponse> response, Retrofit retrofit) {
                // Got success from server
                if (null != response.body()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    public void checkRunningTrip(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<CheckDriverStatusResponse> restCall = mRestClient.checkRunningTrip(AppPreferences.getDriverId(),
                AppPreferences.getAccessToken());
        restCall.enqueue(new Callback<CheckDriverStatusResponse>() {
            @Override
            public void onResponse(Response<CheckDriverStatusResponse> response, Retrofit retrofit) {
                // Got success from server
                if (null != response.body()) {
                    mResponseCallBack.onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));

            }
        });


    }

    //THIS METHOD IS TO UPLOAD AUDIO MESSAGE FILE
    public void uploadAudioFile(Context context, IResponseCallback responseCallBack, final File file) {
        mContext = context;
        mResponseCallBack = responseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<UploadAudioFile> requestCall = mRestClient.uploadAudioFile(Utils.convertFileToRequestBody(file));
        requestCall.enqueue(new Callback<UploadAudioFile>() {
            @Override
            public void onResponse(Response<UploadAudioFile> response, Retrofit retrofit) {
                Utils.deleteFile(file);
                if (null == response.body()) {
                    mResponseCallBack.onError(0, mContext.getString(R.string.error_try_again));
                } else if (response.body().getCode() == HTTPStatus.OK ||
                        response.body().getCode() == HTTPStatus.CREATED) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(),
                            response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.deleteFile(file);
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });

    }

    //THIS METHOD IS TO UPLOAD IMAGE FILE
    public void uploadImageFile(Context context, IResponseCallback responseCallBack, final File file) {
        mContext = context;
        mResponseCallBack = responseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<UploadImageFile> requestCall = mRestClient.uploadImageFile(Utils.convertFileToRequestBody(file));
        requestCall.enqueue(new Callback<UploadImageFile>() {
            @Override
            public void onResponse(Response<UploadImageFile> response, Retrofit retrofit) {
                Utils.deleteFile(file);
                if (null == response.body()) {
                    mResponseCallBack.onError(0, mContext.getString(R.string.error_try_again));
                } else if (response.body().getCode() == HTTPStatus.OK ||
                        response.body().getCode() == HTTPStatus.CREATED) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(),
                            response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.deleteFile(file);
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });

    }

    //THIS METHOD IS UPLOAD DRIVER DOCUMENT FILE
//    public void uplaodDriverDocument(Context context, final IResponseCallback onResponseCallback, File file) {
//        mContext = context;
//        mResponseCallBack = onResponseCallback;
//        mRestClient = RestClient.getClient(mContext);
//        Call<UploadDocumentFile> requestCall = mRestClient.uploadDocumentFile(Utils.convertStringToRequestBody(),
//                Utils.convertStringToRequestBody(), Utils.convertFileToRequestBody(file));
//        requestCall.enqueue(new Callback<UploadDocumentFile>() {
//            @Override
//            public void onResponse(Response<UploadDocumentFile> response, Retrofit retrofit) {
//                if (null == response.body()) {
//                    mResponseCallBack.onError(0, mContext.getString(R.string.error_try_again));
//                } else if (response.body().getCode() == HTTPStatus.OK ||
//                        response.body().getCode() == HTTPStatus.CREATED) {
//                    mResponseCallBack.onResponse(response.body());
//                } else {
//                    mResponseCallBack.onError(response.body().getCode(),
//                            response.body().getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                mResponseCallBack.onError(0, getErrorMessage(t));
//            }
//        });
//    }

    //THIS METHOD IS TO GET SERVICE TYPES
    public void getServiceTypes(Context context, final IResponseCallback onResponseCallback) {
        mContext = context;
        mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);
        Call<ServiceTypeResponse> requestCall = mRestClient.getServiceTypes();
        requestCall.enqueue(new Callback<ServiceTypeResponse>() {
            @Override
            public void onResponse(Response<ServiceTypeResponse> response, Retrofit retrofit) {
                if (null == response.body()) {
                    mResponseCallBack.onError(0, mContext.getString(R.string.error_try_again));
                } else if (response.body().getCode() == HTTPStatus.OK ||
                        response.body().getCode() == HTTPStatus.CREATED) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(),
                            response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void reverseGeoding(Context context, final IResponseCallback onResponseCallback,
                               String latLng, String key) {
        mContext = context;
        mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getGooglePlaceApiClient();
        Call<GeocoderApi> requestCall = mRestClient.callGeoCoderApi(latLng, key);
        requestCall.enqueue(new Callback<GeocoderApi>() {
            @Override
            public void onResponse(Response<GeocoderApi> response, Retrofit retrofit) {
                if (response.body().getStatus().equalsIgnoreCase("ok")) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(0, "Address not found.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });
    }

    public void requestSignUpSettings(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getBykeaSignUpApiClient();
        Call<SignUpSettingsResponse> requestCall = mRestClient.requestSignUpSettings(ApiTags.BASE_SERVER_URL_SIGN_UP_X_API);
        requestCall.enqueue(new Callback<SignUpSettingsResponse>() {
            @Override
            public void onResponse(Response<SignUpSettingsResponse> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, mContext.getString(R.string.error_try_again));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void requestRegisterNumber(Context context, String phone, String city, String cnic, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getBykeaSignUpApiClient();
//        ArrayList<Double> loc = new ArrayList<>();
//        loc.add(AppPreferences.getLatitude());
//        loc.add(AppPreferences.getLongitude());
//        SignupAddRequest request = new SignupAddRequest();
//        request.setCity(city);
//        request.setGeoloc(loc);
//        request.setImei(Utils.getDeviceId(context));
//        request.setPhone(phone);
//        request.setMobile_brand(Utils.getDeviceName());
//        request.setMobile_model(Utils.getDeviceModel());
//        Call<SignUpAddNumberResponse> requestCall = mRestClient.requestRegisterNumber(ApiTags.BASE_SERVER_URL_SIGN_UP_X_API, request);


        Call<SignUpAddNumberResponse> requestCall = mRestClient.requestRegisterNumber(ApiTags.BASE_SERVER_URL_SIGN_UP_X_API,
                phone, Utils.getDeviceId(context), Utils.getDeviceName(), Utils.getDeviceModel(), AppPreferences.getLatitude() + "," + AppPreferences.getLongitude(), cnic, city);


        requestCall.enqueue(new Callback<SignUpAddNumberResponse>() {
            @Override
            public void onResponse(Response<SignUpAddNumberResponse> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }


    public void postOptionalSignupData(Context context, String id, String email, String referenceNo, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getBykeaSignUpApiClient();
        Call<SignUpOptionalDataResponse> requestCall = mRestClient.postOptionalSignupData(ApiTags.BASE_SERVER_URL_SIGN_UP_X_API,
                id, StringUtils.isNotBlank(email) ? email : null, StringUtils.isNotBlank(referenceNo) ? referenceNo : null);
        requestCall.enqueue(new Callback<SignUpOptionalDataResponse>() {
            @Override
            public void onResponse(Response<SignUpOptionalDataResponse> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void postBiometricVerification(Context context, String id, boolean isVerified, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getBykeaSignUpApiClient();
        Call<BiometricApiResponse> requestCall = mRestClient.postBiometricVerification(ApiTags.BASE_SERVER_URL_SIGN_UP_X_API,
                id, isVerified);
        requestCall.enqueue(new Callback<BiometricApiResponse>() {
            @Override
            public void onResponse(Response<BiometricApiResponse> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void requestCompleteSignupData(Context context, String id, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getBykeaSignUpApiClient();
        Call<SignUpCompleteResponse> requestCall = mRestClient.requestCompleteSignupData(ApiTags.BASE_SERVER_URL_SIGN_UP_X_API,
                id);
        requestCall.enqueue(new Callback<SignUpCompleteResponse>() {
            @Override
            public void onResponse(Response<SignUpCompleteResponse> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void uplodaDocumentImage(Context context, String id, String type, File file, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getBykeaSignUpApiClient();
        Call<SignupUplodaImgResponse> requestCall = mRestClient.uplodaDocumentImage(ApiTags.BASE_SERVER_URL_SIGN_UP_X_API,
                Utils.convertStringToRequestBody(id), Utils.convertStringToRequestBody(type), Utils.convertFileToRequestBody(file));
        requestCall.enqueue(new Callback<SignupUplodaImgResponse>() {
            @Override
            public void onResponse(Response<SignupUplodaImgResponse> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void getSettings(Context context, final IResponseCallback onResponseCallBack) {
        if (!AppPreferences.isLoggedIn() || AppPreferences.getPilotData() == null
                || AppPreferences.getPilotData().getCity() == null) {
            return;
        }
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<SettingsResponse> requestCall = mRestClient.getSettings("d",
                AppPreferences.getPilotData().getCity().get_id(), AppPreferences.getSettingsVersion());
        requestCall.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Response<SettingsResponse> response, Retrofit retrofit) {
                if (response == null || response.body() == null) {
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, ""
                            + mContext.getString(R.string.error_try_again) + " ");
                    return;
                }
                if (response.isSuccess()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void getSettingsBeforeLogin(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<SettingsResponse> requestCall = mRestClient.getSettings("d");
        requestCall.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Response<SettingsResponse> response, Retrofit retrofit) {
                if (response == null || response.body() == null) {
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, ""
                            + mContext.getString(R.string.error_try_again) + " ");
                    return;
                }
                if (response.isSuccess()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void getWalletHistory(Context context, final IResponseCallback onResponseCallBack,
                                 String driverId, String accessToken, String pageNo) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<WalletHistoryResponse> restCall = mRestClient.getWalletHistory(driverId,
                accessToken, "d", pageNo);
//        restCall.enqueue(new GenericRetrofitCallBackSuccess<WalletHistoryResponse>(onResponseCallBack));
        restCall.enqueue(new Callback<WalletHistoryResponse>() {
            @Override
            public void onResponse(Response<WalletHistoryResponse> response, Retrofit retrofit) {
                if (response == null || response.body() == null) {
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" + mContext.getString(R.string.error_try_again) + " ");
                    return;
                }
                if (response.isSuccess()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });

    }

    public void requestBankAccounts(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<BankAccountListResponse> restCall = mRestClient.getBankAccounts(AppPreferences.getPilotData().getId(),
                AppPreferences.getPilotData().getAccessToken(), "" + AppPreferences.getLatitude(), "" + AppPreferences.getLongitude());
        restCall.enqueue(new GenericRetrofitCallBack<BankAccountListResponse>(onResponseCallBack));
    }

    /***
     * Request driver status update to API server
     * @param context Calling context.
     * @param statusRequestBody Driver status request body which needs to be send to API Server
     * @param responseCallback Response callback handler.
     */
    public void requestDriverStatusUpdate(Context context,
                                          DriverAvailabilityRequest statusRequestBody,
                                          IResponseCallback responseCallback) {
        mContext = context;
        this.mResponseCallBack = responseCallback;
        mRestClient = RestClient.getClient(context);
        Call<PilotStatusResponse> restCall = mRestClient.updateDriverStatus(statusRequestBody);
        restCall.enqueue(new Callback<PilotStatusResponse>() {
            @Override
            public void onResponse(Response<PilotStatusResponse> response, Retrofit retrofit) {
                if (response == null || response.body() == null) {
                    if (response != null && response.errorBody() != null) {
                        PilotStatusResponse pilotStatusResponse =
                                Utils.parseAPIErrorResponse(response, retrofit, PilotStatusResponse.class);
                        if (pilotStatusResponse != null) {
                            mResponseCallBack.onResponse(pilotStatusResponse);
                        } else {
                            mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                    mContext.getString(R.string.error_try_again) + " ");
                        }
                    } else {
                        mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                mContext.getString(R.string.error_try_again) + " ");
                    }
                } else {
                    if (response.isSuccess()) {
                        mResponseCallBack.onResponse(response.body());
                    } else {
                        mResponseCallBack.onError(response.body().getCode(),
                                response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void requestBankAccountsDetails(Context context, String bankId, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<BankDetailsResponse> restCall = mRestClient.getBankAccountDetails(AppPreferences.getPilotData().getId(),
                AppPreferences.getPilotData().getAccessToken(), "" + AppPreferences.getLatitude(), "" + AppPreferences.getLongitude(), bankId);
        restCall.enqueue(new GenericRetrofitCallBack<BankDetailsResponse>(onResponseCallBack));
    }

    public void getContactNumbers(Context context, final IResponseCallback onResponseCallBack,
                                  String driverId, String accessToken) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<ContactNumbersResponse> restCall = mRestClient.getContactNumbers(driverId,
                accessToken, "d");
//        restCall.enqueue(new GenericRetrofitCallBackSuccess<WalletHistoryResponse>(onResponseCallBack));
        restCall.enqueue(new Callback<ContactNumbersResponse>() {
            @Override
            public void onResponse(Response<ContactNumbersResponse> response, Retrofit retrofit) {
                if (response == null || response.body() == null) {
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" + mContext.getString(R.string.error_try_again) + " ");
                    return;
                }
                if (response.isSuccess()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });

    }

    public void requestChangePin(Context context, String newPin, String oldPin, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<ChangePinResponse> requestCall = mRestClient.requestChangePin(AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(),
                newPin, oldPin, "d");
        requestCall.enqueue(new GenericRetrofitCallBackSuccess<ChangePinResponse>(onResponseCallBack));
    }

    public void getProfileData(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<GetProfileResponse> requestCall = mRestClient.requestProfileData(AppPreferences.getDriverId(), AppPreferences.getAccessToken(), "d");
        requestCall.enqueue(new GenericRetrofitCallBackSuccess<GetProfileResponse>(onResponseCallBack));
    }

    public void getCities(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<GetCitiesResponse> requestCall = mRestClient.getCities();
        requestCall.enqueue(new GenericRetrofitCallBackSuccess<GetCitiesResponse>(onResponseCallBack));
    }

    public synchronized void requestHeatMap(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
//        mRestClient = RestClient.getBykea2ApiClient(mContext);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String url = ApiTags.HEAT_MAP_2.replace("HOUR", "" + hour)
                .replace("CITY_NAME", StringUtils.capitalize(AppPreferences.getPilotData().getCity().getName()));
        Call<ArrayList<HeatMapUpdatedResponse>> requestCall = RestClient.getBykea2ApiClient(mContext).getHeatMap(ApiTags.HEAT_MAP_2_X_API, url);
        requestCall.enqueue(new Callback<ArrayList<HeatMapUpdatedResponse>>() {
            @Override
            public void onResponse(Response<ArrayList<HeatMapUpdatedResponse>> response, Retrofit retrofit) {
                if (response != null && response.isSuccess() && response.body() != null) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onResponse(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.redLog("onError", "HeatMapUpdatedResponse");
            }
        });
    }


    private class GenericRetrofitCallBackSuccess<T extends CommonResponse> implements Callback<T> {
        private IResponseCallback mCallBack;

        public GenericRetrofitCallBackSuccess(IResponseCallback callBack) {
            mCallBack = callBack;
        }

        @Override
        public void onResponse(Response<T> response, Retrofit retrofit) {
            if (response == null || response.body() == null) {
                mCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" + mContext.getString(R.string.error_try_again) + " ");
                return;
            }
            if (response.isSuccess()) {
                mCallBack.onResponse(response.body());
            } else {
                mCallBack.onError(response.body().getCode(), response.body().getMessage());
            }
        }

        @Override
        public void onFailure(Throwable t) {
            mCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
        }
    }

    public class GenericRetrofitCallBack<T extends CommonResponse> implements Callback<T> {
        private IResponseCallback mCallBack;

        public GenericRetrofitCallBack(IResponseCallback callBack) {
            mCallBack = callBack;
        }

        @Override
        public void onResponse(Response<T> response, Retrofit retrofit) {
            if (response == null || response.body() == null) {
                mCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                        mContext.getString(R.string.error_try_again) + " ");
                return;
            }
            if (response.body().isSuccess()) {
                mCallBack.onResponse(response.body());
            } else {
                mCallBack.onError(response.body().getCode(), response.body().getMessage());
            }
        }

        @Override
        public void onFailure(Throwable t) {
            mCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
        }
    }

    /***
     * Generic location handler for driver location update rest event.
     *
     */
    public class GenericLocationRetrofitCallBack implements Callback<LocationResponse> {
        private IResponseCallback mCallBack;

        public GenericLocationRetrofitCallBack(IResponseCallback callBack) {
            mCallBack = callBack;
        }

        @Override
        public void onResponse(Response<LocationResponse> response, Retrofit retrofit) {
            if (response == null || response.body() == null) {
                if (response != null && response.errorBody() != null) {
                    LocationResponse LocationResponse =
                            Utils.parseAPIErrorResponse(response, retrofit, LocationResponse.class);
                    if (LocationResponse != null) {
                        mResponseCallBack.onResponse(LocationResponse);
                        /*mResponseCallBack.onError(LocationResponse.getCode(),
                                LocationResponse.getMessage());*/
                    } else {
                        Utils.redLog(TAG, "Location on Failure: " + response.code() + " Internal Server Error");
                        mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                mContext.getString(R.string.error_try_again) + " ");
                    }

                } else {
                    Utils.redLog(TAG, "Location on Failure: " + response.code() + " Internal Server Error");
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                            mContext.getString(R.string.error_try_again) + " ");
                }
            } else {
                if (response.isSuccess()) {
                    if (AppPreferences.isLoggedIn() && response.body().getLocation() != null) {
                        if (StringUtils.isNotBlank(response.body().getLocation().getLat())
                                && StringUtils.isNotBlank(response.body().getLocation().getLng())) {
                            AppPreferences.saveLastUpdatedLocation(
                                    new LatLng(Double.parseDouble(response.body().getLocation().getLat()),
                                            Double.parseDouble(response.body().getLocation().getLng())));
                        }
                        Utils.saveServerTimeDifference(response.body().getTimeStampServer());
                    }
                    if (AppPreferences.isWalletAmountIncreased()) {
                        AppPreferences.setWalletAmountIncreased(false);
                        AppPreferences.setAvailableStatus(true);
                    }
                    if (AppPreferences.isOutOfFence()) {
                        AppPreferences.setOutOfFence(false);
                        AppPreferences.setAvailableStatus(true);
                    }
                        mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(),
                            response.body().getMessage());
                }
            }


        }

        @Override
        public void onFailure(Throwable t) {
            Utils.redLog(TAG, "Location on Failure: " + t.getMessage());
            mCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
        }
    }

    public void requestDriverDropOff(Context context, IResponseCallback onResponseCallBack,
                                     String lat, String lng, String address) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<DriverDestResponse> requestCall = mRestClient.setDriverDroppOff(AppPreferences.getDriverId()
                , AppPreferences.getAccessToken(), lat, lng, address);
        requestCall.enqueue(new GenericRetrofitCallBackSuccess<DriverDestResponse>(onResponseCallBack));
    }

    public void updateRegid(IResponseCallback onResponseCallBack,
                            String id,
                            String reg_id,
                            String tokenId, Context context) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<UpdateRegIDResponse> requestCall = mRestClient.updateRegid(id, id, tokenId, reg_id,
                "android", "d");
        requestCall.enqueue(new GenericRetrofitCallBack<UpdateRegIDResponse>(onResponseCallBack));
    }

    public void requestZones(Context context, IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<GetZonesResponse> restCall = mRestClient.requestZones(AppPreferences.getLatitude() + "", AppPreferences.getLongitude() + "");
        restCall.enqueue(new GenericRetrofitCallBack<GetZonesResponse>(onResponseCallBack));
    }

    public void requestShahkar(Context context, IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<ShahkarResponse> restCall = mRestClient.requestShahkar(AppPreferences.getDriverId(), AppPreferences.getAccessToken());
        restCall.enqueue(new GenericRetrofitCallBack<ShahkarResponse>(onResponseCallBack));
    }

    public void requestBonusChart(Context context, IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<RankingResponse> restCall = mRestClient.requestBonusStats(AppPreferences.getDriverId(), AppPreferences.getAccessToken(),
                AppPreferences.getPilotData().getCity().get_id());
        restCall.enqueue(new GenericRetrofitCallBack<RankingResponse>(onResponseCallBack));
    }

    public void requestPerformanceData(Context context, IResponseCallback onResponseCallBack, int weekStatus) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<DriverPerformanceResponse> restCall = mRestClient.requestDriverPerformance(AppPreferences.getDriverId(), AppPreferences.getAccessToken(),
                weekStatus);
        restCall.enqueue(new GenericRetrofitCallBack<DriverPerformanceResponse>(onResponseCallBack));
    }

    public void requestLoadBoard(Context context, IResponseCallback onResponseCallBack, String lat, String lng) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<LoadBoardResponse> restCall = mRestClient.requestLoadBoard(AppPreferences.getDriverId(), AppPreferences.getAccessToken(),
                lat, lng);
        restCall.enqueue(new GenericRetrofitCallBack<LoadBoardResponse>(onResponseCallBack));
    }

    public void requestZoneAreas(Context context, ZoneData zone, IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<ZoneAreaResponse> restCall = mRestClient.requestZoneAreas(zone.get_id());
        restCall.enqueue(new GenericRetrofitCallBack<ZoneAreaResponse>(onResponseCallBack));
    }


    public void addSavedPlace(Context context, final IResponseCallback onResponseCallback,
                              SavedPlaces savedPlaces) {
        mContext = context;
        this.mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);
        savedPlaces.setUserId(AppPreferences.getPilotData().getId());
        savedPlaces.setToken_id(AppPreferences.getPilotData().getAccessToken());

        Call<AddSavedPlaceResponse> requestCall = mRestClient.addSavedPlace(savedPlaces);
        requestCall.enqueue(new GenericRetrofitCallBack<AddSavedPlaceResponse>(onResponseCallback));

    }

    public void updateSavedPlace(Context context, final IResponseCallback onResponseCallback,
                                 SavedPlaces savedPlaces) {
        mContext = context;
        this.mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);
        savedPlaces.setUserId(AppPreferences.getPilotData().getId());
        savedPlaces.setToken_id(AppPreferences.getPilotData().getAccessToken());

        Call<AddSavedPlaceResponse> requestCall = mRestClient.updateSavedPlace(savedPlaces);
        requestCall.enqueue(new GenericRetrofitCallBack<AddSavedPlaceResponse>(onResponseCallback));

    }

    public void deleteSavedPlace(Context context, final IResponseCallback onResponseCallback,
                                 SavedPlaces savedPlaces) {
        mContext = context;
        this.mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);
        DeletePlaceRequest request = new DeletePlaceRequest();
        request.setUserId(AppPreferences.getPilotData().getId());
        request.setToken_id(AppPreferences.getPilotData().getAccessToken());
        request.setPlaceId(savedPlaces.getPlaceId());

        Call<DeleteSavedPlaceResponse> requestCall = mRestClient.deleteSavedPlace(request);
        requestCall.enqueue(new GenericRetrofitCallBack<DeleteSavedPlaceResponse>(onResponseCallback));

    }

    public void getSavedPlaces(Context context, final IResponseCallback onResponseCallback) {
        mContext = context;
        this.mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);

        Call<GetSavedPlacesResponse> requestCall = mRestClient.getSavedPlaces(AppPreferences.getPilotData().getId(), AppPreferences.getPilotData().getAccessToken());
        requestCall.enqueue(new GenericRetrofitCallBack<GetSavedPlacesResponse>(onResponseCallback));

    }

    public void topUpPassengerWallet(Context context, NormalCallData callData, String amount, final IResponseCallback onResponseCallback) {
        mContext = context;
        this.mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);

        Call<TopUpPassWalletResponse> requestCall = mRestClient.topUpPassengerWallet(AppPreferences.getPilotData().getId(),
                AppPreferences.getPilotData().getAccessToken(), callData.getTripNo(), amount, callData.getPassId());
        requestCall.enqueue(new GenericRetrofitCallBack<TopUpPassWalletResponse>(onResponseCallback));

    }


    @NonNull
    private String getErrorMessage(Throwable error) {
        String errorMsg;
        if (error instanceof IOException) {
            Utils.redLog(Constants.LogTags.RETROFIT_ERROR, Constants.LogTags.TIME_OUT_ERROR + String.valueOf(error.getCause()));
            errorMsg = mContext.getString(R.string.internet_error);
            //To prompt user to input base url for local builds again in case when URL is not working/wrong url. (BS-1017)
            /*AppPreferences.setLocalBaseUrl(BuildConfig.FLAVOR_URL);
            ApiTags.LOCAL_BASE_URL = BuildConfig.FLAVOR_URL;*/
        } else if (error instanceof IllegalStateException) {
            Utils.redLog(Constants.LogTags.RETROFIT_ERROR, Constants.LogTags.CONVERSION_ERROR + String.valueOf(error.getCause()));
            errorMsg = mContext.getString(R.string.error_try_again);
        } else {
            Utils.redLog(Constants.LogTags.RETROFIT_ERROR, Constants.LogTags.OTHER_ERROR + String.valueOf(error.getLocalizedMessage()));
            errorMsg = mContext.getString(R.string.error_try_again);
        }
        return errorMsg;
    }

    public void postProblem(Context context,
                            String selectedReason,
                            String tripId,
                            String email,
                            String contactType,
                            String details,
                            boolean isFromReport,
                            IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<ProblemPostResponse> restCall = mRestClient.postProblem(AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(),
                selectedReason,
                tripId,
                email,
                contactType,
                AppPreferences.getPilotData().getFullName(),
                AppPreferences.getPilotData().getPhoneNo(),
                details,
                isFromReport,
                "d");
        restCall.enqueue(new GenericRetrofitCallBackSuccess<ProblemPostResponse>(onResponseCallBack));

    }

    public void downloadAudioFile(Context context, final IResponseCallback onResponseCallBack,
                                  final String url) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<ResponseBody> restCall = mRestClient.downloadAudioFile(url);
        restCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    Utils.redLog("DownloadAudio", "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());
                    if (writtenToDisk) {
                        DownloadAudioFileResponse response1 = new DownloadAudioFileResponse();
                        response1.setLink(url);
                        response1.setPath(mContext.getExternalFilesDir(null) + File.separator + "bykea_msg.wav");
                        onResponseCallBack.onResponse(response1);
                    }
                    Utils.redLog("DownloadAudio", "file download was a success? " + writtenToDisk);
                } else {
                    Utils.redLog("DownloadAudio", "server contact failed");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" + getErrorMessage(t));
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File futureStudioIconFile = new File(mContext.getExternalFilesDir(null) + File.separator + "bykea_msg.wav");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Utils.redLog("DownloadAudio", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void callGeoCoderApi(final String latitude, final String longitude,
                                final IResponseCallback mDataCallback, Context context) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<GeocoderApi> call = restClient.callGeoCoderApi(latitude + "," + longitude, Utils.getApiKeyForGeoCoder());
        call.enqueue(new Callback<GeocoderApi>() {
            @Override
            public void onResponse(Response<GeocoderApi> geocoderApiResponse, Retrofit retrofit) {
                String add = StringUtils.EMPTY;
                if (geocoderApiResponse != null && geocoderApiResponse.isSuccess()) {
                    if (geocoderApiResponse.body() != null
                            && geocoderApiResponse.body().getStatus().equalsIgnoreCase(Constants.STATUS_CODE_OK)
                            && geocoderApiResponse.body().getResults().length > 0) {
                        String address = StringUtils.EMPTY;
                        String subLocality = StringUtils.EMPTY;
//                        String postalCode = StringUtils.EMPTY;
                        String cityName = StringUtils.EMPTY;
                        String streetNumber = StringUtils.EMPTY;
                        GeocoderApi.Address_components[] address_componentses = geocoderApiResponse.body().getResults()[0].getAddress_components();
                        for (GeocoderApi.Address_components addressComponent : address_componentses) {
                            String[] types = addressComponent.getTypes();
                            for (String type : types) {

                                if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_CITY)) {
                                    cityName = addressComponent.getLong_name();
//                                    setOneSignalTags(cityName, false);
//                                    AppPreferences.setCityForNearByRequest(mContext, cityName);
                                }
                                if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_STREET_NUMBER)) {
                                    streetNumber = addressComponent.getLong_name();
                                }
                                if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS)
                                        || type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS_1)) {
                                    address = addressComponent.getLong_name();
                                }
                                if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_ADDRESS_SUB_LOCALITY)) {
                                    subLocality = addressComponent.getLong_name();
                                }
                                if (StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(subLocality)) {
                                    break;
                                }
                            }
                            if (StringUtils.isNotBlank(cityName) && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(subLocality)) {
                                break;
                            }
                        }
                        if (StringUtils.isNotBlank(subLocality)) {
                            if (StringUtils.isNotBlank(address)) {
                                address = address + " " + subLocality;
                            } else {
                                address = subLocality;
                            }
                        }
                        if (StringUtils.isNotBlank(address)) {
                            add = address;
                        }

                        if (StringUtils.isNotBlank(address)) {
                            add = address + ";" + cityName;
                        }
                        if (StringUtils.isNotBlank(add)) {
                            mDataCallback.onResponse(add);
                        } else {
                            AppPreferences.setGeoCoderApiKeyRequired(true);
                            mDataCallback.onError(0, "No Address Found");
                        }
                    } else {
                        AppPreferences.setGeoCoderApiKeyRequired(true);
                    }
                } else {
                    AppPreferences.setGeoCoderApiKeyRequired(true);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AppPreferences.setGeoCoderApiKeyRequired(true);
                Utils.redLog("GeoCode", t.getMessage() + "");
            }
        });
    }

    public void getDistanceMatriax(String origin, String destination, final IResponseCallback mDataCallback, Context context) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<GoogleDistanceMatrixApi> call = restClient.callDistanceMatrixApi(origin, destination, Utils.getApiKeyForDirections(mContext));
        call.enqueue(new Callback<GoogleDistanceMatrixApi>() {
            @Override
            public void onResponse(Response<GoogleDistanceMatrixApi> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body() != null) {
                    mDataCallback.onResponse(response.body());
                    if (Constants.INVALID_REQUEST.equalsIgnoreCase(response.body().getStatus()) ||
                            Constants.OVER_QUERY_LIMIT.equalsIgnoreCase(response.body().getStatus())) {
                        AppPreferences.setDirectionsApiKeyRequired(true);
                    }
                } else {
                    AppPreferences.setDirectionsApiKeyRequired(true);
                    mDataCallback.onError(0, "Could not get the distance matrix");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AppPreferences.setDirectionsApiKeyRequired(true);
                mDataCallback.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" + getErrorMessage(t));
            }
        });
    }


    public void autocomplete(Context context, String input, final IResponseCallback mDataCallback) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<PlaceAutoCompleteResponse> call = restClient.getAutoCompletePlaces(input, Utils.getCurrentLocation(), Constants.COUNTRY_CODE_AUTOCOMPLETE, "35000", Constants.GOOGLE_PLACE_AUTOCOMPLETE_API_KEY);
        call.enqueue(new Callback<PlaceAutoCompleteResponse>() {
            @Override
            public void onResponse(Response<PlaceAutoCompleteResponse> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body() != null &&
                        response.body().getStatus().equalsIgnoreCase("OK")) {
                    mDataCallback.onResponse(response.body());
                } else {
                    mDataCallback.onError(0, response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mDataCallback.onError(0, t.toString());
            }
        });
//        PlaceAutoCompleteResponse placeAutoCompleteResponse = null;
//        try {
//            Response<PlaceAutoCompleteResponse> response = call.execute();
//            if (response.body().getStatus().equalsIgnoreCase("OK")) {
//                placeAutoCompleteResponse = response.body();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Utils.redLog("AutoComplete", "Api called with input = " + input);
//        return placeAutoCompleteResponse;
    }


    public void getPlaceDetails(String s, Context context, final IResponseCallback mDataCallback) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<PlaceDetailsResponse> call = restClient.getPlaceDetails(s, Constants.GOOGLE_PLACE_AUTOCOMPLETE_API_KEY);
        call.enqueue(new Callback<PlaceDetailsResponse>() {
            @Override
            public void onResponse(Response<PlaceDetailsResponse> response, Retrofit retrofit) {
                if (response.isSuccess() && response.body() != null) {
                    mDataCallback.onResponse(response.body());
                } else {
                    mDataCallback.onError(0, response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mDataCallback.onError(0, t.toString());
            }
        });

    }


    /**
     * This method clears Singleton instance of Bykea's retrofit client when URL is changed for Local builds
     */
    public void clearRetrofitClient() {
        RestClient.clearBykeaRetrofitClient();
    }

}
