package com.bykea.pk.partner.communication.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.utils.ApiTags;
import com.google.gson.Gson;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.response.AccountNumbersResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.GetProfileResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.SettingsResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;
import com.squareup.okhttp.internal.Util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class RestRequestHandler {

    private Context mContext;
    private IRestClient mRestClient;
    private IResponseCallback mResponseCallBack;


    public void sendUserLogin(Context context, final IResponseCallback onResponseCallBack, String email, String password,
                              String deviceType, String userStatus, String regID) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Utils.redLog("IMEI NUMBER", Utils.getDeviceId(context));
        Call<LoginResponse> restCall = mRestClient.login(email, password,
                deviceType, userStatus, regID, "" + AppPreferences.getLatitude(), "" + AppPreferences.getLongitude(), Utils.getVersion(context),
                AppPreferences.getOneSignalPlayerId(), AppPreferences.getADID(), Utils.getDeviceId(context));
        restCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Response<LoginResponse> response, Retrofit retrofit) {
                // Got success from server
                if (response.isSuccess()) {
                    if (null != mResponseCallBack) {
                        mResponseCallBack.onResponse(response.body());
                    }
                } else {
                    mResponseCallBack.onError(0, mContext.getString(R.string.error_try_again));
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
    public void uploadAudioFile(Context context, IResponseCallback responseCallBack, File file) {
        mContext = context;
        mResponseCallBack = responseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<UploadAudioFile> requestCall = mRestClient.uploadAudioFile(Utils.convertFileToRequestBody(file));
        requestCall.enqueue(new Callback<UploadAudioFile>() {
            @Override
            public void onResponse(Response<UploadAudioFile> response, Retrofit retrofit) {
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
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });

    }

    //THIS METHOD IS UPLOAD DRIVER DOCUMENT FILE
    public void uplaodDriverDocument(Context context, final IResponseCallback onResponseCallback, File file) {
        mContext = context;
        mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);
        Call<UploadDocumentFile> requestCall = mRestClient.uploadDocumentFile(Utils.convertFileToRequestBody(file));
        requestCall.enqueue(new Callback<UploadDocumentFile>() {
            @Override
            public void onResponse(Response<UploadDocumentFile> response, Retrofit retrofit) {
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
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });
    }

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

    //THIS METHOD IS UPLOAD DRIVER DOCUMENT FILE
    public void updatePilotStatus(Context context, final IResponseCallback onResponseCallback,
                                  String driverId, boolean status) {
        mContext = context;
        mResponseCallBack = onResponseCallback;
        mRestClient = RestClient.getClient(mContext);
        Call<PilotStatusResponse> requestCall = mRestClient.updateStatus(driverId, status + "");
        requestCall.enqueue(new Callback<PilotStatusResponse>() {
            @Override
            public void onResponse(Response<PilotStatusResponse> response, Retrofit retrofit) {
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
                mResponseCallBack.onError(0, getErrorMessage(t));
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

    public void getSettings(Context context, final IResponseCallback onResponseCallBack) {
        if (!AppPreferences.isLoggedIn() || AppPreferences.getPilotData() == null
                || AppPreferences.getPilotData().getCity() == null) {
            return;
        }
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<SettingsResponse> requestCall = mRestClient.getSettings("d",
                AppPreferences.getPilotData().getCity().getName(), AppPreferences.getSettingsVersion());
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
//        restCall.enqueue(new GenericRetrofitCallBack<WalletHistoryResponse>(onResponseCallBack));
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

    public void getAccountNumbers(Context context, final IResponseCallback onResponseCallBack,
                                  String driverId, String accessToken, String pageNo) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<AccountNumbersResponse> restCall = mRestClient.getAccountNumbers(driverId,
                accessToken, "d", pageNo);
//        restCall.enqueue(new GenericRetrofitCallBack<WalletHistoryResponse>(onResponseCallBack));
        restCall.enqueue(new Callback<AccountNumbersResponse>() {
            @Override
            public void onResponse(Response<AccountNumbersResponse> response, Retrofit retrofit) {
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

    public void getContactNumbers(Context context, final IResponseCallback onResponseCallBack,
                                  String driverId, String accessToken) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<ContactNumbersResponse> restCall = mRestClient.getContactNumbers(driverId,
                accessToken, "d");
//        restCall.enqueue(new GenericRetrofitCallBack<WalletHistoryResponse>(onResponseCallBack));
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
        requestCall.enqueue(new GenericRetrofitCallBack<ChangePinResponse>(onResponseCallBack));
    }

    public void getProfileData(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<GetProfileResponse> requestCall = mRestClient.requestProfileData(AppPreferences.getDriverId(), AppPreferences.getAccessToken(), "d");
        requestCall.enqueue(new GenericRetrofitCallBack<GetProfileResponse>(onResponseCallBack));
    }

    public void getCities(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<GetCitiesResponse> requestCall = mRestClient.getCities();
        requestCall.enqueue(new GenericRetrofitCallBack<GetCitiesResponse>(onResponseCallBack));
    }

    public synchronized void requestHeatMap(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
//        mRestClient = RestClient.getBykea2ApiClient(mContext);
        String url = ApiTags.HEAT_MAP_2.replace("CITY_NAME", StringUtils.capitalize(AppPreferences.getPilotData().getCity().getName()));
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


    private class GenericRetrofitCallBack<T extends CommonResponse> implements Callback<T> {
        private IResponseCallback mCallBack;

        public GenericRetrofitCallBack(IResponseCallback callBack) {
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

    public void requestDriverDropOff(Context context, IResponseCallback onResponseCallBack,
                                     String lat, String lng, String address) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<DriverDestResponse> requestCall = mRestClient.setDriverDroppOff(AppPreferences.getDriverId()
                , AppPreferences.getAccessToken(), lat, lng, address);
        requestCall.enqueue(new GenericRetrofitCallBack<DriverDestResponse>(onResponseCallBack));
    }

    @NonNull
    private String getErrorMessage(Throwable error) {
        String errorMsg;
        if (error instanceof IOException) {
            Utils.redLog("Retrofit Error", "TimeOut " + String.valueOf(error.getCause()));
            errorMsg = mContext.getString(R.string.internet_error);
        } else if (error instanceof IllegalStateException) {
            Utils.redLog("Retrofit Error", "ConversionError " + String.valueOf(error.getCause()));
            errorMsg = mContext.getString(R.string.error_try_again);
        } else {
            Utils.redLog("Retrofit Error", "Other Error " + String.valueOf(error.getLocalizedMessage()));
            errorMsg = mContext.getString(R.string.error_try_again);
        }
        return errorMsg;
    }

    public void postProblem(Context context, String selectedReason, String tripId,
                            String email, String details, IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<ProblemPostResponse> restCall = mRestClient.postProblem(AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(),
                selectedReason,
                tripId,
                email,
                AppPreferences.getPilotData().getFullName(),
                AppPreferences.getPilotData().getPhoneNo(),
                details,
                "d");
        restCall.enqueue(new GenericRetrofitCallBack<ProblemPostResponse>(onResponseCallBack));

    }


    public void callGeoCoderApi(final String latitude, final String longitude,
                                final IResponseCallback mDataCallback, Context context) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<GeocoderApi> call = restClient.callGeoCoderApi(latitude + "," + longitude, Constants.GOOGLE_PLACE_SERVER_API_KEY);
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
                            AppPreferences.setApiKeyRequired(true);
                            mDataCallback.onError(0, "No Address Found");
                        }
                    } else {
                        AppPreferences.setApiKeyRequired(true);
                    }
                } else {
                    AppPreferences.setApiKeyRequired(true);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AppPreferences.setApiKeyRequired(true);
                Utils.redLog("GeoCode", t.getMessage() + "");
            }
        });
    }

    public void callGeoCoderApi2(final String latitude, final String longitude, final IResponseCallback mDataCallback, Context context) {
        mContext = context;
        IRestClient restClient = RestClient.getGooglePlaceApiClient();
        Call<GeocoderApi> call = restClient.callGeoCoderApi(latitude + "," + longitude, Utils.getApiKey());
        call.enqueue(new Callback<GeocoderApi>() {
            @Override
            public void onResponse(Response<GeocoderApi> geocoderApiResponse, Retrofit retrofit) {
                if (geocoderApiResponse != null && geocoderApiResponse.isSuccess()) {
                    if (geocoderApiResponse.body() != null
                            && geocoderApiResponse.body().getStatus().equalsIgnoreCase(Constants.STATUS_CODE_OK)
                            && geocoderApiResponse.body().getResults().length > 0) {
                        String cityName = StringUtils.EMPTY;
                        GeocoderApi.Address_components[] address_componentses = geocoderApiResponse.body().getResults()[0].getAddress_components();
                        for (GeocoderApi.Address_components addressComponent : address_componentses) {
                            String[] types = addressComponent.getTypes();
                            for (String type : types) {

                                if (type.equalsIgnoreCase(Constants.GEOCODE_RESULT_TYPE_CITY)) {
                                    cityName = addressComponent.getLong_name();
                                }
                                if (StringUtils.isNotBlank(cityName)) {
                                    break;
                                }
                            }
                            if (StringUtils.isNotBlank(cityName)) {
                                break;
                            }
                        }
                        mDataCallback.onResponse("Current city: " + cityName);
                    } else {
                        AppPreferences.setApiKeyRequired(true);
                    }
                } else {
                    AppPreferences.setApiKeyRequired(true);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                AppPreferences.setApiKeyRequired(true);
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
                if (response.isSuccess()) {
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

}
