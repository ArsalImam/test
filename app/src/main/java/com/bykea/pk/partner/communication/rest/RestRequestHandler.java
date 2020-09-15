package com.bykea.pk.partner.communication.rest;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.dal.source.remote.response.BookingListingResponse;
import com.bykea.pk.partner.models.data.Address;
import com.bykea.pk.partner.models.data.OSMGeoCode;
import com.bykea.pk.partner.models.data.RankingResponse;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.models.data.SignUpAddNumberResponse;
import com.bykea.pk.partner.models.data.SignUpOptionalDataResponse;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.models.data.SignupUplodaImgResponse;
import com.bykea.pk.partner.models.data.ZoneData;
import com.bykea.pk.partner.models.request.DeletePlaceRequest;
import com.bykea.pk.partner.models.request.DriverAvailabilityRequest;
import com.bykea.pk.partner.models.request.DriverLocationRequest;
import com.bykea.pk.partner.models.request.LoadBoardBookingCancelRequest;
import com.bykea.pk.partner.models.response.AcceptLoadboardBookingResponse;
import com.bykea.pk.partner.models.response.AddSavedPlaceResponse;
import com.bykea.pk.partner.models.response.BankAccountListResponse;
import com.bykea.pk.partner.models.response.BankDetailsResponse;
import com.bykea.pk.partner.models.response.BiometricApiResponse;
import com.bykea.pk.partner.models.response.BykeaDistanceMatrixResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.DeleteSavedPlaceResponse;
import com.bykea.pk.partner.models.response.DownloadAudioFileResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.DriverPerformanceResponse;
import com.bykea.pk.partner.models.response.DriverVerifiedBookingResponse;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.models.response.GeoCodeApiResponse;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.GetProfileResponse;
import com.bykea.pk.partner.models.response.GetSavedPlacesResponse;
import com.bykea.pk.partner.models.response.GetZonesResponse;
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
import com.bykea.pk.partner.models.response.UpdateAppVersionResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UpdateRegIDResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadImageFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.models.response.ZoneAreaResponse;
import com.bykea.pk.partner.repositories.IUserDataHandler;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.apache.commons.lang3.StringUtils.SPACE;

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
                phoneNumber, OtpType, deviceType, latitude, longitude, Utils.getVersion());

        numberResponseCall.enqueue(new Callback<VerifyNumberResponse>() {

            @Override
            public void onResponse(Call<VerifyNumberResponse> call, Response<VerifyNumberResponse> response) {
                if (response == null || response.body() == null) {
                    if (response != null && response.errorBody() != null) {
                        VerifyNumberResponse verifyNumberResponse =
                                Utils.parseAPIErrorResponse(response, VerifyNumberResponse.class);
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
                    if (response.isSuccessful()) {
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
            public void onFailure(Call<VerifyNumberResponse> call, Throwable t) {
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
                Utils.getVersion(),
                AppPreferences.getOneSignalPlayerId(),
                AppPreferences.getADID(),
                Utils.getDeviceId(context)
        );

        restCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response == null || response.body() == null) {
                    if (response != null && response.errorBody() != null) {
                        LoginResponse loginResponse =
                                Utils.parseAPIErrorResponse(response, LoginResponse.class);
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
                    if (response.isSuccessful()) {
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
            public void onFailure(Call<LoginResponse> call, Throwable t) {
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
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
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
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
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
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
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
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
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
            public void onResponse(Call<VerifyNumberResponse> call, Response<VerifyNumberResponse> response) {
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
            public void onFailure(Call<VerifyNumberResponse> call, Throwable t) {
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
            public void onResponse(Call<VerifyCodeResponse> call, Response<VerifyCodeResponse> response) {
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
            public void onFailure(Call<VerifyCodeResponse> call, Throwable t) {
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
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.body().getCode() == HTTPStatus.OK ||
                        response.body().getCode() == HTTPStatus.CREATED) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(),
                            response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });

    }

    /**
     * this method can be used to get all trips history from driver id
     * this is a legacy function and is replaced with {@link #requestBookingListing(Context, IResponseCallback, String, String)}
     * and will be removed in the future release
     *
     * @param context            component which requires data
     * @param onResponseCallBack callback to receive data on task completed
     * @param pageNo             param needs to send for pagination
     * @param tripHistoryId      (optional) if any specific trip details needed
     */
    @Deprecated
    public void getTripHistory(Context context, final IResponseCallback onResponseCallBack, String pageNo, String tripHistoryId) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<TripHistoryResponse> restCall = mRestClient.getTripHistory(AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(), Constants.USER_TYPE, pageNo, tripHistoryId);
        restCall.enqueue(new Callback<TripHistoryResponse>() {
            @Override
            public void onResponse(Call<TripHistoryResponse> call, Response<TripHistoryResponse> response) {
                // Got success from server
                if (null != response.body()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<TripHistoryResponse> call, Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });
    }

    /**
     * this method can be used to get all booking listing by driver id from kronos
     *
     * @param context            context component which requires data
     * @param onResponseCallBack onResponseCallBack callback to receive data on task completed
     * @param pageNo             param needs to send for pagination
     * @param limit              number of records per page
     */
    public void requestBookingListing(Context context, final IResponseCallback onResponseCallBack, String pageNo, String limit) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);

        if (AppPreferences.getDriverSettings() == null ||
                AppPreferences.getDriverSettings().getData() == null ||
                StringUtils.isBlank(AppPreferences.getDriverSettings().getData().getBookingLisitingForDriverUrl())) {
            Dialogs.INSTANCE.dismissDialog();
            Utils.appToast(DriverApp.getContext().getString(R.string.settings_are_not_updated));
            return;
        }

        Call<BookingListingResponse> restCall = mRestClient.getBookingListing(
                AppPreferences.getDriverSettings().getData().getBookingLisitingForDriverUrl(),
                AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(),
                Constants.BookingFetchingStates.END,
                pageNo,
                limit,
                Constants.SORT_BY_NEWEST);

        restCall.enqueue(new Callback<BookingListingResponse>() {
            @Override
            public void onResponse(Call<BookingListingResponse> call, Response<BookingListingResponse> response) {
                // Got success from server
                if (null != response.body()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<BookingListingResponse> call, Throwable t) {
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
            public void onResponse(Call<TripMissedHistoryResponse> call, Response<TripMissedHistoryResponse> response) {
                // Got success from server
                if (null != response.body()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<TripMissedHistoryResponse> call, Throwable t) {
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    public void checkRunningTrip(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<CheckDriverStatusResponse> restCall = mRestClient.checkRunningTrip(
                AppPreferences.getDriverId(),
                AppPreferences.getAccessToken());
        restCall.enqueue(new Callback<CheckDriverStatusResponse>() {
            @Override
            public void onResponse(Call<CheckDriverStatusResponse> call, Response<CheckDriverStatusResponse> response) {
                // Got success from server
                Dialogs.INSTANCE.dismissDialog();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mResponseCallBack.onResponse(response.body());
                    Utils.redLog(TAG, new Gson().toJson(response.body().getData()));
                } else {
                    mResponseCallBack.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<CheckDriverStatusResponse> call, Throwable t) {
                Dialogs.INSTANCE.dismissDialog();
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    /**
     * USE WHEN YOU WANT TO DISMISS WHEN THE SUCCESSFUL DATA IS RETRIEVE FOR THE ACTIVE TRIP
     *
     * @param context            : Calling Activity
     * @param onResponseCallBack : Override in Calling Acitivity
     */

    public void checkActiveTrip(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<CheckDriverStatusResponse> restCall = mRestClient.checkRunningTrip(
                AppPreferences.getDriverId(),
                AppPreferences.getAccessToken());
        restCall.enqueue(new Callback<CheckDriverStatusResponse>() {
            @Override
            public void onResponse(Call<CheckDriverStatusResponse> call, Response<CheckDriverStatusResponse> response) {
                // Got success from server
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    mResponseCallBack.onResponse(response.body());
                    Utils.redLog(TAG, new Gson().toJson(response.body().getData()));
                } else {
                    mResponseCallBack.onError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<CheckDriverStatusResponse> call, Throwable t) {
                Dialogs.INSTANCE.dismissDialog();
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });
    }

    //THIS METHOD IS TO UPLOAD AUDIO MESSAGE FILE
    public void uploadAudioFile(Context context, IResponseCallback responseCallBack, final File file) {
        mContext = context;
        mResponseCallBack = responseCallBack;
        mRestClient = RestClient.getChatAudioClient(mContext);
        Call<UploadAudioFile> requestCall = mRestClient.uploadAudioFile(Utils.convertFileToRequestBody(file));
        requestCall.enqueue(new Callback<UploadAudioFile>() {
            @Override
            public void onResponse(Call<UploadAudioFile> call, Response<UploadAudioFile> response) {
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
            public void onFailure(Call<UploadAudioFile> call, Throwable t) {
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
            public void onResponse(Call<UploadImageFile> call, Response<UploadImageFile> response) {
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
            public void onFailure(Call<UploadImageFile> call, Throwable t) {
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
            public void onResponse(Call<ServiceTypeResponse> call, Response<ServiceTypeResponse> response) {
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
            public void onFailure(Call<ServiceTypeResponse> call, Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
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
            public void onResponse(Call<SignUpSettingsResponse> call, Response<SignUpSettingsResponse> response) {
                if (response.isSuccessful() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, mContext.getString(R.string.error_try_again));
                }
            }

            @Override
            public void onFailure(Call<SignUpSettingsResponse> call, Throwable t) {
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
            public void onResponse(Call<SignUpAddNumberResponse> call, Response<SignUpAddNumberResponse> response) {
                if (response.isSuccessful() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SignUpAddNumberResponse> call, Throwable t) {
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
            public void onResponse(Call<SignUpOptionalDataResponse> call, Response<SignUpOptionalDataResponse> response) {
                if (response.isSuccessful() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SignUpOptionalDataResponse> call, Throwable t) {
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
            public void onResponse(Call<BiometricApiResponse> call, Response<BiometricApiResponse> response) {
                if (response.isSuccessful() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<BiometricApiResponse> call, Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });
    }

    public void uploadDocumentImage(Context context, String id, String type, File file, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getBykeaSignUpApiClient();
        Call<SignupUplodaImgResponse> requestCall = mRestClient.uplodaDocumentImage(ApiTags.BASE_SERVER_URL_SIGN_UP_X_API,
                Utils.convertStringToRequestBody(id), Utils.convertStringToRequestBody(type), Utils.convertFileToRequestBody(file));
        requestCall.enqueue(new Callback<SignupUplodaImgResponse>() {
            @Override
            public void onResponse(Call<SignupUplodaImgResponse> call, Response<SignupUplodaImgResponse> response) {
                if (response.isSuccessful() && response.body().getCode() == HTTPStatus.OK) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body() != null ? response.body().getCode() : 0, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SignupUplodaImgResponse> call, Throwable t) {
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
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                if (response == null || response.body() == null) {
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, ""
                            + mContext.getString(R.string.error_try_again) + " ");
                    return;
                }
                if (response.isSuccessful()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
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
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                if (response == null || response.body() == null) {
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, ""
                            + mContext.getString(R.string.error_try_again) + " ");
                    return;
                }
                if (response.isSuccessful()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
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
            public void onResponse(Call<WalletHistoryResponse> call, Response<WalletHistoryResponse> response) {
                if (response == null || response.body() == null) {
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" + mContext.getString(R.string.error_try_again) + " ");
                    return;
                }
                if (response.isSuccessful()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<WalletHistoryResponse> call, Throwable t) {
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
    public void requestDriverStatusUpdate(final Context context,
                                          DriverAvailabilityRequest statusRequestBody,
                                          IResponseCallback responseCallback) {
        mContext = context;
        this.mResponseCallBack = responseCallback;
        mRestClient = RestClient.getClient(context);
        Call<PilotStatusResponse> restCall = mRestClient.updateDriverStatus(statusRequestBody);
        restCall.enqueue(new Callback<PilotStatusResponse>() {
            @Override
            public void onResponse(Call<PilotStatusResponse> call, Response<PilotStatusResponse> response) {
                if (response == null || response.body() == null) {
                    if (response != null && response.errorBody() != null) {
                        PilotStatusResponse pilotStatusResponse =
                                Utils.parseAPIErrorResponse(response, PilotStatusResponse.class);
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
                    if (response.isSuccessful()) {
                        mResponseCallBack.onResponse(response.body());
                    } else {
                        mResponseCallBack.onError(response.body().getCode(),
                                response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PilotStatusResponse> call, Throwable t) {
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
            public void onResponse(Call<ContactNumbersResponse> call, Response<ContactNumbersResponse> response) {
                if (response == null || response.body() == null) {
                    mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" + mContext.getString(R.string.error_try_again) + " ");
                    return;
                }
                if (response.isSuccessful()) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ContactNumbersResponse> call, Throwable t) {
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
            public void onResponse(Call<ArrayList<HeatMapUpdatedResponse>> call, Response<ArrayList<HeatMapUpdatedResponse>> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    mResponseCallBack.onResponse(response.body());
                } else {
                    mResponseCallBack.onResponse(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<HeatMapUpdatedResponse>> call, Throwable t) {
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
        public void onResponse(Call<T> call, Response<T> response) {
            if (response == null || response.body() == null) {
                mCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" + mContext.getString(R.string.error_try_again) + " ");
                return;
            }
            if (response.isSuccessful()) {
                mCallBack.onResponse(response.body());
            } else {
                mCallBack.onError(response.body().getCode(), response.body().getMessage());
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            mCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
        }
    }

    public class GenericRetrofitCallBack<T extends CommonResponse> implements Callback<T> {
        private IResponseCallback mCallBack;

        public GenericRetrofitCallBack(IResponseCallback callBack) {
            mCallBack = callBack;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
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
        public void onFailure(Call<T> call, Throwable t) {
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
        public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.errorBody() != null) {
                    LocationResponse LocationResponse =
                            Utils.parseAPIErrorResponse(response, LocationResponse.class);
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
                if (response.isSuccessful()) {
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
        public void onFailure(Call<LocationResponse> call, Throwable t) {
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

    /**
     * this method will execute api to get booking history stats from kronos
     *
     * @param context            of the activity
     * @param onResponseCallBack callback to send data back on the requested controllers
     */
    public void requestDriverVerifiedBookingStats(Context context, int weekStatus, IResponseCallback onResponseCallBack) {
        if (AppPreferences.getDriverSettings() != null &&
                AppPreferences.getDriverSettings().getData() != null &&
                StringUtils.isNotBlank(AppPreferences.getDriverSettings().getData().getKronosPartnerSummary())) {
            mContext = context;
            mRestClient = RestClient.getClient(mContext);
            Call<DriverVerifiedBookingResponse> restCall =
                    mRestClient.requestDriverVerifiedBookingStats(
                            AppPreferences.getDriverSettings().getData().getKronosPartnerSummary(),
                            AppPreferences.getDriverId(),
                            AppPreferences.getAccessToken(),
                            weekStatus);

            restCall.enqueue(new GenericRetrofitCallBack<>(onResponseCallBack));
        } else {
            Dialogs.INSTANCE.dismissDialog();
            Utils.appToast(DriverApp.getContext().getString(R.string.settings_are_not_updated));
        }
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

    /**
     * accept request for specific booking
     *
     * @param context            Context
     * @param bookingId          selected booking id
     * @param onResponseCallback callback
     */
    public void acceptLoadboardBooking(Context context, String bookingId, final IResponseCallback onResponseCallback) {
        mContext = context;
        this.mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);

        Call<AcceptLoadboardBookingResponse> requestCall = mRestClient.acceptLoadboardBooking(
                bookingId,
                AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(),
                String.valueOf(AppPreferences.getLatitude()),
                String.valueOf(AppPreferences.getLongitude()));
        requestCall.enqueue(new Callback<AcceptLoadboardBookingResponse>() {
            @Override
            public void onResponse(Call<AcceptLoadboardBookingResponse> call, Response<AcceptLoadboardBookingResponse> response) {
                if (response == null || response.body() == null) {
                    if (response != null && response.errorBody() != null) {
                        AcceptLoadboardBookingResponse acceptLoadboardBookingResponse =
                                Utils.parseAPIErrorResponse(response, AcceptLoadboardBookingResponse.class);
                        if (acceptLoadboardBookingResponse != null) {
                            mResponseCallBack.onResponse(acceptLoadboardBookingResponse);
                        } else {
                            mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                    mContext.getString(R.string.error_try_again) + " ");
                        }
                    } else {
                        mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                                mContext.getString(R.string.error_try_again) + " ");
                    }
                } else {
                    if (response.isSuccessful()) {
                        mResponseCallBack.onResponse(response.body());
                    } else {
                        mResponseCallBack.onError(response.body().getCode(), response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AcceptLoadboardBookingResponse> call, Throwable t) {
                mResponseCallBack.onError(HTTPStatus.INTERNAL_SERVER_ERROR, getErrorMessage(t));
            }
        });

    }

    @NonNull
    private String getErrorMessage(Throwable error) {
        String errorMsg;
        if (error instanceof IOException) {
            Utils.redLog(Constants.LogTags.RETROFIT_ERROR, Constants.LogTags.TIME_OUT_ERROR + String.valueOf(error.getCause()));
            if (mContext == null)
                mContext = DriverApp.getContext();
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
        mRestClient = RestClient.getChatAudioClient(mContext);
        Call<ResponseBody> restCall = mRestClient.downloadAudioFile(url);
        restCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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

    /**
     * call osm reverse code api to get address from latitude/longitude (gps)
     *
     * @param latitude      of location from which address is required
     * @param longitude     of location from which address is required
     * @param mDataCallback callback to pass the control in case of success and failure
     * @param context       of the activity
     */
    public void callOSMGeoCoderApi(final String latitude, final String longitude,
                                   final IResponseCallback mDataCallback, Context context) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();

        Call<OSMGeoCode> call = restClient.callOSMGeoCoderApi(String.format(ApiTags.GeocodeOSMApis.GEOCODER_URL, latitude, longitude));
        call.enqueue(new Callback<OSMGeoCode>() {
            @Override
            public void onResponse(Call<OSMGeoCode> call, Response<OSMGeoCode> osmGeoCodeResponse) {
                String address = StringUtils.EMPTY;

                if (osmGeoCodeResponse != null && osmGeoCodeResponse.isSuccessful()) {
                    OSMGeoCode body = osmGeoCodeResponse.body();
                    if (body != null && body.getAddress() != null) {
                        Address geocodeAddress = body.getAddress();

                        String addressFirstPart = StringUtils.EMPTY;
                        //Use one of these only from house_number, office, amenity or building
                        if (StringUtils.isNotEmpty(geocodeAddress.getHouse_number())) {
                            addressFirstPart = geocodeAddress.getHouse_number() + SPACE;
                        } else if (StringUtils.isNotEmpty(geocodeAddress.getOffice())) {
                            addressFirstPart = geocodeAddress.getOffice() + SPACE;
                        } else if (StringUtils.isNotEmpty(geocodeAddress.getAmenity())) {
                            addressFirstPart = geocodeAddress.getAmenity() + SPACE;
                        } else if (StringUtils.isNotEmpty(geocodeAddress.getBuilding())) {
                            addressFirstPart = geocodeAddress.getBuilding() + SPACE;
                        }
                        address += addressFirstPart;

                        //concat road if found
                        String addressSecondPart = StringUtils.EMPTY;
                        if (StringUtils.isNotEmpty(geocodeAddress.getRoad())) {
                            addressSecondPart = geocodeAddress.getRoad() + SPACE;
                        }
                        address += addressSecondPart;


                        //concat neighbourhood if found
                        String addressThirdPart = StringUtils.EMPTY;
                        if (StringUtils.isNotEmpty(geocodeAddress.getNeighbourhood())) {
                            addressThirdPart = geocodeAddress.getNeighbourhood() + SPACE;
                        }
                        address += addressThirdPart;


                        //concat town if found
                        String addressFourthPart = StringUtils.EMPTY;
                        if (StringUtils.isNotEmpty(geocodeAddress.getTown())) {
                            addressFourthPart = geocodeAddress.getTown() + SPACE;
                        }
                        address += addressFourthPart;


                        //if nothing found from Address object from response, assign display_name
                        //to address by checking its not empty
                        if (StringUtils.isEmpty(address) && StringUtils.isNotEmpty(body.getDisplay_name())) {
                            address = body.getDisplay_name();
                        } else {


                            //concat suburb if we found only one from first second or third part of address
                            if ((address.trim().equals(addressFirstPart.trim()) ||
                                    address.trim().equals(addressSecondPart.trim()) ||
                                    address.trim().equals(addressThirdPart.trim())) &&
                                    StringUtils.isNotEmpty(geocodeAddress.getSuburb())) {
                                address += geocodeAddress.getSuburb() + SPACE;
                            }

                            //concat hamlet if we found only neighbourhood and no suburb
                            if (address.trim().equals(addressThirdPart.trim()) &&
                                    StringUtils.isEmpty(geocodeAddress.getSuburb()) &&
                                    StringUtils.isNotEmpty(geocodeAddress.getHamlet())) {
                                address += geocodeAddress.getHamlet();
                            }

                            //concat suburb before address if we found only town
                            if (address.trim().equals(addressFourthPart.trim()) &&
                                    StringUtils.isNotEmpty(geocodeAddress.getSuburb())) {
                                address = geocodeAddress.getSuburb() + SPACE + address;
                            }

                        }
                    }
                    if (StringUtils.isNotEmpty(address)) {
                        mDataCallback.onResponse(address);
                    } else {
                        mDataCallback.onError(HTTPStatus.INTERNAL_SERVER_ERROR, Constants.NO_ADDRESS_FOUND);
                    }
                } else {
                    mDataCallback.onError(HTTPStatus.INTERNAL_SERVER_ERROR, Constants.NO_ADDRESS_FOUND);
                }
            }

            @Override
            public void onFailure(Call<OSMGeoCode> call, Throwable t) {
                Utils.redLog("GeoCode", t.getMessage() + "");
            }
        });
    }

    public void callGeoCoderApi(final String latitude, final String longitude,
                                final IResponseCallback mDataCallback, Context context) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<GeocoderApi> call = restClient.callGeoCoderApi(latitude + "," + longitude, Utils.getApiKeyForGeoCoder());
        call.enqueue(new Callback<GeocoderApi>() {
            @Override
            public void onResponse(Call<GeocoderApi> call, Response<GeocoderApi> geocoderApiResponse) {
                String add = StringUtils.EMPTY;
                if (geocoderApiResponse != null && geocoderApiResponse.isSuccessful()) {
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
            public void onFailure(Call<GeocoderApi> call, Throwable t) {
                AppPreferences.setGeoCoderApiKeyRequired(true);
                Utils.redLog("GeoCode", t.getMessage() + "");
            }
        });
    }

    /**
     * Call Geo Code Api via Place Id
     *
     * @param placeId       Place Id
     * @param context       Caller context
     * @param mDataCallback Result callback
     */
    public void callGeoCodeApiWithPlaceId(final String placeId, Context context, final IResponseCallback mDataCallback) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<GeoCodeApiResponse> call = restClient.callGeoCoderApiWithPlaceId(placeId, Utils.getApiKeyForGeoCoder());
        call.enqueue(new Callback<GeoCodeApiResponse>() {
            @Override
            public void onResponse(Call<GeoCodeApiResponse> call, Response<GeoCodeApiResponse> geocoderApiResponse) {
                if (geocoderApiResponse != null && geocoderApiResponse.isSuccessful()
                        && geocoderApiResponse.body() != null
                        && geocoderApiResponse.body().getStatus().equalsIgnoreCase(Constants.STATUS_CODE_OK)
                        && geocoderApiResponse.body().getResults().size() > 0) {
                    mDataCallback.onResponse(geocoderApiResponse.body());
                } else {
                    mDataCallback.onError(0, "" +
                            mContext.getString(R.string.error_try_again) + " ");
                }
            }

            @Override
            public void onFailure(Call<GeoCodeApiResponse> call, Throwable t) {
                mDataCallback.onError(HTTPStatus.INTERNAL_SERVER_ERROR, "" +
                        mContext.getString(R.string.error_try_again) + " ");
            }
        });
    }

    public void getDistanceMatrix(String origin, String destination, final IResponseCallback mDataCallback, Context context) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();

        LatLng originLatLng = Utils.getLatLngFromString(origin);
        LatLng destinationLatLng = Utils.getLatLngFromString(destination);

        Call<BykeaDistanceMatrixResponse> call =
                restClient.callDistanceMatrixApi(ApiTags.BykeaMaps.DISTANCE_MATRIX,
                        originLatLng.latitude, originLatLng.longitude,
                        destinationLatLng.latitude, destinationLatLng.longitude);

        call.enqueue(new GenericRetrofitCallBack<>(mDataCallback));
    }


    public void autocomplete(Context context, String input, final IResponseCallback mDataCallback) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<PlaceAutoCompleteResponse> call = restClient.getAutoCompletePlaces(input, Utils.getCurrentLocation(), Constants.COUNTRY_CODE_AUTOCOMPLETE, "35000",
                AppPreferences.getDriverSettings().getData().getGoogleAutoCompleteApiKey());
        call.enqueue(new Callback<PlaceAutoCompleteResponse>() {
            @Override
            public void onResponse(Call<PlaceAutoCompleteResponse> call, Response<PlaceAutoCompleteResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getStatus().equalsIgnoreCase("OK")) {
                    mDataCallback.onResponse(response.body());
                } else {
                    mDataCallback.onError(0, response.message());
                }
            }

            @Override
            public void onFailure(Call<PlaceAutoCompleteResponse> call, Throwable t) {
                mDataCallback.onError(0, t.toString());
            }
        });
    }


    public void getPlaceDetails(String s, Context context, final IResponseCallback mDataCallback) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<PlaceDetailsResponse> call = restClient.getPlaceDetails(s, Utils.getApiKeyForGeoCoder());
        call.enqueue(new Callback<PlaceDetailsResponse>() {
            @Override
            public void onResponse(Call<PlaceDetailsResponse> call, Response<PlaceDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mDataCallback.onResponse(response.body());
                } else {
                    mDataCallback.onError(0, response.message());
                }
            }

            @Override
            public void onFailure(Call<PlaceDetailsResponse> call, Throwable t) {
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

    /**
     * This method will call Update App Version API
     *
     * @param onResponseCallBack to handle call back
     */
    public void updateAppVersion(final IResponseCallback onResponseCallBack) {
        mRestClient = RestClient.getClient(DriverApp.getContext());
        Call<UpdateAppVersionResponse> restCall = mRestClient.updateAppVersion(
                AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(),
                Double.parseDouble(Utils.getVersion()));
        restCall.enqueue(new GenericRetrofitCallBack<UpdateAppVersionResponse>(onResponseCallBack));
    }

    /**
     * Request remote to cancel booking picked from loadboard
     *
     * @param context         App context
     * @param body            Request body
     * @param userDataHandler Callback
     */
    public void cancelLoadBoardBooking(Context context, LoadBoardBookingCancelRequest body, final IUserDataHandler userDataHandler) {
        mContext = context;
        RestClient.getClient(context).cancelLoadBoardBooking(body).enqueue(
                new Callback<CancelRideResponse>() {
                    @Override
                    public void onResponse(Call<CancelRideResponse> call, Response<CancelRideResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            userDataHandler.onCancelRide(response.body());
                        } else {
                            userDataHandler.onError(0, response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<CancelRideResponse> call, Throwable t) {
                        userDataHandler.onError(0, t.toString());
                    }
                });
    }
}
