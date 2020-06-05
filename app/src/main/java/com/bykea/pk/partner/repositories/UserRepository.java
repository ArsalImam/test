package com.bykea.pk.partner.repositories;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.communication.rest.RestRequestHandler;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.dal.LocCoordinatesInTrip;
import com.bykea.pk.partner.dal.source.remote.response.BookingListingResponse;
import com.bykea.pk.partner.models.data.DirectionDropOffData;
import com.bykea.pk.partner.models.data.MultiDeliveryCallDriverData;
import com.bykea.pk.partner.models.data.MultipleDeliveryRemainingETA;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.data.RankingResponse;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.models.data.SignUpAddNumberResponse;
import com.bykea.pk.partner.models.data.SignUpCompleteResponse;
import com.bykea.pk.partner.models.data.SignUpOptionalDataResponse;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.models.data.SignupUplodaImgResponse;
import com.bykea.pk.partner.models.data.TrackingData;
import com.bykea.pk.partner.models.data.ZoneData;
import com.bykea.pk.partner.models.request.DriverAvailabilityRequest;
import com.bykea.pk.partner.models.request.DriverLocationRequest;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.AcceptLoadboardBookingResponse;
import com.bykea.pk.partner.models.response.AckCallResponse;
import com.bykea.pk.partner.models.response.AddSavedPlaceResponse;
import com.bykea.pk.partner.models.response.ArrivedResponse;
import com.bykea.pk.partner.models.response.BankAccountListResponse;
import com.bykea.pk.partner.models.response.BankDetailsResponse;
import com.bykea.pk.partner.models.response.BeginRideResponse;
import com.bykea.pk.partner.models.response.BiometricApiResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.ConversationChatResponse;
import com.bykea.pk.partner.models.response.ConversationResponse;
import com.bykea.pk.partner.models.response.DownloadAudioFileResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.DriverPerformanceResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.models.response.DriverVerifiedBookingResponse;
import com.bykea.pk.partner.models.response.EndRideResponse;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.models.response.FreeDriverResponse;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.GetConversationIdResponse;
import com.bykea.pk.partner.models.response.GetProfileResponse;
import com.bykea.pk.partner.models.response.GetSavedPlacesResponse;
import com.bykea.pk.partner.models.response.GetZonesResponse;
import com.bykea.pk.partner.models.response.GoogleDistanceMatrixApi;
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.models.response.LoadBoardResponse;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryAcceptCallResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryCallDriverAcknowledgeResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryCancelBatchResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryCompleteRideResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryDriverArrivedResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryDriverStartedResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryFeedbackResponse;
import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse;
import com.bykea.pk.partner.models.response.MultipleDeliveryDropOff;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.models.response.SendMessageResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.SettingsResponse;
import com.bykea.pk.partner.models.response.ShahkarResponse;
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateAppVersionResponse;
import com.bykea.pk.partner.models.response.UpdateConversationStatusResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UpdateRegIDResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.models.response.UploadImageFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.models.response.ZoneAreaResponse;
import com.bykea.pk.partner.repositories.places.PlacesDataHandler;
import com.bykea.pk.partner.repositories.places.PlacesRepository;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository {

    private static final String TAG = UserRepository.class.getSimpleName();
    private Context mContext;
    private IUserDataHandler mUserCallback;
    private WebIORequestHandler mWebIORequestHandler;
    private RestRequestHandler mRestRequestHandler;

    private DirectionDropOffData directionDropOffData;

    public UserRepository() {
        mWebIORequestHandler = WebIORequestHandler.getInstance();
        mRestRequestHandler = new RestRequestHandler();
    }

    /***
     * Send Request to API server for Driver login, which send sms
     *
     * @param context Calling context.
     * @param handler API response callback.
     * @param phoneNumber Driver phone number,
     * @param latitude Driver current latitude.
     * @param longitude Driver current longitude.
     * @param otpReceiveType How OTP would be received by user. i.e. (Call/SMS)
     */
    public void requestDriverLogin(Context context,
                                   IUserDataHandler handler,
                                   String phoneNumber,
                                   double latitude,
                                   double longitude,
                                   String otpReceiveType) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.sendDriverLogin(context, mDataCallback,
                phoneNumber, Constants.DEVICE_TYPE, latitude, longitude, otpReceiveType);
    }

    /***
     * Sending Login request to API Server
     * @param context Calling context.
     * @param handler API response Handler
     * @param phoneNumber Driver phone number
     * @param otpCode Entered OTP COde
     */
    public void requestUserLogin(Context context,
                                 IUserDataHandler handler,
                                 String phoneNumber,
                                 String otpCode) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.sendUserLogin(context, mDataCallback, phoneNumber, otpCode,
                Constants.DEVICE_TYPE, AppPreferences.getRegId());

    }

    /***
     * Update driver location with latest latitude and longitude
     * @param context calling context
     * @param handler Response handler
     * @param locationRequest Driver location request model
     */
    public void updateDriverLocation(Context context, IUserDataHandler handler, DriverLocationRequest locationRequest) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.sendDriverLocationUpdate(context, mDataCallback, locationRequest);

    }

    public void requestPilotLogout(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.sendLogout(context, mDataCallback);

    }

    public void requestDriverDropOff(Context context, IUserDataHandler handler, String lat, String lng, String address) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestDriverDropOff(context, mDataCallback, lat, lng, address);
    }


    public void requestUpdateProfile(Context context, IUserDataHandler handler,
                                     String fullName, String city, String address, String email, String pincode) {
        mContext = context;
        mUserCallback = handler;

        mRestRequestHandler.updateProfile(context, mDataCallback, fullName, city, address, email, pincode);
    }

    public void requestForgotPassword(Context context, IUserDataHandler handler, String email) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.forgotPassword(context, mDataCallback, email);
    }

    public void requestRunningTrip(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.checkRunningTrip(mContext, mDataCallback);
    }


    /**
     * USE WHEN YOU WANT TO DISMISS WHEN THE SUCCESSFUL DATA IS RETRIEVE FOR THE ACTIVE TRIP
     *
     * @param context : Calling Activity
     * @param handler : Override in Calling Acitivity
     */
    public void getActiveTrip(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.checkActiveTrip(mContext, mDataCallback);
    }

    /**
     * this method can be used to get all trips history from driver id
     * this is a legacy function and is replaced with {@link #requestBookingListing(Context, IUserDataHandler, String, String)}
     * and will be removed in the future release
     *
     * @param context       component which requires data
     * @param handler       callback to receive data on task completed
     * @param pageNo        param needs to send for pagination
     * @param tripHistoryId (optional) if any specific trip details needed
     */
    @Deprecated
    public void requestTripHistory(Context context, IUserDataHandler handler, String pageNo, String tripHistoryId) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getTripHistory(mContext, mDataCallback, pageNo, tripHistoryId);
    }

    /**
     * this method can be used to get all booking listing by driver id from kronos
     *
     * @param context context component which requires data
     * @param handler onResponseCallBack callback to receive data on task completed
     * @param pageNo  param needs to send for pagination
     * @param limit   number of records per page
     */
    public void requestBookingListing(Context context, IUserDataHandler handler, String pageNo, String limit) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestBookingListing(mContext, mDataCallback, pageNo, limit);
    }

    public void requestMissedTripHistory(Context context, IUserDataHandler handler, String pageNo) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getMissedTripHistory(mContext, mDataCallback, pageNo);
    }
/*

    public void requestUploadFile(Context context, IUserDataHandler handler, File file) {
        if (Connectivity.isConnectedFast(context)) {
            mContext = context;
            mUserCallback = handler;
            mRestRequestHandler.uplaodDriverDocument(context, mDataCallback, file);
        }
    }
*/

    public void uploadAudioFile(Context context, IUserDataHandler handler, File file) {
        if (Connectivity.isConnectedFast(context)) {
            mContext = context;
            mUserCallback = handler;
            mRestRequestHandler.uploadAudioFile(mContext, mDataCallback, file);
        }

    }

    public void uploadImageFile(Context context, IUserDataHandler handler, File file) {
        if (Connectivity.isConnectedFast(context)) {
            mContext = context;
            mUserCallback = handler;
            mRestRequestHandler.uploadImageFile(mContext, mDataCallback, file);
        }

    }

    public void requestGetServiceTypes(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getServiceTypes(context, mDataCallback);
    }

    public void requestPhoneNumberVerification(Context context, IUserDataHandler handler,
                                               String phoneNumber) {
        mContext = context;
        mUserCallback = handler;
        // 1 is for rider change it to 2 for driver
        mRestRequestHandler.sendPhoneNumberVerificationRequest(context, mDataCallback, phoneNumber, 1);
    }

    public void requestCodeAuthentication(Context context, IUserDataHandler handler, String code,
                                          String phone) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.sendCodeVerificationRequest(context, mDataCallback, code,
                phone);
    }

    public void getCities(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getCities(mContext, mDataCallback);
    }

    /*public void requestHeatMapData(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("_id", AppPreferences.getDriverId());
            if (AppPreferences.getPilotData() != null) {
                if (StringUtils.isNotBlank(AppPreferences.getPilotData().getService_type())) {
                    jsonObject.put("service_type", AppPreferences.getPilotData().getService_type());
                }
                if (AppPreferences.getPilotData().getCity() != null &&
                        StringUtils.isNotBlank(AppPreferences.getPilotData().getCity().getId()))
                    jsonObject.put("city", AppPreferences.getPilotData().getCity().getId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.requestHeatmap(jsonObject, mDataCallback);
    }*/
    public void requestHeatMapData(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestHeatMap(context, mDataCallback);
    }

    /***
     * Send driver Location update request to our API server.
     *
     * @param context Calling context.
     * @param handler Response handler
     * @param lat current driver latitude.
     * @param lon current driver longitude.
     */
    public void requestLocationUpdate(Context context, IUserDataHandler handler,
                                      double lat, double lon) {

        mContext = context;
        mUserCallback = handler;
        String driverId = AppPreferences.getDriverId();
        if (StringUtils.isBlank(driverId)) {
            return;
        }

        DriverLocationRequest locationRequest = new DriverLocationRequest();
        locationRequest.setTokenID(AppPreferences.getAccessToken());
        locationRequest.setDriverID(driverId);
        locationRequest.setLatitude(lat + "");
        locationRequest.setLongitude(lon + "");
        String tripStatus = StringUtils.EMPTY;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lat", lat);
            jsonObject.put("lng", lon);
            Utils.addDriverDestinationProperty(jsonObject);
            Utils.logEvent(context, AppPreferences.getDriverId(), Constants.AnalyticsEvents.ON_PARTNER_LOCATION_UPDATE, jsonObject);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (AppPreferences.isOnTrip()) {
            if (AppPreferences.getDeliveryType().equalsIgnoreCase(Constants.CallType.SINGLE)) {
                tripStatus = AppPreferences.getCallData() != null
                        && StringUtils.isNotBlank(AppPreferences.getCallData().getStatus())
                        ? AppPreferences.getCallData().getStatus() : StringUtils.EMPTY;
                setupLocationRequestUpdate(lat, lon, locationRequest, tripStatus);
                mRestRequestHandler.sendDriverLocationUpdate(mContext,
                        mDataCallback, locationRequest);
            } else {
                //In Batch Trip
                setupLocationRequestUpdate(lat, lon, locationRequest, tripStatus);
                if (AppPreferences.isMultiDeliveryJobActivityOnForeground()) {
                    mRestRequestHandler.sendDriverLocationUpdate(mContext,
                            mDataCallback, locationRequest);
                } else if (!AppPreferences.isMultiDeliveryJobActivityOnForeground() && AppPreferences.isMultiDeliveryDistanceMatrixCalledRequired()) {
                    AppPreferences.setMultiDeliveryDistanceMatrixCalledRequired(false);
                    Log.v(TAG, "Distance Matrix - Multidelivery");
                    calculateDistanceFromDirectionAPI(locationRequest);
                }
            }
        } else {
            if (StringUtils.isBlank(tripStatus)) {
                tripStatus = AppPreferences.getTripStatus();
            }
            locationRequest.setAvailableStatus(tripStatus);
            locationRequest.setUuid(UUID.randomUUID().toString());
            mRestRequestHandler.sendDriverLocationUpdate(mContext,
                    mDataCallback, locationRequest);
        }
    }

    /**
     * Prepare data for location update when partner's on trip.
     *
     * @param lat             current lat
     * @param lon             current lng
     * @param locationRequest request data object
     * @param tripStatus      current status of running trip
     */
    private void setupLocationRequestUpdate(double lat, double lon, DriverLocationRequest locationRequest, String tripStatus) {
        locationRequest.setEta(AppPreferences.getEta());
        locationRequest.setDistance(AppPreferences.getEstimatedDistance());
        if (AppPreferences.getCallData() != null && AppPreferences.getCallData().getTripId() != null)
            locationRequest.setTripID(AppPreferences.getCallData().getTripId());
        ArrayList<TrackingData> trackingData = AppPreferences.getTrackingData();
        if (trackingData.size() == 0) {
            TrackingData data = new TrackingData();
            data.setLat(lat + "");
            data.setLng(lon + "");
            trackingData.add(data);
        }
        locationRequest.setTrackingData(trackingData);
        AppPreferences.clearTrackingData();
        if (StringUtils.isBlank(tripStatus)) {
            tripStatus = AppPreferences.getTripStatus();
        }
        locationRequest.setAvailableStatus(tripStatus);
        locationRequest.setUuid(UUID.randomUUID().toString());
    }

    /**
     * Fetch Tracking Data List
     *
     * <p>Calculate the distance & duration between driver location to each drop off location</p>
     *
     * @param locationRequest The {@linkplain DriverLocationRequest} object.
     */
    private synchronized void calculateDistanceFromDirectionAPI(final DriverLocationRequest
                                                                        locationRequest) {
        String driverLatLng =
                AppPreferences.getLatitude() + "," + AppPreferences.getLongitude();
        String dropLatLng = StringUtils.EMPTY;
        final MultiDeliveryCallDriverData callDriverData = AppPreferences.
                getMultiDeliveryCallDriverData();

        //TODO temp fix until we figure it out how to handle tip notification socket for single trips.
        if (callDriverData == null) {
            return;
        }

        final ArrayList<MultipleDeliveryRemainingETA> trackingDataList = new ArrayList<>();
        final List<MultipleDeliveryBookingResponse> bookingResponseList =
                callDriverData.getBookings();

        final int bookingSize = bookingResponseList.size();
        final int[] counter = {0};
        for (final MultipleDeliveryBookingResponse bookingResponse : bookingResponseList) {
            counter[0]++;
            MultipleDeliveryDropOff dropOff = bookingResponse.getDropOff();
            dropLatLng = dropLatLng.concat(dropOff.getLat() + "," +
                    dropOff.getLng());
            Log.d(TAG, bookingResponse.getTrip().getId());
            if (counter[0] != bookingSize)
                dropLatLng = dropLatLng.concat("|");
        }

        new PlacesRepository().getDistanceMatrix(
                driverLatLng,
                dropLatLng,
                mContext,
                new PlacesDataHandler() {
                    @Override
                    public void onDistanceMatrixResponse(GoogleDistanceMatrixApi response) {
                        if (response != null && response.getRows() != null && response.getRows().length > 0) {
                            counter[0] = 0;
                            GoogleDistanceMatrixApi.Elements[] elements = response.getRows()[0].getElements();
                            for (GoogleDistanceMatrixApi.Elements element : elements) {
                                String tripID = bookingResponseList.get(counter[0]).getTrip().getId();
                                Log.d(TAG, tripID);
                                int distance = element.getDistance().getValueInt();
                                int duration = element.getDuration().getValueInt();
                                MultipleDeliveryRemainingETA remainingETA = new MultipleDeliveryRemainingETA();
                                remainingETA.setTripID(tripID);
                                remainingETA.setRemainingDistance(distance);
                                remainingETA.setRemainingTime(duration);
                                trackingDataList.add(remainingETA);
                                counter[0]++;
                            }
                            locationRequest.setBatchBookings(trackingDataList);
                            mRestRequestHandler.sendDriverLocationUpdate(mContext,
                                    mDataCallback, locationRequest);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.d(TAG, error);
                    }
                });

    }


    public void freeDriverStatus(Context context, IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("lat", AppPreferences.getLatitude() + "");
            jsonObject.put("lng", AppPreferences.getLongitude() + "");
            jsonObject.put("trip_id", AppPreferences.getCallData().getTripId());
            jsonObject.put("pid", AppPreferences.getCallData().getPassId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.requestFreePilot(jsonObject, mDataCallback);
    }


    public void requestAcceptCall(Context context, String acceptedSecond, IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("_id", AppPreferences.getDriverId());
//            jsonObject.put("did", AppPreferences.getDriverId(context));
            jsonObject.put("tid", AppPreferences.getCallData().getTripId());
            jsonObject.put("pid", AppPreferences.getCallData().getPassId());
            jsonObject.put("lat", AppPreferences.getLatitude() + "");
            jsonObject.put("lng", AppPreferences.getLongitude() + "");
            jsonObject.put("accept_seconds", acceptedSecond + "");

            jsonObject.put("battery", Utils.getBatteryPercentage(context) + "");
            jsonObject.put("memory", Utils.getTotalRAM(context) + "");
            jsonObject.put("os", Utils.getAndroidVersion() + "");
            jsonObject.put("connection_status", Utils.getSignalStrength(context) + "");
            jsonObject.put("imei", Utils.getDeviceId(context) + "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.acceptNormalCall(jsonObject, mDataCallback);

    }

    public void ackCall(Context context, IUserDataHandler handler) {
        String tripID = AppPreferences.getCallData().getTripId();
        if (tripID.equalsIgnoreCase(AppPreferences.getLastAckTripID())) {
            return;
        }
        AppPreferences.setLastAckTripID(tripID);
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("trip_id", tripID);
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("pass_id", AppPreferences.getCallData().getPassId());
            //            jsonObject.put("event_id", AppPreferences.getCallData(context).getEvent_id());
            jsonObject.put("full_name", AppPreferences.getPilotData().getFullName());
            jsonObject.put("delay", AppPreferences.getTripDelay());
            jsonObject.put("pass_socket_id", AppPreferences.getCallData().getPass_socket_id());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.ackCall(jsonObject, mDataCallback);

    }

    public void requestRejectCall(Context context, IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("driver_id", AppPreferences.getDriverId());
            jsonObject.put("did", AppPreferences.getDriverId());
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("trips_id", AppPreferences.getCallData().getTripId());
            jsonObject.put("passenger_id", AppPreferences.getCallData().getPassId());
            jsonObject.put("pid", AppPreferences.getCallData().getPassId());
            jsonObject.put("lat", AppPreferences.getLatitude());
            jsonObject.put("lng", AppPreferences.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.rejectNormalCall(jsonObject, mDataCallback);

    }

    /**
     * Request to cancel driver booking either to goto socket or to REST server
     *
     * @param context   App context
     * @param handler   Callback
     * @param reasonMsg Reason to cancel
     */
    public void requestCancelRide(Context context, IUserDataHandler handler, String reasonMsg) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("driver_id", AppPreferences.getDriverId());
            jsonObject.put("message", reasonMsg);
            jsonObject.put("trips_id", AppPreferences.getCallData().getTripId());
            jsonObject.put("tid", AppPreferences.getCallData().getTripId());
            jsonObject.put("passenger_id", AppPreferences.getCallData().getPassId());
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("lat", AppPreferences.getLatitude() + "");
            jsonObject.put("lng", AppPreferences.getLongitude() + "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.cancelRide(jsonObject, mDataCallback);
    }


    public void requestArrived(Context context, IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("did", AppPreferences.getDriverId());
            jsonObject.put("pid", AppPreferences.getCallData().getPassId());
            jsonObject.put("trip_id", AppPreferences.getCallData().getTripId());
            jsonObject.put("tid", AppPreferences.getCallData().getTripId());
            String lat = AppPreferences.getLatitude() + "";
            String lng = AppPreferences.getLongitude() + "";
            jsonObject.put("lat", lat);
            jsonObject.put("lng", lng);

            LocCoordinatesInTrip arrivedLatLng = new LocCoordinatesInTrip();
            arrivedLatLng.setLat(lat);
            arrivedLatLng.setLng(lng);
            arrivedLatLng.setDate(Utils.getIsoDate());
            ArrayList<LocCoordinatesInTrip> prevLatLngList = AppPreferences.getLocCoordinatesInTrip();
            prevLatLngList.add(arrivedLatLng);
            jsonObject.put("routes", new Gson().toJson(prevLatLngList));

            AppPreferences.clearTripDistanceData();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.driverArrived(jsonObject, mDataCallback);

    }


    public void requestBeginRide(Context context, IUserDataHandler handler, String endLat,
                                 String endLng, String endAddress) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            NormalCallData callData = AppPreferences.getCallData();
            String startLat = AppPreferences.getLatitude() + "";
            String startLng = AppPreferences.getLongitude() + "";
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("tid", callData.getTripId());
            jsonObject.put("trip_id", callData.getTripId());
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("did", AppPreferences.getDriverId());
            jsonObject.put("startlatitude", startLat);
            jsonObject.put("startlongitude", startLng);
            jsonObject.put("start_address", callData.getStartAddress());
            jsonObject.put("pid", callData.getPassId());
            jsonObject.put("endlatitude", endLat);
            jsonObject.put("endlongitude", endLng);
            jsonObject.put("end_address", endAddress);

            //To update start latlng on App Side.
            callData.setStartLat(startLat);
            callData.setStartLng(startLng);
            AppPreferences.setCallData(callData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.beginRide(jsonObject, mDataCallback);

    }

    public void requestEndRide(Context context, String endAddress, IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        /*int totalTime = 0;
        long diff = System.currentTimeMillis() - AppPreferences.getStartTripTime(mContext);
        if (diff > 0) {
            totalTime = (int) (diff / (1000));
        }*/
        try {
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("tid", AppPreferences.getCallData().getTripId());
            jsonObject.put("token_id", AppPreferences.getAccessToken());

            if (StringUtils.isNotEmpty(endAddress)) {
                jsonObject.put("address", endAddress);
            }

            String endLatString = AppPreferences.getLatitude() + "";
            String endLngString = AppPreferences.getLongitude() + "";
            String lastLat = AppPreferences.getPrevDistanceLatitude();
            String lastLng = AppPreferences.getPrevDistanceLongitude();
            if (!lastLat.equalsIgnoreCase("0.0") && !lastLng.equalsIgnoreCase("0.0")) {
                if (!Utils.isValidLocation(Double.parseDouble(endLatString), Double.parseDouble(endLngString), Double.parseDouble(lastLat), Double.parseDouble(lastLng))) {
                    endLatString = lastLat;
                    endLngString = lastLng;
                }
            }

            jsonObject.put("lat", endLatString);
            jsonObject.put("lng", endLngString);

            LocCoordinatesInTrip startLatLng = new LocCoordinatesInTrip();
            startLatLng.setLat(AppPreferences.getCallData().getStartLat());
            startLatLng.setLng(AppPreferences.getCallData().getStartLng());
            startLatLng.setDate(Utils.getIsoDate(AppPreferences.getStartTripTime()));

            LocCoordinatesInTrip endLatLng = new LocCoordinatesInTrip();
            endLatLng.setLat(endLatString);
            endLatLng.setLng(endLngString);
            endLatLng.setDate(Utils.getIsoDate());
            ArrayList<LocCoordinatesInTrip> prevLatLngList = AppPreferences.getLocCoordinatesInTrip();
            ArrayList<LocCoordinatesInTrip> latLngList = new ArrayList<>();
            latLngList.add(startLatLng);
            if (prevLatLngList != null && prevLatLngList.size() > 0) {
                latLngList.addAll(prevLatLngList);
            }
            latLngList.add(endLatLng);
            jsonObject.put("routes", new Gson().toJson(latLngList));


        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.endRide(jsonObject, mDataCallback);

    }


    private void setFeedBackCommonData(String feedback, String rate, String amount, JSONObject jsonObject) throws JSONException {
        jsonObject.put("token_id", AppPreferences.getAccessToken());
        jsonObject.put("tid", AppPreferences.getCallData().getTripId());
//        jsonObject.put("trip_id", AppPreferences.getCallData().getTripId());
//        jsonObject.put("trips_id", AppPreferences.getCallData().getTripId());
        jsonObject.put("_id", AppPreferences.getDriverId());
//        jsonObject.put("did", AppPreferences.getDriverId());
//        jsonObject.put("driver_id", AppPreferences.getDriverId());
        jsonObject.put("pid", AppPreferences.getCallData().getPassId());
//        jsonObject.put("passenger_id", AppPreferences.getCallData().getPassId());
        jsonObject.put("received_amount", amount);
        jsonObject.put("rate", rate);
        jsonObject.put("feedback", feedback);
        jsonObject.put("is_dispatch", AppPreferences.getCallData().isDispatcher());
//        if (StringUtils.isNotBlank(AppPreferences.getCallData().getCreator_type())) {
//            jsonObject.put("creator_type", AppPreferences.getCallData().getCreator_type());
//        } else {
//            jsonObject.put("creator_type", "UnKnown");
//        }
        jsonObject.put("lat", AppPreferences.getLatitude() + "");
        jsonObject.put("lng", AppPreferences.getLongitude() + "");
    }

    public void requestFeedback(Context context, IUserDataHandler handler, String feedback,
                                String rate, String amount, String purcAmount) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            setFeedBackCommonData(feedback, rate, amount, jsonObject);
            if (StringUtils.isNotBlank(purcAmount)) {
                jsonObject.put("purcAmount", purcAmount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.feedback(jsonObject, mDataCallback);

    }

    public void requestFeedback(Context context, IUserDataHandler handler, String feedback,
                                String rate, String amount,
                                boolean dStatus, String dMsg, String recName, String recPh) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            setFeedBackCommonData(feedback, rate, amount, jsonObject);
            jsonObject.put("dStatus", dStatus);
            if (StringUtils.isNotBlank(dMsg)) {
                jsonObject.put("dMsg", dMsg);
            }
            if (StringUtils.isNotBlank(recName)) {
                jsonObject.put("recName", recName);
            }
            if (StringUtils.isNotBlank(recPh)) {
                jsonObject.put("recPh", recPh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.feedback(jsonObject, mDataCallback);

    }

    public void requestFeedback(Context context, IUserDataHandler handler, String feedback,
                                String rate, String amount) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            setFeedBackCommonData(feedback, rate, amount, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.feedback(jsonObject, mDataCallback);

    }

    //region Multi Delivery emit data

    /**
     * Set Driver Acknowledge Data in Json Object.
     *
     * @param jsonObject The json object.
     * @throws JSONException if something went wrong with json object it will throw an exception.
     */
    private void setMultiDeliveryData(JSONObject jsonObject) throws JSONException {
        jsonObject.put("batch_id", AppPreferences
                .getMultiDeliveryCallDriverData()
                .getBatchID());
        jsonObject.put("_id", AppPreferences.getDriverId());
        jsonObject.put("token_id", AppPreferences.getAccessToken());
        jsonObject.put("trip_type", Constants.TripTypes.BATCH_TYPE);
        jsonObject.put("lat", AppPreferences.getLatitude());
        jsonObject.put("lng", AppPreferences.getLongitude());
    }

    /**
     * Emit request Driver Acknowledge Response.
     *
     * @param handler The Callback that will be invoked when response received.
     * @see IUserDataHandler
     * @see UserRepository#setMultiDeliveryData(JSONObject)
     */
    public void requestDriverAcknowledged(IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        try {
            setMultiDeliveryData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.sendCallDriverAcknowledge(jsonObject, mDataCallback);

    }

    /**
     * Emit request MuliDelivery Accept Call event
     *
     * @param context        Holding the reference of an activity.
     * @param acceptedSecond The timer interval at which driver accept the call.
     * @param handler        The Callback that will be invoked when response received.
     */
    public void requestMultiDeliveryAcceptCall(Context context, String acceptedSecond,
                                               IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {

            setMultiDeliveryData(jsonObject);
            jsonObject.put("accept_timer_seconds", acceptedSecond);
            jsonObject.put("os", Build.VERSION.SDK_INT);
            jsonObject.put("os_name", Constants.OS_NAME);
            jsonObject.put("imei", Utils.getDeviceId(context));
            try {
                int battery = Integer.parseInt(Utils.getBatteryPercentage(context)
                        .split(" ")[0]);
                int signalStrength = Integer.parseInt(Utils.getSignalStrength(context));
                jsonObject.put("battery", battery);
                jsonObject.put("connection_strength", signalStrength);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.acceptMultiDeliveryRequest(jsonObject, mDataCallback);

    }

    /**
     * Emit Driver Arrived data.
     *
     * @param handler The Callback that will be invoked when driver arrived response received.
     * @see IUserDataHandler
     * @see UserRepository#setMultiDeliveryData(JSONObject)
     */
    public void requestMultiDeliveryDriverArrived(IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        try {
            setMultiDeliveryData(jsonObject);
            LocCoordinatesInTrip arrivedLatLng = new LocCoordinatesInTrip();
            arrivedLatLng.setLat(String.valueOf(AppPreferences.getLatitude()));
            arrivedLatLng.setLng(String.valueOf(AppPreferences.getLongitude()));
            arrivedLatLng.setDate(Utils.getIsoDate());
            ArrayList<LocCoordinatesInTrip> prevLatLngList = AppPreferences.getLocCoordinatesInTrip();
            prevLatLngList.add(arrivedLatLng);
            jsonObject.put("route", new Gson().toJson(prevLatLngList));

            AppPreferences.clearTripDistanceData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.requestMultideliveryDriverArrived(jsonObject, mDataCallback);

    }

    /**
     * Emit Driver Started data.
     *
     * @param activity context of the activity
     * @param handler  The Callback that will be invoked when driver started response received.
     * @param address  address received
     * @see IUserDataHandler
     * @see UserRepository#setMultiDeliveryData(JSONObject)
     */
    public void requestMultiDeliveryDriverStarted(Activity activity, IUserDataHandler handler, String address) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        try {
            setMultiDeliveryData(jsonObject);
            jsonObject.put("start_address", address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.requestMultiDriverStartedRide(jsonObject, mDataCallback);
    }

    /**
     * Emit Driver Finished data.
     *
     * @param handler               The Callback that will be invoked when driver finish event response received.
     * @param reverseGeoCodeAddress
     * @see IUserDataHandler
     * @see UserRepository#setMultiDeliveryData(JSONObject)
     */
    public void requestMultiDeliveryDriverFinishRide(DirectionDropOffData data,
                                                     IUserDataHandler handler, String reverseGeoCodeAddress) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        try {
            setMultiDeliveryData(jsonObject);
            jsonObject.put("trip_id", data.getTripID());
            jsonObject.put("route", new Gson()
                    .toJson(AppPreferences.getLocCoordinatesInTrip()));

            if (StringUtils.isNotEmpty(reverseGeoCodeAddress)) {
                jsonObject.put("address", reverseGeoCodeAddress);
            }

            directionDropOffData = data;
            AppPreferences.clearTripDistanceData();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.requestMultiDriverFinishRide(jsonObject, mDataCallback);
    }

    /**
     * Emit Driver Finished data.
     *
     * @param tripID             finishing trip id
     * @param receivedAmount     amount that is received by partner
     * @param rating             feedback rating
     * @param isDeliveryFeedback check whether finishing of multiple delivery ride or multiple delivery simple ride
     * @param deliveryStatus     delivery success or fail
     * @param deliveryMsg        delivery success or fail msg
     * @param receiverName       receiver name
     * @param receiverPhone      receiver phone number
     * @param handler            handler The Callback that will be invoked when driver finish event response received.
     * @see IUserDataHandler
     * @see UserRepository#setMultiDeliveryData(JSONObject)
     */
    public void requestMultiDeliveryDriverFeedback(String tripID,
                                                   int receivedAmount,
                                                   float rating,
                                                   boolean isDeliveryFeedback,
                                                   boolean deliveryStatus,
                                                   String deliveryMsg,
                                                   String receiverName,
                                                   String receiverPhone,
                                                   IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        try {
            setMultiDeliveryData(jsonObject);
            jsonObject.remove("batch_id");
            jsonObject.remove("trip_type");
            jsonObject.put("trip_id", tripID);
            jsonObject.put("rate", rating);
            jsonObject.put("feedback", "nice");
            jsonObject.put("received_amount", receivedAmount);
            if (isDeliveryFeedback) {
                jsonObject.put("delivery_status", deliveryStatus);
                jsonObject.put("delivery_message", deliveryMsg);
                jsonObject.put("received_by_name", receiverName);
                jsonObject.put("received_by_phone", receiverPhone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.requestMultiDeliveryDriverFeedback(jsonObject, mDataCallback);
    }

    /**
     * Emit driver cancel batch request.
     *
     * @param cancelReason The cancellation reason.
     * @param handler      The Callback that will be invoked when driver arrived response received.
     * @see IUserDataHandler
     * @see UserRepository#setMultiDeliveryData(JSONObject)
     */
    public void requestMultiDeliveryCancelBatch(String cancelReason, IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        try {
            setMultiDeliveryData(jsonObject);
            jsonObject.put("cancelled_at", Utils.getIsoDate());
            jsonObject.put("cancel_reason", cancelReason);

        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.requestMultideliveryCancelBatch(jsonObject, mDataCallback);

    }


    //endregion


    /***
     * Send request to API server using socket connection which Assigns scheduled request
     * send by passenger
     *
     * @param context Calling context
     * @param bookingId Booking ID of the request
     * @param handler Response handler callback
     */
    public void requestAcceptScheduledCall(Context context,
                                           String bookingId,
                                           IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("bId", bookingId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.acceptScheduledCall(jsonObject, mDataCallback);

    }


    public void requestReverseGeocoding(Context context, IUserDataHandler handler,
                                        String latLng, String key) {
        mUserCallback = handler;
        mContext = context;
        mRestRequestHandler.reverseGeoding(mContext, mDataCallback, latLng, key);
    }

    public void requestWalletHistory(Context context, IUserDataHandler handler, String pageNo) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getWalletHistory(mContext, mDataCallback, AppPreferences.getDriverId(),
                AppPreferences.getAccessToken(), pageNo);
    }

    public void requestSettings(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        if (AppPreferences.isLoggedIn()) {
            mRestRequestHandler.getSettings(mContext, mDataCallback);
        } else {
            mRestRequestHandler.getSettingsBeforeLogin(mContext, mDataCallback);
        }
    }

    public void requestSignUpSettings(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestSignUpSettings(mContext, mDataCallback);
    }

    public void requestRegisterNumber(Context context, String phone, String city, String cnic, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestRegisterNumber(mContext, phone, city, cnic, mDataCallback);
    }

    public void postOptionalSignupData(Context context, String id, String email, String referenceNo, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.postOptionalSignupData(mContext, id, email, referenceNo, mDataCallback);
    }

    public void postBiometricVerification(Context context, String id, boolean isVerified, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.postBiometricVerification(mContext, id, isVerified, mDataCallback);
    }
/*
    public void requestCompleteSignupData(Context context, String id, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestCompleteSignupData(mContext, id, mDataCallback);
    }*/

    public void uploadDocumentImage(Context context, String id, String type, File imageFile, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.uploadDocumentImage(mContext, id, type, imageFile, mDataCallback);
    }

    public void requestBankAccounts(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestBankAccounts(mContext, mDataCallback);
    }

    public void requestBankAccountsDetails(Context context, String bankId, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestBankAccountsDetails(mContext, bankId, mDataCallback);
    }

    public void requestContactNumbers(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getContactNumbers(mContext, mDataCallback, AppPreferences.getDriverId(),
                AppPreferences.getAccessToken());
    }


    public void getConversationId(Context context, IUserDataHandler handler, String passId, String tripId) {
        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("tid", tripId);
            jsonObject.put("user_type", "d");
//            jsonObject.put("service_code", AppPreferences.getCallData().getServiceCode());
            jsonObject.put("driver_id", AppPreferences.getDriverId());
            jsonObject.put("passenger_id", passId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.getConversationId(mDataCallback, jsonObject);
    }

    public void getConversationChat(Context context, IUserDataHandler handler, String conversationId) {
        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("user_type", "d");
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("conversation_id", conversationId);
//            jsonObject.put("service_code",  AppPreferences.getCallData().getServiceCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.getConversationChat(mDataCallback, jsonObject);
    }

    /***
     * Update driver availability status on API server.
     * @param context Calling Context.
     * @param handler Response callback
     * @param driverStatus driver status
     */
    public void requestDriverUpdateStatus(Context context, IUserDataHandler handler, boolean driverStatus) {
        mContext = context;
        mUserCallback = handler;
        PilotData pilotData = AppPreferences.getPilotData();
        DriverAvailabilityRequest statusRequest = new DriverAvailabilityRequest();
        try {
            //region Setting MixPanel properties JSON
            JSONObject properties = new JSONObject();
            properties.put("DriverID", pilotData.getId());
            properties.put("timestamp", Utils.getIsoDate());
            properties.put("SignUpCity", pilotData.getCity() != null
                    ? pilotData.getCity().getName() : "N/A");
            properties.put("DriverName", pilotData.getFullName());
            properties.put("CurrentLocation", Utils.getCurrentLocation());
            properties.put("cih", AppPreferences.getCashInHands());
            properties.put("status", driverStatus ? "Active" : "Inactive");

            //endregion

            //region Driver status request Body
            statusRequest.setAvailable(driverStatus);
            statusRequest.setId(AppPreferences.getDriverId());
            statusRequest.setTokenID(AppPreferences.getAccessToken());

            statusRequest.setLatitude(AppPreferences.getLatitude());
            statusRequest.setLongitude(AppPreferences.getLongitude());
            statusRequest.setCih(AppPreferences.getCashInHands());
            statusRequest.setImei(Utils.getDeviceId(mContext));

            //endregion

            if (driverStatus && AppPreferences.getDriverDestination() != null) {
                statusRequest.setEndingLatitude(String.valueOf(AppPreferences.getDriverDestination().latitude));
                statusRequest.setEndingLongitude(String.valueOf(AppPreferences.getDriverDestination().longitude));
                statusRequest.setEndingAddress(AppPreferences.getDriverDestination().address);

                properties.put("DD", true);
                properties.put("DDLocation", AppPreferences.getDriverDestination().latitude
                        + "," + AppPreferences.getDriverDestination().longitude);
                properties.put("DDAddress", AppPreferences.getDriverDestination().address);
            } else {
                properties.put("DD", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRestRequestHandler.requestDriverStatusUpdate(mContext, statusRequest, mDataCallback);


    }

    public void sendMessage(Context context, IUserDataHandler handler, String message,
                            String conversationId, String receiverId, String messageType, String tripId) {

        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("tid", tripId);
            jsonObject.put("user_type", "d");
            jsonObject.put("receiver_id", receiverId);
            jsonObject.put("conversation_id", conversationId);
            jsonObject.put("message_type", messageType);
            jsonObject.put("message", message);
            jsonObject.put("service_code", AppPreferences.getCallData().getServiceCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mWebIORequestHandler.sendMessage(mDataCallback, jsonObject);

    }

    public void requestDriverStats(Context context, IUserDataHandler handler, boolean isHome) {
        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", AppPreferences.getDriverId());
            jsonObject.put("token_id", AppPreferences.getAccessToken());
            jsonObject.put("app_version", Utils.getVersion());
            if (isHome) {
                jsonObject.put("home", true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mWebIORequestHandler.getDriverStats(mDataCallback, jsonObject);
    }

    public void updateDropOff(IUserDataHandler handler, Context context,
                              String tid, String end_address, String lat, String lng) {
        mUserCallback = handler;
        mContext = context;
        PilotData user = AppPreferences.getPilotData();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(UserRepository.UpdateDropOff.END_ADDRESS, end_address);
            jsonObject.put(UserRepository.UpdateDropOff.TOKEN_ID, user.getAccessToken());
            jsonObject.put(UserRepository.UpdateDropOff.TRIP_ID, tid);
            jsonObject.put(UserRepository.UpdateDropOff.ID, user.getId());
            jsonObject.put(UserRepository.UpdateDropOff.END_LAT, "" + lat);
            jsonObject.put(UserRepository.UpdateDropOff.END_LNG, "" + lng);
            jsonObject.put(UserRepository.UpdateDropOff.TYPE, "" + "p");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mWebIORequestHandler.updateDropOff(mDataCallback, jsonObject);
    }

    public static final class UpdateDropOff {
        public static final String TOKEN_ID = "token_id";
        public static final String TRIP_ID = "tid";
        public static final String ID = "_id";
        public static final String END_ADDRESS = "end_address";
        public static final String END_LAT = "lat";
        public static final String END_LNG = "lng";
        public static final String TYPE = "type";
    }


    public void requestChangePin(Context context, String oldPin, String newPin, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestChangePin(context, newPin, oldPin, mDataCallback);
    }

    public void getProfileData(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getProfileData(context, mDataCallback);
    }

    public void postProblem(Context context,
                            IUserDataHandler handler,
                            String selectedReason,
                            String tripId,
                            String email,
                            String contactType,
                            String details,
                            boolean isFromReport) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.postProblem(context,
                selectedReason,
                tripId,
                email,
                contactType,
                details,
                isFromReport, mDataCallback);
    }

    public void downloadAudioFile(Context context, String path, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.downloadAudioFile(context, mDataCallback, path);
    }

    public void updateRegid(Context context,
                            IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        PilotData user = AppPreferences.getPilotData();
        mRestRequestHandler.updateRegid(mDataCallback, user.getId(), AppPreferences.getRegId(), user.getAccessToken(), context);
    }


    public void requestZones(Context context, UserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestZones(context, mDataCallback);
    }

    public void requestShahkar(Context context, UserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestShahkar(context, mDataCallback);
    }

    public void requestBonusChart(Context context, UserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestBonusChart(context, mDataCallback);

    }

    public void requestDriverPerformance(Context context, UserDataHandler handler, int weekStatus) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestPerformanceData(context, mDataCallback, weekStatus);
    }

    /**
     * this method can be invoked to get stats data from kronos
     *
     * @param context of the activity
     * @param handler callback to get data
     * @see com.bykea.pk.partner.models.data.SettingsData for kronos URLs
     */
    public void requestDriverVerifiedBookingStats(Context context, int weekStatus, UserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestDriverVerifiedBookingStats(context, weekStatus, mDataCallback);
    }

    public void requestLoadBoard(Context context, UserDataHandler handler, String lat, String lng) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestLoadBoard(context, mDataCallback, lat, lng);
    }


    public void requestZoneAreas(Context context, ZoneData zone, UserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.requestZoneAreas(context, zone, mDataCallback);
    }

    public void addSavedPlace(Context context, SavedPlaces savedPlaces, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.addSavedPlace(mContext, mDataCallback, savedPlaces);
    }

    public void updateSavedPlace(Context context, SavedPlaces savedPlaces, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.updateSavedPlace(mContext, mDataCallback, savedPlaces);
    }


    public void deleteSavedPlace(Context context, SavedPlaces savedPlaces, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.deleteSavedPlace(mContext, mDataCallback, savedPlaces);
    }

    public void getSavedPlaces(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getSavedPlaces(mContext, mDataCallback);
    }


    public void topUpPassengerWallet(Context context, NormalCallData data, String amount, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.topUpPassengerWallet(mContext, data, amount, mDataCallback);
    }

    /**
     * This method is responsible for updating App version on server
     */
    public void updateAppVersion(UserDataHandler handler) {
        mUserCallback = handler;
        mRestRequestHandler.updateAppVersion(mDataCallback);
    }

    /**
     * accept request for specific booking
     *
     * @param context   Context
     * @param bookingId selected booking id
     * @param handler   callback
     */
    public void acceptLoadboardBooking(Context context, String bookingId, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.acceptLoadboardBooking(mContext, bookingId, mDataCallback);

    }

    public void setCallback(IUserDataHandler handler) {
        mUserCallback = handler;
    }

    private IResponseCallback mDataCallback = new IResponseCallback() {
        @Override
        public void onResponse(Object object) {
            String className = object.getClass().getSimpleName();
            if (null != mUserCallback) {
                switch (className) {
                    case "RegisterResponse":
                        mUserCallback.onUserRegister((RegisterResponse) object);
                        break;
                    case "DriverDestResponse":
                        mUserCallback.onDropOffUpdated((DriverDestResponse) object);
                        break;
                    case "LoginResponse":
                        mUserCallback.onUserLogin((LoginResponse) object);
                        break;
                    case "LogoutResponse":
                        mUserCallback.onPilotLogout((LogoutResponse) object);
                        break;
                    case "SettingsResponse":
                        SettingsResponse settingsResponse = (SettingsResponse) object;
                        if (settingsResponse.getData() != null && settingsResponse.getData().getSettings() != null) {
                            AppPreferences.setSettingsVersion(settingsResponse.getSetting_version());
                            AppPreferences.saveSettingsData(settingsResponse.getData());
                            if (settingsResponse.getData().getSettings().getCih_range() != null) {
                                AppPreferences.setCashInHandsRange(settingsResponse.getData().getSettings().getCih_range());
                            }
                            mUserCallback.onGetSettingsResponse(true);
                        } else {
                            mUserCallback.onGetSettingsResponse(false);
                        }
                        break;
                    case "GetCitiesResponse":
                        AppPreferences.setAvailableCities((GetCitiesResponse) object);
                        mUserCallback.onCitiesResponse(((GetCitiesResponse) object));
                        break;
                    case "PilotStatusResponse":
                        mUserCallback.onUpdateStatus((PilotStatusResponse) object);
                        break;
                    case "AckCallResponse":
                        mUserCallback.onAck(((AckCallResponse) object).getMessage());
                        break;
                    case "ArrayList":
                        mUserCallback.getHeatMap((ArrayList<HeatMapUpdatedResponse>) object);
                        break;
                    case "UpdateProfileResponse":
                        mUserCallback.onUpdateProfile((UpdateProfileResponse) object);
                        break;
                    case "WalletHistoryResponse":
                        mUserCallback.getWalletData((WalletHistoryResponse) object);
                        break;
                    case "BankAccountListResponse":
                        mUserCallback.getAccountNumbers((BankAccountListResponse) object);
                        break;
                    case "BankDetailsResponse":
                        mUserCallback.onBankDetailsResponse((BankDetailsResponse) object);
                        break;
                    case "ContactNumbersResponse":
                        mUserCallback.getContactNumbers((ContactNumbersResponse) object);
                        break;
                    case "CheckDriverStatusResponse":
                        mUserCallback.onRunningTrips((CheckDriverStatusResponse) object);
                        break;
                    case "TripHistoryResponse":
                        mUserCallback.onGetTripHistory((TripHistoryResponse) object);
                        break;
                    case "TripMissedHistoryResponse":
                        mUserCallback.onGetMissedTripHistory((TripMissedHistoryResponse) object);
                        break;
                    case "GeocoderApi":
                        mUserCallback.onReverseGeocode((GeocoderApi) object);
                        break;
                    case "CancelRideResponse":
                        mUserCallback.onCancelRide((CancelRideResponse) object);
                        break;
                    case "FreeDriverResponse":
                        mUserCallback.onFreeDriver((FreeDriverResponse) object);
                        break;
                    case "UploadAudioFile":
                        mUserCallback.onUploadAudioFile((UploadAudioFile) object);
                        break;
                    case "UploadImageFile":
                        mUserCallback.onUploadImageFile((UploadImageFile) object);
                        break;
                    case "UploadDocumentFile":
                        mUserCallback.onUploadFile((UploadDocumentFile) object);
                        break;
                    case "ForgotPasswordResponse":
                        mUserCallback.onForgotPassword((ForgotPasswordResponse) object);
                        break;
                    case "VerifyCodeResponse":
                        mUserCallback.onCodeVerification((VerifyCodeResponse) object);
                        break;
                    case "VerifyNumberResponse":
                        mUserCallback.onNumberVerification((VerifyNumberResponse) object);
                        break;
                    case "AcceptCallResponse":
                        WebIORequestHandler.getInstance().registerChatListener();
                        mUserCallback.onAcceptCall((AcceptCallResponse) object);
                        break;
                    case "RejectCallResponse":
                        mUserCallback.onRejectCall((RejectCallResponse) object);
                        break;
                    case "ArrivedResponse":
                        mUserCallback.onArrived((ArrivedResponse) object);
                        break;
                    case "BeginRideResponse":
                        mUserCallback.onBeginRide((BeginRideResponse) object);
                        break;
                    case "EndRideResponse":
                        WebIORequestHandler.getInstance().unRegisterChatListener();
                        mUserCallback.onEndRide((EndRideResponse) object);
                        break;
                    case "FeedbackResponse":
                        mUserCallback.onFeedback((FeedbackResponse) object);
                        break;
                    case "ConversationResponse":
                        mUserCallback.onGetConversations((ConversationResponse) object);
                        break;
                    case "SendMessageResponse":
                        mUserCallback.onSendMessage((SendMessageResponse) object);
                        break;
                    case "ConversationChatResponse":
                        mUserCallback.onGetConversationChat((ConversationChatResponse) object);
                        break;
                    case "UpdateConversationStatusResponse":
                        mUserCallback.onUpdateConversationStatus((UpdateConversationStatusResponse) object);
                        break;
                    case "GetConversationIdResponse":
                        mUserCallback.onGetConversationId((GetConversationIdResponse) object);
                        break;
                    case "ServiceTypeResponse":
                        mUserCallback.onGetServiceTypes((ServiceTypeResponse) object);
                        break;
                    case "ChangePinResponse":
                        mUserCallback.onChangePinResponse(((ChangePinResponse) object));
                        break;
                    case "GetProfileResponse":
                        mUserCallback.onGetProfileResponse(((GetProfileResponse) object));
                        break;
                    case "DriverStatsResponse":
                        mUserCallback.onDriverStatsResponse(((DriverStatsResponse) object));
                        break;
                    case "UpdateDropOffResponse":
                        mUserCallback.onUpdateDropOff(((UpdateDropOffResponse) object));
                        break;
                    case "ProblemPostResponse":
                        mUserCallback.onProblemPosted((ProblemPostResponse) object);
                        break;
                    case "DownloadAudioFileResponse":
                        mUserCallback.onDownloadAudio((DownloadAudioFileResponse) object);
                        break;
                    case "UpdateRegIDResponse":
                        PilotData user = AppPreferences.getPilotData();
                        if (user != null) {
                            user.setReg_id(AppPreferences.getRegId());
                            AppPreferences.setPilotData(user);
                        }
                        mUserCallback.onUpdateRegid((UpdateRegIDResponse) object);
                        break;
                    case "AddSavedPlaceResponse":
                        mUserCallback.onAddSavedPlaceResponse((AddSavedPlaceResponse) object);
                        break;
                    case "DeleteSavedPlaceResponse":
                        mUserCallback.onDeleteSavedPlaceResponse();
                        break;
                    case "GetSavedPlacesResponse":
                        GetSavedPlacesResponse getSavedPlacesResponse = (GetSavedPlacesResponse) object;
                        if (getSavedPlacesResponse.getData() != null && getSavedPlacesResponse.getData().size() > 0) {
                            for (SavedPlaces place : getSavedPlacesResponse.getData()) {
                                place.setPlaceId(place.getUserId());  // in response _id is place id
                                if (place.getLoc() != null && place.getLoc().size() > 1) {
                                    place.setLat(place.getLoc().get(0));
                                    place.setLng(place.getLoc().get(1));
                                    place.getLoc().clear();
                                }
                            }
                            AppPreferences.updateSavedPlace(getSavedPlacesResponse.getData());
                        }
                        AppPreferences.setSavedPlacesAPICalled(true);
                        mUserCallback.onGetSavedPlacesResponse((GetSavedPlacesResponse) object);
                        break;
                    case "GetZonesResponse":
                        mUserCallback.onZonesResponse((GetZonesResponse) object);
                        break;
                    case "ZoneAreaResponse":
                        mUserCallback.onZoneAreasResponse((ZoneAreaResponse) object);
                        break;
                    case "TopUpPassengerWalletResponse":
                        mUserCallback.onTopUpPassWallet((TopUpPassWalletResponse) object);
                        break;
                    case "LocationResponse":
                        mUserCallback.onLocationUpdate((LocationResponse) object);
                        break;
                    case "SignUpSettingsResponse":
                        SignUpSettingsResponse response = (SignUpSettingsResponse) object;
                        response.setTimeStamp(System.currentTimeMillis());
                        AppPreferences.setObjectToSharedPref(response);
                        mUserCallback.onSignUpSettingsResponse(response);
                        break;
                    case "SignUpAddNumberResponse":
                        mUserCallback.onSignUpAddNumberResponse((SignUpAddNumberResponse) object);
                        break;
                    case "SignupUplodaImgResponse":
                        mUserCallback.onSignUpImageResponse((SignupUplodaImgResponse) object);
                        break;
                    case "SignUpOptionalDataResponse":
                        mUserCallback.onSignUpOptionalResponse((SignUpOptionalDataResponse) object);
                        break;
                    case "SignUpCompleteResponse":
                        mUserCallback.onSignupCompleteResponse((SignUpCompleteResponse) object);
                        break;
                    case "BiometricApiResponse":
                        mUserCallback.onBiometricApiResponse((BiometricApiResponse) object);
                        break;
                    case "ShahkarResponse":
                        mUserCallback.onShahkarResponse((ShahkarResponse) object);
                        break;
                    case "RankingResponse":
                        mUserCallback.onBonusChartResponse((RankingResponse) object);
                        break;
                    case "DriverPerformanceResponse":
                        mUserCallback.onDriverPerformanceResponse((DriverPerformanceResponse) object);
                        break;
                    case "LoadBoardResponse":
                        mUserCallback.onLoadBoardResponse((LoadBoardResponse) object);
                        break;
                    case "UpdateAppVersionResponse":
                        mUserCallback.onUpdateAppVersionResponse((UpdateAppVersionResponse) object);
                        break;
                    case "AcceptLoadboardBookingResponse":
                        WebIORequestHandler.getInstance().registerChatListener();
                        mUserCallback.onAcceptLoadboardBookingResponse((AcceptLoadboardBookingResponse) object);
                        break;
                    case "MultiDeliveryCallDriverAcknowledgeResponse":
                        mUserCallback.onDriverAcknowledgeResponse(
                                (MultiDeliveryCallDriverAcknowledgeResponse) object
                        );
                        break;
                    case "MultiDeliveryDriverArrivedResponse":
                        mUserCallback.onMultiDeliveryDriverArrived(
                                (MultiDeliveryDriverArrivedResponse) object
                        );
                        break;
                    case "MultiDeliveryAcceptCallResponse":
                        mUserCallback.onMultiDeliveryAcceptCall(
                                (MultiDeliveryAcceptCallResponse) object
                        );
                        break;
                    case "MultiDeliveryDriverStartedResponse":
                        mUserCallback.onMultiDeliveryDriverStarted(
                                (MultiDeliveryDriverStartedResponse) object
                        );
                        break;
                    case "MultiDeliveryCompleteRideResponse":
                        mUserCallback.onMultiDeliveryDriverRideFinish(
                                (MultiDeliveryCompleteRideResponse) object,
                                directionDropOffData
                        );
                        break;
                    case "MultiDeliveryFeedbackResponse":
                        mUserCallback.onMultiDeliveryDriverFeedback(
                                (MultiDeliveryFeedbackResponse) object
                        );
                        break;
                    case "MultiDeliveryCancelBatchResponse":
                        mUserCallback.onMultiDeliveryDriverCancelBatch(
                                (MultiDeliveryCancelBatchResponse) object
                        );
                        break;
                    case "BookingListingResponse":
                        mUserCallback.onBookingListingResponse((BookingListingResponse) object);
                        break;
                    case "DriverVerifiedBookingResponse":
                        mUserCallback.onDriverVerifiedBookingResponse((DriverVerifiedBookingResponse) object);
                        break;
                    case "CommonResponse":
                        mUserCallback.onCommonResponse((CommonResponse) object);
                        break;

                }
            } else {
                Utils.redLog("UserRepo", "mUserCallback is Null");
            }
        }

        @Override
        public void onError(int errorCode, String error) {
            /*if (errorCode == HTTPStatus.UNAUTHORIZED) {
                EventBus.getDefault().post(Keys.UNAUTHORIZED_BROADCAST);
            } else {
                mUserCallback.onError(errorCode, error);
            }*/
            mUserCallback.onError(errorCode, error);
        }

    };

}
