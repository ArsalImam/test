package com.bykea.pk.partner.communication.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.google.gson.Gson;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PilotData;
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
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.SettingsResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.bykea.pk.partner.utils.Utils;

import java.io.File;
import java.io.IOException;

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
        Utils.printUrl("UrlLogin", ApiTags.USER_LOGIN_API + "&email=" + email +
                "&password=" + password +
                "&deviceType=" + deviceType +
                "&gcmId=" + regID);
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<LoginResponse> restCall = mRestClient.login(email, password,
                deviceType, userStatus, regID, Utils.getVersion(context),
                AppPreferences.getOneSignalPlayerId(mContext), AppPreferences.getADID(context));
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
                Utils.infoLog("LoginResponse", t.getMessage() + " ");
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    public void sendLogout(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<LogoutResponse> restCall = mRestClient.logout(AppPreferences.getDriverId(context),
                AppPreferences.getAccessToken(context), AppPreferences.getDriverId(context));
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
                Utils.infoLog("Logout Response", t.getMessage() + " ");
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    public void registerUser(Context context, final IResponseCallback onResponseCallBack, PilotData data) {

        this.mResponseCallBack = onResponseCallBack;
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<RegisterResponse> restCall = mRestClient.register(data.getPhoneNo(),
                data.getPincode(), "android", AppPreferences.getRegId(mContext), data.getFullName(), data.getCity().getName(),
                data.getAddress(), data.isTermsAndConditions() + "", data.getPlateNo(), data.getLicenseNo(),
                data.getLicenseExpiry(), data.getVehicleType(), data.getCnic(), data.getEmail(),
                data.getLicenseImage(), data.getLat(), data.getLng(), data.getPilotImage());
        restCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Response<RegisterResponse> response, Retrofit retrofit) {
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
                Utils.infoLog("REGISTER RESPONSE", t.getMessage() + " ");
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
                city, address, email, AppPreferences.getDriverId(context),
                AppPreferences.getAccessToken(context), Constants.USER_TYPE, pincode);
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
                Utils.infoLog("UPDATE PROFILE RESPONSE", t.getMessage() + " ");
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    // This method will get you a verification code
    public void sendPhoneNumberVerificationRequest(Context context, final IResponseCallback onResponseCallBack,
                                                   String phoneNumber, int userStatus) {
        Utils.printUrl("UrlPhoneVerification", ApiTags.PHONE_NUMBER_VERIFICATION_API + "&phone=" + phoneNumber);
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
                Utils.infoLog("Number Verification", t.getMessage() + " ");
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }


    // This method will authenticate the code
    public void sendCodeVerificationRequest(Context context, final IResponseCallback onResponseCallBack,
                                            String code, String phoneNumber) {
        Utils.printUrl("CodeVerification", ApiTags.CODE_VERIFICATION_API + "&code=" + code + "&phone=" + phoneNumber);
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
                Utils.infoLog("LoginResponse", t.getMessage() + " ");
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
                Utils.infoLog("ForgotPassword", t.getMessage() + " ");
                mResponseCallBack.onError(0, getErrorMessage(t));

            }
        });

    }

    public void getTripHistory(Context context, final IResponseCallback onResponseCallBack, String pageNo) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<TripHistoryResponse> restCall = mRestClient.getTripHistory(AppPreferences.getDriverId(context),
                AppPreferences.getAccessToken(context), Constants.USER_TYPE, pageNo);
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
                Utils.infoLog("GET TRIP HISTORY Response", t.getMessage() + " ");
                mResponseCallBack.onError(0, getErrorMessage(t));

            }
        });


    }

    public void getMissedTripHistory(Context context, final IResponseCallback onResponseCallBack, String pageNo) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<TripMissedHistoryResponse> restCall = mRestClient.getMissedTripHistory(AppPreferences.getDriverId(context),
                AppPreferences.getAccessToken(context), Constants.USER_TYPE, pageNo);
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
                Utils.infoLog("GET TRIP HISTORY Response", t.getMessage() + " ");
                mResponseCallBack.onError(0, getErrorMessage(t));
            }
        });


    }

    public void checkRunningTrip(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        this.mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(mContext);
        Call<CheckDriverStatusResponse> restCall = mRestClient.checkRunningTrip(AppPreferences.getDriverId(context),
                AppPreferences.getAccessToken(context));
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
                Utils.infoLog("CHECK RUNNING TRIP Response", t.getMessage() + " ");
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
                Utils.infoLog("Upload Audio File", t.getMessage() + " ");
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
                Utils.infoLog("UploadDocumentFile", t.getMessage() + " ");
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
                Utils.infoLog("GET SERVICE TYPES", t.getMessage() + " ");
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
                Utils.infoLog("GET SERVICE TYPES", t.getMessage() + " ");
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
                Utils.infoLog("GET SERVICE TYPES", t.getMessage() + " ");
            }
        });
    }

    public void getSettings(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mResponseCallBack = onResponseCallBack;
        mRestClient = RestClient.getClient(context);
        Call<SettingsResponse> requestCall = mRestClient.getSettings("d", AppPreferences.getPilotData(mContext).getCity().getName());
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
        Call<ChangePinResponse> requestCall = mRestClient.requestChangePin(AppPreferences.getDriverId(mContext), AppPreferences.getAccessToken(mContext),
                newPin, oldPin, "d");
        requestCall.enqueue(new GenericRetrofitCallBack<ChangePinResponse>(onResponseCallBack));
    }

    public void getProfileData(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<GetProfileResponse> requestCall = mRestClient.requestProfileData(AppPreferences.getDriverId(mContext), AppPreferences.getAccessToken(mContext), "d");
        requestCall.enqueue(new GenericRetrofitCallBack<GetProfileResponse>(onResponseCallBack));
    }

    public void getCities(Context context, final IResponseCallback onResponseCallBack) {
        mContext = context;
        mRestClient = RestClient.getClient(mContext);
        Call<GetCitiesResponse> requestCall = mRestClient.getCities();
        requestCall.enqueue(new GenericRetrofitCallBack<GetCitiesResponse>(onResponseCallBack));
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


}
