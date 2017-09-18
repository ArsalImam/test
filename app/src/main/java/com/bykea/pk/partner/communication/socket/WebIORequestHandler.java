package com.bykea.pk.partner.communication.socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.utils.HTTPStatus;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.Notifications;
import com.bykea.pk.partner.communication.IResponseCallback;
import com.bykea.pk.partner.models.ReceivedMessage;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.AckCallResponse;
import com.bykea.pk.partner.models.response.ArrivedResponse;
import com.bykea.pk.partner.models.response.BeginRideResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.ConversationChatResponse;
import com.bykea.pk.partner.models.response.ConversationResponse;
import com.bykea.pk.partner.models.response.EndRideResponse;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.FreeDriverResponse;
import com.bykea.pk.partner.models.response.GetConversationIdResponse;
import com.bykea.pk.partner.models.response.HeatMapResponse;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.NormalCallData;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.models.response.SendMessageResponse;
import com.bykea.pk.partner.models.response.UpdateConversationStatusResponse;
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class WebIORequestHandler {

    private static Context mContext;
    //    private String mSocket;
    private static WebIORequestHandler mWebIORequestHandler = new WebIORequestHandler();
    //    private static AdvanceCallListener advanceCallListener = new AdvanceCallListener();
//    private static JobCallListener jobCallListener = new JobCallListener();
    private static ChatMessageListener chatMessageListener = new ChatMessageListener();

    public static WebIORequestHandler getInstance() {
        if (null == mWebIORequestHandler) {
            mWebIORequestHandler = new WebIORequestHandler();
        }
//        if (null == advanceCallListener) advanceCallListener = new AdvanceCallListener();
//        if (null == jobCallListener) jobCallListener = new JobCallListener();
        return mWebIORequestHandler;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private WebIORequestHandler() {
    }

    public void registerChatListener() {
        Utils.redLog("RECEIVE CHAT LISTENER ON", "CALLED...");
        WebIO.getInstance().on(ApiTags.SOCKET_RECEIVE_CHAT_MESSAGE, chatMessageListener);
    }

    public void unRegisterChatListener() {
        Utils.redLog("RECEIVE CHAT LISTENER OFF", "CALLED...");
        WebIO.getInstance().off(ApiTags.SOCKET_RECEIVE_CHAT_MESSAGE, chatMessageListener);
    }

    public synchronized void requestHeatmap(final JSONObject jsonObject, IResponseCallback onResponseCallBack) {
        emitWithJObject(ApiTags.SOCKET_GET_HEATMAP_DATA, new MyGenericListener(ApiTags.SOCKET_GET_HEATMAP_DATA, HeatMapResponse.class, onResponseCallBack),
                jsonObject);
    }

    public synchronized void requestLocationUpdate(final JSONObject jsonObject, IResponseCallback onResponseCallBack) {
        emitWithJObject(ApiTags.SOCKET_UPDATE_DRIVER_LOC, new LocationUpdateListener(ApiTags.SOCKET_UPDATE_DRIVER_LOC, LocationResponse.class, onResponseCallBack),
                jsonObject);
    }

    public void requestFreePilot(final JSONObject jsonObject, IResponseCallback onResponseCallBack) {
        emitWithJObject(ApiTags.SOCKET_FREE_PILOT, new MyGenericListener(ApiTags.SOCKET_FREE_PILOT, FreeDriverResponse.class,
                onResponseCallBack), jsonObject);
    }

    public void acceptNormalCall(JSONObject acceptCallData, IResponseCallback responseCallBack) {
        emitWithJObject(ApiTags.SOCKET_ACCEPT_CALL, new MyGenericListener(ApiTags.SOCKET_ACCEPT_CALL, AcceptCallResponse.class,
                responseCallBack), acceptCallData);
    }

    public void ackCall(JSONObject acceptCallData, IResponseCallback responseCallBack) {
        emitWithJObject(ApiTags.ACK_CALL, new MyGenericListener(ApiTags.ACK_CALL, AckCallResponse.class,
                responseCallBack), acceptCallData);
    }

    public void rejectNormalCall(JSONObject rejectCallData, IResponseCallback responseCallBack) {
        emitWithJObject(ApiTags.SOCKET_REJECT_CALL, new MyGenericListener(ApiTags.SOCKET_REJECT_CALL, RejectCallResponse.class,
                responseCallBack), rejectCallData);
    }

    public void driverArrived(JSONObject driverArrivedObject, IResponseCallback responseCallBack) {
        emitWithJObject(ApiTags.SOCKET_ARRIVED, new MyGenericListener(ApiTags.SOCKET_ARRIVED, ArrivedResponse.class,
                responseCallBack), driverArrivedObject);
    }

    public void beginRide(JSONObject beginTripData, IResponseCallback responseCallBack) {
        emitWithJObject(ApiTags.SOCKET_BEGIN_TRIP, new MyGenericListener(ApiTags.SOCKET_BEGIN_TRIP, BeginRideResponse.class,
                responseCallBack), beginTripData);
    }

    public void cancelRide(JSONObject cancelRideObject, IResponseCallback responseCallBack) {
        Utils.redLog(Constants.APP_NAME + " CancelRideEmit ", cancelRideObject.toString());
        emitWithJObject(ApiTags.SOCKET_CANCEL_RIDE_DRIVER, new MyGenericListener(ApiTags.SOCKET_CANCEL_RIDE_DRIVER, CancelRideResponse.class, responseCallBack),
                cancelRideObject);
    }

    public void endRide(JSONObject endRideData, IResponseCallback responseCallBack) {
        Utils.redLog(Constants.APP_NAME + " FinishRideEmit ", endRideData.toString());
        emitWithJObject(ApiTags.SOCKET_END_TRIP, new MyGenericListener(ApiTags.SOCKET_END_TRIP, EndRideResponse.class, responseCallBack),
                endRideData);
    }

    public void feedback(JSONObject feedbackData, IResponseCallback responseCallBack) {
        Utils.redLog(Constants.APP_NAME + " FinishRideEmit ", feedbackData.toString());
        emitWithJObject(ApiTags.SOCKET_DRIVER_FEEDBACK, new MyGenericListener(ApiTags.SOCKET_DRIVER_FEEDBACK, FeedbackResponse.class, responseCallBack),
                feedbackData);
    }


    public void getConversationChat(final IResponseCallback mResponseCallBack,
                                    JSONObject jsonObject) {

        Utils.redLog(Constants.APP_NAME + " getConversationChat ", jsonObject.toString());
        emitWithJObject(ApiTags.SOCKET_GET_CONVERSATION, new MyGenericListener(ApiTags.SOCKET_GET_CONVERSATION, ConversationChatResponse.class, mResponseCallBack)
                , jsonObject);
    }

    public void updatePilotStatus(final IResponseCallback mResponseCallBack,
                                  JSONObject jsonObject) {
        Utils.redLog(Constants.APP_NAME + " updateStatus ", jsonObject.toString());
        emitWithJObject(ApiTags.SOCKET_UPDATE_STATUS, new MyGenericListener(ApiTags.SOCKET_UPDATE_STATUS, PilotStatusResponse.class, mResponseCallBack)
                , jsonObject);
    }

    public void getConversationId(final IResponseCallback mResponseCallBack,
                                  JSONObject jsonObject) {
        Utils.redLog(Constants.APP_NAME + " getConversationId ", jsonObject.toString());
        emitWithJObject(ApiTags.SOCKET_GET_CONVERSATION_ID, new MyGenericListener(ApiTags.SOCKET_GET_CONVERSATION_ID, GetConversationIdResponse.class, mResponseCallBack)
                , jsonObject);
    }

    public void sendMessage(final IResponseCallback mResponseCallBack, JSONObject jsonObject) {
        Utils.redLog(Constants.APP_NAME + " sendMessage ", jsonObject.toString());
        emitWithJObject(ApiTags.SOCKET_SEND_CHAT_MESSAGE, new MyGenericListener(ApiTags.SOCKET_SEND_CHAT_MESSAGE, SendMessageResponse.class, mResponseCallBack)
                , jsonObject);
    }

    public void getDriverStats(final IResponseCallback mResponseCallBack, JSONObject jsonObject) {
        Utils.redLog(ApiTags.SOCKET_GET_DRIVER_STATS, jsonObject.toString());
        emitWithJObject(ApiTags.SOCKET_GET_DRIVER_STATS, new MyGenericListener(ApiTags.SOCKET_GET_DRIVER_STATS, DriverStatsResponse.class, mResponseCallBack)
                , jsonObject);
    }


    public void updateDropOff(final IResponseCallback mResponseCallBack, JSONObject jsonObject) {
        emitWithJObject(ApiTags.UPDATE_DROP_OFF, new MyGenericListener(ApiTags.UPDATE_DROP_OFF, UpdateDropOffResponse.class, mResponseCallBack),
                jsonObject);
    }


    private synchronized void emitWithJObject(final String socket, LocationUpdateListener locationUpdateListener, final JSONObject json) {
        WebIO.getInstance().on(socket, locationUpdateListener);
        if (!WebIO.getInstance().emitLocation(socket, json)) {
            WebIO.getInstance().onConnect(new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        //To avoid previous calls with wrong token_id
                        if (json.getString("token_id").equalsIgnoreCase(AppPreferences.getAccessToken(DriverApp.getContext()))) {
                            WebIO.getInstance().emitLocation(socket, json);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utils.redLog(" Socket Was disconnected and connect again" +
                            " to Update Location : , location emition completed.....", json.toString());
                }
            });
        } else {
            Utils.redLog(" Socket connected , Location emition completed.....", json.toString());

        }
    }

    private void emitWithJObject(final String eventName, MyGenericListener myGenericListener, final JSONObject json) {
        Utils.redLog("Request at" + eventName, json.toString());
        WebIO.getInstance().on(eventName, myGenericListener);
        if (!WebIO.getInstance().emit(eventName, json)) {
            WebIO.getInstance().onConnect(new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        //To avoid previous calls with wrong token_id
                        if (json.getString("token_id").equalsIgnoreCase(AppPreferences.getAccessToken(DriverApp.getContext()))) {
                            WebIO.getInstance().emit(eventName, json);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utils.redLog(eventName + " Socket Was disconnected and connect again" +
                            " to emit json object : , generice emition completed..... ", json.toString());
                }
            });
        } else {
            Utils.redLog(eventName + " Socket connected , generice emition completed..... ", json.toString());

        }
    }


    private class MyGenericListener implements Emitter.Listener, SetDataModel {
        private Class<?> dataModelClass;
        private String mSocketName;
        private IResponseCallback mOnResponseCallBack;

        public MyGenericListener(String socketName, Class<?> a, IResponseCallback onResponseCallBack) {
            setDataModel(a);
            mOnResponseCallBack = onResponseCallBack;
            mSocketName = socketName;
        }

        @Override
        public void call(Object... args) {
            String serverResponse = args[0].toString();
            Utils.redLog("RESPONSE at " + mSocketName, serverResponse);
            Gson gson = new Gson();
            try {
                Object commonResponse = gson.fromJson(serverResponse, dataModelClass);
                if (commonResponse instanceof AcceptCallResponse) {
                    Utils.infoLog(" ACCEPT CALL RESPONSE....", serverResponse);
                    AcceptCallResponse response = (AcceptCallResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                        registerChatListener();
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof RejectCallResponse) {
                    Utils.infoLog(" REJECT CALL RESPONSE....", serverResponse);
                    RejectCallResponse response = (RejectCallResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof FreeDriverResponse) {
                    Utils.infoLog(" FREE DRIVER RESPONSE....", serverResponse);
                    FreeDriverResponse response = (FreeDriverResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof HeatMapResponse) {
                    Utils.infoLog("HEAT MAP DATA RESPONSE....", serverResponse);
                    HeatMapResponse response = (HeatMapResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof ArrivedResponse) {
                    Utils.infoLog("ARRIVED RESPONSE....", serverResponse);
                    ArrivedResponse response = (ArrivedResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof BeginRideResponse) {
                    Utils.infoLog("BEGIN RIDE RESPONSE....", serverResponse);
                    BeginRideResponse response = (BeginRideResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof EndRideResponse) {
                    Utils.infoLog("END RIDE RESPONSE....", serverResponse);
                    EndRideResponse response = (EndRideResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                        getInstance().unRegisterChatListener();
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof FeedbackResponse) {
                    Utils.infoLog("FEEDBACK RESPONSE....", serverResponse);
                    FeedbackResponse response = (FeedbackResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof ConversationResponse) {
                    Utils.infoLog("GET ALL CONVERSATION RESPONSE....", serverResponse);
                    ConversationResponse response = (ConversationResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof SendMessageResponse) {
                    Utils.infoLog("SEND MESSAGE RESPONSE....", serverResponse);
                    SendMessageResponse response = (SendMessageResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof ConversationChatResponse) {
                    Utils.infoLog("CONVERSATION CHAT RESPONSE....", serverResponse);
                    ConversationChatResponse response = (ConversationChatResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof UpdateConversationStatusResponse) {
                    Utils.infoLog("UPDATE CONVERSATION RESPONSE....", serverResponse);
                    UpdateConversationStatusResponse response = (UpdateConversationStatusResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof GetConversationIdResponse) {
                    Utils.infoLog("GET CONVERSATION ID RESPONSE....", serverResponse);
                    GetConversationIdResponse response = (GetConversationIdResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof CancelRideResponse) {
                    Utils.infoLog("CANCEL CALL RESPONSE....", serverResponse);
                    CancelRideResponse response = (CancelRideResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof AckCallResponse) {
                    Utils.infoLog("Ack CALL RESPONSE....", serverResponse);
                    AckCallResponse response = (AckCallResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof PilotStatusResponse) {
                    Utils.infoLog("Update STATUS....", serverResponse);
                    PilotStatusResponse response = (PilotStatusResponse) commonResponse;
                    mOnResponseCallBack.onResponse(response);
                     /*else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }*/
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof DriverStatsResponse) {
                    Utils.infoLog("Updated STATS....", serverResponse);
                    DriverStatsResponse response = (DriverStatsResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                } else if (commonResponse instanceof UpdateDropOffResponse) {
                    UpdateDropOffResponse response = (UpdateDropOffResponse) commonResponse;
                    if (response.isSuccess()) {
                        mOnResponseCallBack.onResponse(response);
                    } else {
                        mOnResponseCallBack.onError(response.getCode(), response.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setDataModel(Class<?> a) {
            dataModelClass = a;
        }
    }


    private class LocationUpdateListener implements Emitter.Listener, SetDataModel {
        private Class<?> dataModelClass;
        private String mSocketName;
        private IResponseCallback mOnResponseCallBack;

        public LocationUpdateListener(String socketName, Class<?> a, IResponseCallback onResponseCallBack) {
            setDataModel(a);
            mOnResponseCallBack = onResponseCallBack;
            mSocketName = socketName;
        }

        @Override
        public void call(Object... args) {
            String serverResponse = args[0].toString();
            Utils.redLog("Update location Response", serverResponse);

            Gson gson = new Gson();
            try {
                LocationResponse locationResponse = gson.fromJson(serverResponse, LocationResponse.class);
                if (null == mContext) {
                    mContext = DriverApp.getContext();
                }
                if (AppPreferences.isLoggedIn(mContext) && locationResponse.getData() != null) {
                    if (StringUtils.isNotBlank(locationResponse.getData().getLat())
                            && StringUtils.isNotBlank(locationResponse.getData().getLng())) {
                        AppPreferences.saveLastUpdatedLocation(mContext,
                                new LatLng(Double.parseDouble(locationResponse.getData().getLat()),
                                        Double.parseDouble(locationResponse.getData().getLng())));
                    }
                    Utils.saveServerTimeDifference(mContext, locationResponse.getTimestampserver());
                }
                if (locationResponse.isSuccess()) {
                    if (AppPreferences.isWalletAmountIncreased(mContext)) {
                        AppPreferences.setWalletAmountIncreased(mContext, false);
                        AppPreferences.setAvailableStatus(mContext, true);
                    }
                    if (AppPreferences.isOutOfFence(mContext)) {
                        AppPreferences.setOutOfFence(mContext, false);
                        AppPreferences.setAvailableStatus(mContext, true);
                        mOnResponseCallBack.onError(HTTPStatus.FENCE_SUCCESS, locationResponse.getMessage());
                    } else {
                        mOnResponseCallBack.onResponse(locationResponse);
                    }
                } else {
                    mOnResponseCallBack.onError(locationResponse.getCode(), locationResponse.getMessage());
                }
                WebIO.getInstance().off(mSocketName, LocationUpdateListener.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setDataModel(Class<?> a) {
            dataModelClass = a;
        }
    }

    private static class ChatMessageListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            String serverResponse = args[0].toString();
            Utils.infoLog("CHAT MESSAGE RECEIVED....", serverResponse);

            Gson gson = new Gson();
            if (null == mContext) {
                mContext = DriverApp.getContext();
            }
            ReceivedMessage receivedMessage = gson.fromJson(serverResponse, ReceivedMessage.class);
            try {
                if (AppPreferences.isOnTrip(mContext)) {

                    if (!AppPreferences.getLastMessageID(mContext)
                            .equalsIgnoreCase(receivedMessage.getData().getMessageId())) {
                        if (!AppPreferences.isChatActivityOnForeground(mContext)) {
                            Notifications.createChatNotification(mContext, receivedMessage);
                        }
                        Intent intent = new Intent(Keys.BROADCAST_MESSAGE_RECEIVE);
                        intent.putExtra("action", Keys.BROADCAST_MESSAGE_RECEIVE);
                        intent.putExtra("msg", receivedMessage);
                        ((Activity) mContext).sendBroadcast(intent);
                        AppPreferences.setLastMessageID(mContext, receivedMessage.getData().getMessageId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

/*

    public static class ConnectionListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
//            Utils.redLog(ConstantKeys.APP_NAME + "  ########################    ", "Socket Connection Established....");

         *//**//*   mAccessTokenListener = new AccessTokenListener();
            mJobCallListener = new WebIORequestHandler.JobCallListener();
            mAdvanceJobCallListener = new WebIORequestHandler.AdvanceCallListener();
            WebIO.on(ApiTags.SOCKET_ACCESS_TOKEN, mAccessTokenListener);
            WebIO.on(ApiTags.SOCKET_PASSENGER_CALL, mJobCallListener);
            WebIO.on(ApiTags.SOCKET_ADVANCE_CALL, mAdvanceJobCallListener);*//**//*
            WebIORequestHandler.getInstance().registerCallListener();
            WebIORequestHandler.getInstance().registerAdvanceCallListener();
        }
    }*/

    public static class JobCallListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            String serverResponse = args[0].toString();
            Utils.redLog(Constants.APP_NAME + " TRIP NOTIFICATION LISTENER +++++++++++++++++++ ", serverResponse);

            Gson gson = new Gson();
            try {
                if (null == mContext) {
                    mContext = DriverApp.getContext();
                }
                NormalCallData normalCallData = gson.fromJson(serverResponse, NormalCallData.class);
                if (normalCallData.getStatus().equalsIgnoreCase(TripStatus.ON_CALLING) && normalCallData.isSuccess()) {
                    Utils.redLog(Constants.APP_NAME, " NORMAL CALLING....");
                   /* if (AppPreferences.isIncomingCall(mContext)) {*/
                    if (AppPreferences.getAvailableStatus(mContext) && Utils.isGpsEnable(mContext)
                            && Utils.isNotDelayed(mContext, normalCallData.getData().getSentTime())) {
                        AppPreferences.setCallType(mContext, Keys.NORMAL_CALL);
                        AppPreferences.setIncomingCall(mContext, false);
                        AppPreferences.setCallData(mContext, normalCallData.getData());
                        ActivityStackManager.getInstance(mContext).startCallingActivity();
                    }

                   /* } else {
                        Utils.redLog(ConstantKeys.TAG_CALL, "Already On Job ============");
                    }*/
                } else if (normalCallData.getStatus().equalsIgnoreCase(TripStatus.ON_CANCEL_TRIP)) {
                    Utils.redLog(Constants.APP_NAME, " CANCEL CALLING Socket");
                    if (normalCallData.isSuccess() /*&& AppPreferences.isOnTrip(mContext)*/) {
                        Intent intent = new Intent(Keys.BROADCAST_CANCEL_RIDE);
                        intent.putExtra("action", Keys.BROADCAST_CANCEL_RIDE);
                        intent.putExtra("msg", normalCallData.getMessage());
                        Utils.setCallIncomingState(mContext);
                        if (AppPreferences.isJobActivityOnForeground(mContext) ||
                                AppPreferences.isCallingActivityOnForeground(mContext)) {
                            mContext.sendBroadcast(intent);
                        } else {
                            mContext.sendBroadcast(intent);
                            Notifications.createCancelNotification(mContext, "Passenger has cancelled the Trip", 23);
                        }
                        getInstance().unRegisterChatListener();
                    } else {
                        Utils.appToastDebug(mContext, normalCallData.getMessage());
                    }
                } else if (StringUtils.isNotBlank(normalCallData.getData().getEndAddress()) &&
                        !normalCallData.getData().getEndAddress().equalsIgnoreCase(AppPreferences.getCallData(mContext).getEndAddress())) {
                    NormalCallData callData = AppPreferences.getCallData(mContext);
                    callData.setEndAddress(normalCallData.getData().getEndAddress());
                    callData.setEndLat(normalCallData.getData().getEndLat());
                    callData.setEndLng(normalCallData.getData().getEndLng());
                    callData.setStatus(normalCallData.getData().getStatus());
                    AppPreferences.setCallData(mContext, callData);
                    Intent intent = new Intent(Keys.BROADCAST_DROP_OFF_UPDATED);
                    intent.putExtra("action", Keys.BROADCAST_DROP_OFF_UPDATED);
                    mContext.sendBroadcast(intent);
                } else {
                    Utils.redLog(Constants.TAG_CALL, normalCallData.getMessage() + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private interface SetDataModel {
        void setDataModel(Class<?> a);
    }


}
