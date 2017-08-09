package com.bykea.pk.partner.repositories;

import android.content.Context;

import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.communication.rest.RestRequestHandler;
import com.bykea.pk.partner.communication.socket.WebIORequestHandler;
import com.bykea.pk.partner.models.data.LocCoordinatesInTrip;
import com.bykea.pk.partner.models.data.PilotData;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.AccountNumbersResponse;
import com.bykea.pk.partner.models.response.AckCallResponse;
import com.bykea.pk.partner.models.response.ArrivedResponse;
import com.bykea.pk.partner.models.response.BeginRideResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.ConversationChatResponse;
import com.bykea.pk.partner.models.response.ConversationResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.models.response.EndRideResponse;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.models.response.FreeDriverResponse;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.GetConversationIdResponse;
import com.bykea.pk.partner.models.response.GetProfileResponse;
import com.bykea.pk.partner.models.response.HeatMapResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.models.response.SendMessageResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.SettingsResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateConversationStatusResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
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

public class UserRepository {

    private Context mContext;
    private IUserDataHandler mUserCallback;
    private WebIORequestHandler mWebIORequestHandler;
    private RestRequestHandler mRestRequestHandler;

    public UserRepository() {
        mWebIORequestHandler = WebIORequestHandler.getInstance();
        mRestRequestHandler = new RestRequestHandler();
    }

    public void requestUserLogin(Context context, IUserDataHandler handler, String email, String password) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.sendUserLogin(context, mDataCallback, email, password,
                Constants.DEVICE_TYPE, "2", AppPreferences.getRegId(context));

    }

    public void requestPilotLogout(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.sendLogout(context, mDataCallback);

    }

    public void requestUserRegister(Context context, IUserDataHandler handler, PilotData data) {
        mContext = context;
        mUserCallback = handler;

        mRestRequestHandler.registerUser(context, mDataCallback, data);
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

    public void requestTripHistory(Context context, IUserDataHandler handler, String pageNo) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getTripHistory(mContext, mDataCallback, pageNo);
    }

    public void requestMissedTripHistory(Context context, IUserDataHandler handler, String pageNo) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getMissedTripHistory(mContext, mDataCallback, pageNo);
    }

    public void requestUploadFile(Context context, IUserDataHandler handler, File file) {
        if (Connectivity.isConnectedFast(context)) {
            mContext = context;
            mUserCallback = handler;
            mRestRequestHandler.uplaodDriverDocument(context, mDataCallback, file);
        }
    }

    public void uploadAudioFile(Context context, IUserDataHandler handler, File file) {
        if (Connectivity.isConnectedFast(context)) {
            mContext = context;
            mUserCallback = handler;
            mRestRequestHandler.uploadAudioFile(mContext, mDataCallback, file);
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

    public void requestHeatMapData(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            if (AppPreferences.getPilotData(context) != null) {
                if (StringUtils.isNotBlank(AppPreferences.getPilotData(context).getService_type())) {
                    jsonObject.put("service_type", AppPreferences.getPilotData(context).getService_type());
                }
                if (AppPreferences.getPilotData(context).getCity() != null &&
                        StringUtils.isNotBlank(AppPreferences.getPilotData(context).getCity().get_id()))
                    jsonObject.put("city", AppPreferences.getPilotData(context).getCity().get_id());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.requestHeatmap(jsonObject, mDataCallback);
    }


    public void requestLocationUpdate(Context context, IUserDataHandler handler) {

        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            Utils.redLog("token_id at Location", AppPreferences.getAccessToken(context));
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("lat", AppPreferences.getLatitude(mContext));
            jsonObject.put("lng", AppPreferences.getLongitude(mContext));
            jsonObject.put("status", AppPreferences.getTripStatus(mContext));
            Utils.redLog("Status", AppPreferences.getTripStatus(mContext));

            // THIS CHECK IS FOR TRACKING DURING TRIP...
            if (AppPreferences.isOnTrip(mContext)) {
                jsonObject.put("eta", AppPreferences.getEta(mContext));
                jsonObject.put("passenger_id", AppPreferences.getCallData(mContext).getPassId());
                jsonObject.put("trip_id", AppPreferences.getCallData(mContext).getTripId());
                jsonObject.put("inCall", true);
                jsonObject.put("end_lat", AppPreferences.getCallData(mContext).getEndLat());
                jsonObject.put("end_lng", AppPreferences.getCallData(mContext).getEndLng());
                jsonObject.put("end_address", AppPreferences.getCallData(mContext).getEndAddress());
            } else {
                //to free driver after trip Finished
                if ("finished".equalsIgnoreCase(AppPreferences.getTripStatus(mContext))) {
                    jsonObject.put("inCall", true);
                } else {
                    jsonObject.put("inCall", false);
                }
            }
            Utils.redLog("isInCall", jsonObject.get("inCall") + "");


        } catch (Exception ex) {

        }

        mWebIORequestHandler.requestLocationUpdate(jsonObject, mDataCallback);

    }


    public void requestLocationUpdate(Context context, IUserDataHandler handler,
                                      double lat, double lon) {

        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            Utils.redLog("token_id at Location", AppPreferences.getAccessToken(context));
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            String driverId = AppPreferences.getDriverId(context);
            if (StringUtils.isBlank(driverId)) {
                return;
            }
            jsonObject.put("_id", driverId);
            jsonObject.put("lat", lat + "");
            jsonObject.put("lng", lon + "");
            String tripStatus = StringUtils.EMPTY;
            // THIS CHECK IS FOR TRACKING DURING TRIP...
            if (AppPreferences.isOnTrip(mContext)) {
                tripStatus = AppPreferences.getCallData(mContext) != null
                        && StringUtils.isNotBlank(AppPreferences.getCallData(mContext).getStatus())
                        ? AppPreferences.getCallData(mContext).getStatus() : StringUtils.EMPTY;
                jsonObject.put("eta", AppPreferences.getEta(mContext));
                jsonObject.put("distance", AppPreferences.getEstimatedDistance(mContext));
                jsonObject.put("passenger_id", AppPreferences.getCallData(mContext).getPassId());
                jsonObject.put("trip_id", AppPreferences.getCallData(mContext).getTripId());
                jsonObject.put("inCall", true);
//                jsonObject.put("end_lat", AppPreferences.getCallData(mContext).getEndLat());
//                jsonObject.put("end_lng", AppPreferences.getCallData(mContext).getEndLng());
//                jsonObject.put("end_address", AppPreferences.getCallData(mContext).getEndAddress());
            } else {
                //to free driver after trip Finished
                if ("finished".equalsIgnoreCase(AppPreferences.getTripStatus(mContext))) {
                    jsonObject.put("inCall", true);
                } else {
                    jsonObject.put("inCall", false);
                }
            }
            if (StringUtils.isBlank(tripStatus)) {
                tripStatus = AppPreferences.getTripStatus(mContext);
            }
            jsonObject.put("status", tripStatus);
            Utils.redLog("isInCall", jsonObject.get("inCall") + "");


        } catch (Exception ex) {

        }

        mWebIORequestHandler.requestLocationUpdate(jsonObject, mDataCallback);

    }

    public void freeDriverStatus(Context context, IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("lat", AppPreferences.getLatitude(context) + "");
            jsonObject.put("lng", AppPreferences.getLongitude(context) + "");
            jsonObject.put("trip_id", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("pid", AppPreferences.getCallData(context).getPassId());
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
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("did", AppPreferences.getDriverId(context));
            jsonObject.put("tid", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("pid", AppPreferences.getCallData(context).getPassId());
            jsonObject.put("lat", AppPreferences.getLatitude(context) + "");
            jsonObject.put("lng", AppPreferences.getLongitude(context) + "");
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
        String tripID = AppPreferences.getCallData(context).getTripId();
        if (tripID.equalsIgnoreCase(AppPreferences.getLastAckTripID(context))) {
            return;
        }
        AppPreferences.setLastAckTripID(mContext, tripID);
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("trip_id", tripID);
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("pass_id", AppPreferences.getCallData(context).getPassId());
            //            jsonObject.put("event_id", AppPreferences.getCallData(context).getEvent_id());
            jsonObject.put("full_name", AppPreferences.getPilotData(context).getFullName());
            jsonObject.put("pass_socket_id", AppPreferences.getCallData(context).getPass_socket_id());
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
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("driver_id", AppPreferences.getDriverId(context));
            jsonObject.put("did", AppPreferences.getDriverId(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("trips_id", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("passenger_id", AppPreferences.getCallData(context).getPassId());
            jsonObject.put("pid", AppPreferences.getCallData(context).getPassId());
            jsonObject.put("lat", AppPreferences.getLatitude(context));
            jsonObject.put("lng", AppPreferences.getLongitude(context));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mWebIORequestHandler.rejectNormalCall(jsonObject, mDataCallback);

    }

    public void requestCancelRide(Context context, IUserDataHandler handler, String message) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("driver_id", AppPreferences.getDriverId(context));
            jsonObject.put("message", message);
            jsonObject.put("trips_id", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("tid", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("passenger_id", AppPreferences.getCallData(context).getPassId());
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("lat", AppPreferences.getLatitude(context) + "");
            jsonObject.put("lng", AppPreferences.getLongitude(context) + "");
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
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("did", AppPreferences.getDriverId(context));
            jsonObject.put("pid", AppPreferences.getCallData(context).getPassId());
            jsonObject.put("trip_id", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("tid", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("lat", AppPreferences.getLatitude(context) + "");
            jsonObject.put("lng", AppPreferences.getLongitude(context) + "");
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
            String startLat = AppPreferences.getLatitude(context) + "";
            String startLng = AppPreferences.getLongitude(context) + "";
            jsonObject.put("token_id", AppPreferences.getAccessToken(mContext));
            jsonObject.put("tid", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("trip_id", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("_id", AppPreferences.getDriverId(mContext));
            jsonObject.put("did", AppPreferences.getDriverId(mContext));
            jsonObject.put("startlatitude", startLat);
            jsonObject.put("startlongitude", startLng);
            jsonObject.put("start_address", AppPreferences.getCallData(mContext).getStartAddress());
            jsonObject.put("pid", AppPreferences.getCallData(mContext).getPassId());
            jsonObject.put("endlatitude", endLat);
            jsonObject.put("endlongitude", endLng);
            jsonObject.put("end_address", endAddress);

            //To update start latlng on App Side.
            NormalCallData callData = AppPreferences.getCallData(context);
            callData.setStartLat(startLat);
            callData.setStartLng(startLng);
            AppPreferences.setCallData(context, callData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.beginRide(jsonObject, mDataCallback);

    }

    public void requestEndRide(Context context, IUserDataHandler handler) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        int totalTime = 0;
        long diff = System.currentTimeMillis() - AppPreferences.getStartTripTime(mContext);
        if (diff > 0) {
            totalTime = (int) (diff / (1000));
        }
        try {
            jsonObject.put("driver_id", AppPreferences.getDriverId(context));
            jsonObject.put("did", AppPreferences.getDriverId(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("pid", AppPreferences.getCallData(context).getPassId());
            jsonObject.put("tid", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("trips_id", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("total_time", totalTime + "");

            String endLatString = AppPreferences.getLatitude(context) + "";
            String endLngString = AppPreferences.getLongitude(context) + "";
            jsonObject.put("endlatitude", endLatString);
            jsonObject.put("endlongitude", endLngString);

            jsonObject.put("lat", endLatString);
            jsonObject.put("lng", endLngString);

            LocCoordinatesInTrip startLatLng = new LocCoordinatesInTrip();
            startLatLng.setLat(AppPreferences.getCallData(context).getStartLat());
            startLatLng.setLng(AppPreferences.getCallData(context).getStartLng());
            startLatLng.setDate(Utils.getIsoDate());

            LocCoordinatesInTrip endLatLng = new LocCoordinatesInTrip();
            endLatLng.setLat(endLatString);
            endLatLng.setLng(endLngString);
            endLatLng.setDate(Utils.getIsoDate());
            ArrayList<LocCoordinatesInTrip> prevLatLngList = AppPreferences.getLocCoordinatesInTrip(context);
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
        Utils.infoLog("Ridetime", totalTime + " seconds");
        mWebIORequestHandler.endRide(jsonObject, mDataCallback);

    }

    public void requestFeedback(Context context, IUserDataHandler handler, String feedback,
                                String rate, String amount) {
        JSONObject jsonObject = new JSONObject();
        mUserCallback = handler;
        mContext = context;
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken(mContext));
            jsonObject.put("tid", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("trip_id", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("trips_id", AppPreferences.getCallData(context).getTripId());
            jsonObject.put("_id", AppPreferences.getDriverId(mContext));
            jsonObject.put("did", AppPreferences.getDriverId(mContext));
            jsonObject.put("driver_id", AppPreferences.getDriverId(mContext));
            jsonObject.put("pid", AppPreferences.getCallData(mContext).getPassId());
            jsonObject.put("passenger_id", AppPreferences.getCallData(mContext).getPassId());
            jsonObject.put("received_amount", amount);
            jsonObject.put("rate", rate);
            jsonObject.put("feedback", feedback);
            jsonObject.put("is_dispatch", AppPreferences.getCallData(context).isDispatcher());
            jsonObject.put("lat", AppPreferences.getLatitude(context) + "");
            jsonObject.put("lng", AppPreferences.getLongitude(context) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.feedback(jsonObject, mDataCallback);

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
        mRestRequestHandler.getWalletHistory(mContext, mDataCallback, AppPreferences.getDriverId(context),
                AppPreferences.getAccessToken(context), pageNo);
    }

    public void requestSettings(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getSettings(mContext, mDataCallback);
    }

    public void requestAccountNumbers(Context context, IUserDataHandler handler, String pageNo) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getAccountNumbers(mContext, mDataCallback, AppPreferences.getDriverId(context),
                AppPreferences.getAccessToken(context), pageNo);
    }

    public void requestContactNumbers(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        mRestRequestHandler.getContactNumbers(mContext, mDataCallback, AppPreferences.getDriverId(context),
                AppPreferences.getAccessToken(context));
    }


    public void getConversationId(Context context, IUserDataHandler handler, String passId, String tripId) {
        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token_id", AppPreferences.getAccessToken(mContext));
            jsonObject.put("_id", AppPreferences.getDriverId(mContext));
            jsonObject.put("tid", tripId);
            jsonObject.put("user_type", "d");
            jsonObject.put("driver_id", AppPreferences.getDriverId(mContext));
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
            jsonObject.put("token_id", AppPreferences.getAccessToken(mContext));
            jsonObject.put("user_type", "d");
            jsonObject.put("_id", AppPreferences.getDriverId(mContext));
            jsonObject.put("conversation_id", conversationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.getConversationChat(mDataCallback, jsonObject);
    }


    public void requestUpdateStatus(Context context, IUserDataHandler handler, boolean status) {

        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("is_available", "" + status);
            jsonObject.put("driver_id", AppPreferences.getDriverId(context));
            jsonObject.put("_id", AppPreferences.getDriverId(context));
            jsonObject.put("token_id", AppPreferences.getAccessToken(context));
            jsonObject.put("lat", AppPreferences.getLatitude(context));
            jsonObject.put("lng", AppPreferences.getLongitude(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebIORequestHandler.updatePilotStatus(mDataCallback, jsonObject);

    }

    public void sendMessage(Context context, IUserDataHandler handler, String message,
                            String conversationId, String receiverId, String messageType, String tripId) {

        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", AppPreferences.getDriverId(mContext));
            jsonObject.put("token_id", AppPreferences.getAccessToken(mContext));
            jsonObject.put("tid", tripId);
            jsonObject.put("user_type", "d");
            jsonObject.put("receiver_id", receiverId);
            jsonObject.put("conversation_id", conversationId);
            jsonObject.put("message_type", messageType);
            jsonObject.put("message", message);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mWebIORequestHandler.sendMessage(mDataCallback, jsonObject);

    }

    public void requestDriverStats(Context context, IUserDataHandler handler) {
        mContext = context;
        mUserCallback = handler;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", AppPreferences.getDriverId(mContext));
            jsonObject.put("token_id", AppPreferences.getAccessToken(mContext));
            jsonObject.put("app_version", Utils.getVersion(mContext));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mWebIORequestHandler.getDriverStats(mDataCallback, jsonObject);
    }

    public void updateDropOff(IUserDataHandler handler, Context context,
                              String tid, String end_address, String lat, String lng) {
        mUserCallback = handler;
        mContext = context;
        PilotData user = AppPreferences.getPilotData(mContext);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(UpdateDropOff.END_ADDRESS, end_address);
            jsonObject.put(UpdateDropOff.TOKEN_ID, user.getAccessToken());
            jsonObject.put(UpdateDropOff.TRIP_ID, tid);
            jsonObject.put(UpdateDropOff.ID, user.getId());
            jsonObject.put(UpdateDropOff.END_LAT, "" + lat);
            jsonObject.put(UpdateDropOff.END_LNG, "" + lng);
            jsonObject.put(UpdateDropOff.TYPE, "" + "p");
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

    private IResponseCallback mDataCallback = new IResponseCallback() {
        @Override
        public void onResponse(Object object) {
            if (object instanceof RegisterResponse) {
                mUserCallback.onUserRegister((RegisterResponse) object);
            } else if (object instanceof LoginResponse) {
                if (null != mUserCallback) {
                    mUserCallback.onUserLogin((LoginResponse) object);
                }
            } else if (object instanceof LogoutResponse) {
                if (null != mUserCallback) {
                    mUserCallback.onPilotLogout((LogoutResponse) object);
                }
            } else if (object instanceof SettingsResponse) {
                if (null != mUserCallback) {
                    SettingsResponse settingsResponse = (SettingsResponse) object;
                    AppPreferences.saveSettingsData(mContext, settingsResponse.getData());
                }
            } else if (object instanceof GetCitiesResponse) {
                if (null != mUserCallback) {
                    AppPreferences.setAvailableCities(mContext, (GetCitiesResponse) object);
                    mUserCallback.onCitiesResponse(((GetCitiesResponse) object));
                }
            } else if (object instanceof PilotStatusResponse) {
                mUserCallback.onUpdateStatus((PilotStatusResponse) object);
            } else if (object instanceof AckCallResponse) {
                mUserCallback.onAck(((AckCallResponse) object).getMessage());
            } else if (object instanceof HeatMapResponse) {
                mUserCallback.getHeatMap((HeatMapResponse) object);
            } else if (object instanceof UpdateProfileResponse) {
                mUserCallback.onUpdateProfile((UpdateProfileResponse) object);
            } else if (object instanceof WalletHistoryResponse) {
                mUserCallback.getWalletData((WalletHistoryResponse) object);
            } else if (object instanceof AccountNumbersResponse) {
                mUserCallback.getAccountNumbers((AccountNumbersResponse) object);
            } else if (object instanceof ContactNumbersResponse) {
                mUserCallback.getContactNumbers((ContactNumbersResponse) object);
            } else if (object instanceof CheckDriverStatusResponse) {
                mUserCallback.onRunningTrips((CheckDriverStatusResponse) object);
            } else if (object instanceof TripHistoryResponse) {
                mUserCallback.onGetTripHistory((TripHistoryResponse) object);
            } else if (object instanceof TripMissedHistoryResponse) {
                mUserCallback.onGetMissedTripHistory((TripMissedHistoryResponse) object);
            } else if (object instanceof GeocoderApi) {
                mUserCallback.onReverseGeocode((GeocoderApi) object);
            } else if (object instanceof CancelRideResponse) {
                mUserCallback.onCancelRide((CancelRideResponse) object);
            } else if (object instanceof FreeDriverResponse) {
                mUserCallback.onFreeDriver((FreeDriverResponse) object);
            } else if (object instanceof UploadAudioFile) {
                mUserCallback.onUploadAudioFile((UploadAudioFile) object);
            } else if (object instanceof UploadDocumentFile) {
                mUserCallback.onUploadFile((UploadDocumentFile) object);
            } else if (object instanceof ForgotPasswordResponse) {
                mUserCallback.onForgotPassword((ForgotPasswordResponse) object);
            } else if (object instanceof VerifyCodeResponse) {
                mUserCallback.onCodeVerification((VerifyCodeResponse) object);
            } else if (object instanceof VerifyNumberResponse) {
                mUserCallback.onNumberVerification((VerifyNumberResponse) object);
            } else if (object instanceof AcceptCallResponse) {
                mUserCallback.onAcceptCall((AcceptCallResponse) object);
            } else if (object instanceof RejectCallResponse) {
                mUserCallback.onRejectCall((RejectCallResponse) object);
            } else if (object instanceof ArrivedResponse) {
                mUserCallback.onArrived((ArrivedResponse) object);
            } else if (object instanceof BeginRideResponse) {
                mUserCallback.onBeginRide((BeginRideResponse) object);
            } else if (object instanceof EndRideResponse) {
                mUserCallback.onEndRide((EndRideResponse) object);
            } else if (object instanceof FeedbackResponse) {
                mUserCallback.onFeedback((FeedbackResponse) object);
            } else if (object instanceof ConversationResponse) {
                mUserCallback.onGetConversations((ConversationResponse) object);
            } else if (object instanceof SendMessageResponse) {
                mUserCallback.onSendMessage((SendMessageResponse) object);
            } else if (object instanceof ConversationChatResponse) {
                mUserCallback.onGetConversationChat((ConversationChatResponse) object);
            } else if (object instanceof UpdateConversationStatusResponse) {
                mUserCallback.onUpdateConversationStatus((UpdateConversationStatusResponse) object);
            } else if (object instanceof GetConversationIdResponse) {
                mUserCallback.onGetConversationId((GetConversationIdResponse) object);
            } else if (object instanceof ServiceTypeResponse) {
                mUserCallback.onGetServiceTypes((ServiceTypeResponse) object);
            } else if (object instanceof ChangePinResponse) {
                if (null != mUserCallback) {
                    mUserCallback.onChangePinResponse(((ChangePinResponse) object));
                }
            } else if (object instanceof GetProfileResponse) {
                if (null != mUserCallback) {
                    mUserCallback.onGetProfileResponse(((GetProfileResponse) object));
                }
            } else if (object instanceof DriverStatsResponse) {
                if (null != mUserCallback) {
                    mUserCallback.onDriverStatsResponse(((DriverStatsResponse) object));
                }
            } else if (object instanceof UpdateDropOffResponse) {
                if (null != mUserCallback) {
                    mUserCallback.onUpdateDropOff(((UpdateDropOffResponse) object));
                }
            } else if (object instanceof CommonResponse) {
                mUserCallback.onCommonResponse((CommonResponse) object);
            }
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(int errorCode, String error) {
            mUserCallback.onError(errorCode, error);
        }

    };


}
