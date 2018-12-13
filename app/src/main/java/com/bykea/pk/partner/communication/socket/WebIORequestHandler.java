package com.bykea.pk.partner.communication.socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryCallDriverResponse;
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
import com.bykea.pk.partner.ui.helpers.ActivityStackManager;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Keys;
import com.bykea.pk.partner.utils.TripStatus;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WebIORequestHandler {
    private static WebIORequestHandler mWebIORequestHandler = new WebIORequestHandler();
    private static ChatMessageListener chatMessageListener = new ChatMessageListener();
//    private static Context mContext;

    public static WebIORequestHandler getInstance() {
        if (null == mWebIORequestHandler) {
            mWebIORequestHandler = new WebIORequestHandler();
        }
        return mWebIORequestHandler;
    }

//    public void setContext(Context context) {
//        mContext = context;
//    }

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
//        Utils.redLog(Constants.APP_NAME + " CancelRideEmit ", cancelRideObject.toString());
        emitWithJObject(ApiTags.SOCKET_CANCEL_RIDE_DRIVER, new MyGenericListener(ApiTags.SOCKET_CANCEL_RIDE_DRIVER, CancelRideResponse.class, responseCallBack),
                cancelRideObject);
    }

    public void endRide(JSONObject endRideData, IResponseCallback responseCallBack) {
//        Utils.redLog(Constants.APP_NAME + " FinishRideEmit ", endRideData.toString());
        emitWithJObject(ApiTags.SOCKET_END_TRIP, new MyGenericListener(ApiTags.SOCKET_END_TRIP, EndRideResponse.class, responseCallBack),
                endRideData);
    }

    public void feedback(JSONObject feedbackData, IResponseCallback responseCallBack) {
//        Utils.redLog(Constants.APP_NAME + " FinishRideEmit ", feedbackData.toString());
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
//        Utils.redLog(Constants.APP_NAME + " updateStatus ", jsonObject.toString());
        emitWithJObject(ApiTags.SOCKET_UPDATE_STATUS, new MyGenericListener(ApiTags.SOCKET_UPDATE_STATUS, PilotStatusResponse.class, mResponseCallBack)
                , jsonObject);
    }

    public void getConversationId(final IResponseCallback mResponseCallBack,
                                  JSONObject jsonObject) {
//        Utils.redLog(Constants.APP_NAME + " getConversationId ", jsonObject.toString());
        emitWithJObject(ApiTags.SOCKET_GET_CONVERSATION_ID, new MyGenericListener(ApiTags.SOCKET_GET_CONVERSATION_ID, GetConversationIdResponse.class, mResponseCallBack)
                , jsonObject);
    }

    public void sendMessage(final IResponseCallback mResponseCallBack, JSONObject jsonObject) {
//        Utils.redLog(Constants.APP_NAME + " sendMessage ", jsonObject.toString());
        emitWithJObject(ApiTags.SOCKET_SEND_CHAT_MESSAGE, new MyGenericListener(ApiTags.SOCKET_SEND_CHAT_MESSAGE, SendMessageResponse.class, mResponseCallBack)
                , jsonObject);
    }

    public void getDriverStats(final IResponseCallback mResponseCallBack, JSONObject jsonObject) {
        emitWithJObject(ApiTags.SOCKET_GET_DRIVER_STATS, new MyGenericListener(ApiTags.SOCKET_GET_DRIVER_STATS, DriverStatsResponse.class, mResponseCallBack)
                , jsonObject);
    }


    public void updateDropOff(final IResponseCallback mResponseCallBack, JSONObject jsonObject) {
        emitWithJObject(ApiTags.UPDATE_DROP_OFF, new MyGenericListener(ApiTags.UPDATE_DROP_OFF, UpdateDropOffResponse.class, mResponseCallBack),
                jsonObject);
    }

    //region Multi delivery


    private synchronized void emitWithJObject(final String socket, LocationUpdateListener locationUpdateListener, final JSONObject json) {
        WebIO.getInstance().on(socket, locationUpdateListener);
        if (!WebIO.getInstance().emitLocation(socket, json)) {
            WebIO.getInstance().onConnect(new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        WebIO.getInstance().off(Socket.EVENT_CONNECT, this);
                        DriverApp.getApplication().attachListenersOnSocketConnected();
                        //To avoid previous calls with wrong token_id
                        if (json.getString("token_id").equalsIgnoreCase(AppPreferences.getAccessToken())) {
                            WebIO.getInstance().emitLocation(socket, json);
                        }
                        Utils.redLog("Request at " + socket + " (onConnect)", json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Utils.redLog("Request at " + socket, json.toString());
        }
    }

    private void emitWithJObject(final String eventName, MyGenericListener myGenericListener, final JSONObject json) {
        WebIO.getInstance().on(eventName, myGenericListener);
        if (!WebIO.getInstance().emit(eventName, json)) {
            WebIO.getInstance().onConnect(new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        WebIO.getInstance().off(Socket.EVENT_CONNECT, this);
                        DriverApp.getApplication().attachListenersOnSocketConnected();
                        //To avoid previous calls with wrong token_id
                        if (json.getString("token_id").equalsIgnoreCase(AppPreferences.getAccessToken())) {
                            WebIO.getInstance().emit(eventName, json);
                        }
                        Utils.redLog("Request at " + eventName + " (onConnect)", json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Utils.redLog("Request at " + eventName, json.toString());
        }
    }


    private class MyGenericListener implements Emitter.Listener {
        private Class<?> dataModelClass;
        private String mSocketName;
        private IResponseCallback mOnResponseCallBack;

        MyGenericListener(String socketName, Class<?> a, IResponseCallback onResponseCallBack) {
            dataModelClass = a;
            mOnResponseCallBack = onResponseCallBack;
            mSocketName = socketName;
        }

        @Override
        public void call(Object... args) {
            String serverResponseJsonString = args[0].toString();
            Utils.redLog("Response at " + mSocketName, serverResponseJsonString);
            Gson gson = new Gson();
            try {
                Object serverResponse = gson.fromJson(serverResponseJsonString, dataModelClass);
                if (serverResponse instanceof CommonResponse) {
                    CommonResponse commonResponse = (CommonResponse) serverResponse;
                    if (commonResponse.isSuccess() || serverResponse instanceof PilotStatusResponse) {
                        mOnResponseCallBack.onResponse(serverResponse);
                    } else {
                        mOnResponseCallBack.onError(commonResponse.getCode(), commonResponse.getMessage());
                    }
                    WebIO.getInstance().off(mSocketName, MyGenericListener.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class LocationUpdateListener implements Emitter.Listener {
        private String mSocketName;
        private IResponseCallback mOnResponseCallBack;

        LocationUpdateListener(String socketName, Class<?> a, IResponseCallback onResponseCallBack) {
            mOnResponseCallBack = onResponseCallBack;
            mSocketName = socketName;
        }

        @Override
        public void call(Object... args) {
            String serverResponse = args[0].toString();
            Utils.redLog("Response at " + mSocketName, serverResponse);

            Gson gson = new Gson();
            try {
                LocationResponse locationResponse = gson.fromJson(serverResponse, LocationResponse.class);
//                if (null == mContext) {
//                    mContext = DriverApp.getContext();
//                }
                if (AppPreferences.isLoggedIn() && locationResponse.getData() != null) {
                    if (StringUtils.isNotBlank(locationResponse.getData().getLat())
                            && StringUtils.isNotBlank(locationResponse.getData().getLng())) {
                        AppPreferences.saveLastUpdatedLocation(
                                new LatLng(Double.parseDouble(locationResponse.getData().getLat()),
                                        Double.parseDouble(locationResponse.getData().getLng())));
                    }
                    Utils.saveServerTimeDifference(locationResponse.getTimestampserver());
                }
                if (locationResponse.isSuccess()) {
                    if (AppPreferences.isWalletAmountIncreased()) {
                        AppPreferences.setWalletAmountIncreased(false);
                        AppPreferences.setAvailableStatus(true);
                    }
                    if (AppPreferences.isOutOfFence()) {
                        AppPreferences.setOutOfFence(false);
                        AppPreferences.setAvailableStatus(true);
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
    }

    private static class ChatMessageListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            String serverResponse = args[0].toString();
            Utils.redLog("CHAT MESSAGE RECEIVED....", serverResponse);

            Gson gson = new Gson();
//            if (null == mContext) {
//                mContext = DriverApp.getContext();
//            }
            ReceivedMessage receivedMessage = gson.fromJson(serverResponse, ReceivedMessage.class);
            try {
                if (AppPreferences.isOnTrip()) {

                    if (!AppPreferences.getLastMessageID()
                            .equalsIgnoreCase(receivedMessage.getData().getMessageId())) {
                        if (!AppPreferences.isChatActivityOnForeground()) {
                            Notifications.createChatNotification(DriverApp.getContext(), receivedMessage);
                        }
                        Intent intent = new Intent(Keys.BROADCAST_MESSAGE_RECEIVE);
                        intent.putExtra("action", Keys.BROADCAST_MESSAGE_RECEIVE);
                        intent.putExtra("msg", receivedMessage);
//                        DriverApp.getContext().sendBroadcast(intent);
                        EventBus.getDefault().post(intent);
                        AppPreferences.setLastMessageID(receivedMessage.getData().getMessageId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static class JobCallListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            String serverResponse = args[0].toString();
            Utils.redLog("TRIP NOTIFICATION (Socket) ", serverResponse);

            Gson gson = new Gson();
            try {
//                if (null == mContext) {
//                    mContext = DriverApp.getContext();
//                }
                NormalCallData normalCallData = gson.fromJson(serverResponse, NormalCallData.class);
                if (normalCallData.getStatus().equalsIgnoreCase(TripStatus.ON_CALLING) && normalCallData.isSuccess()) {
                    ActivityStackManager.getInstance().startCallingActivity(normalCallData, false, DriverApp.getContext());
                } else if (normalCallData.getStatus().equalsIgnoreCase(TripStatus.ON_CANCEL_TRIP)) {
                    if (normalCallData.isSuccess() && AppPreferences.getAvailableStatus()) {

                        /*
                        * when Gps is off, we don't show Calling Screen so we don't need to show
                        * Cancel notification either if passenger cancels it before booking.
                        * If passenger has cancelled it after booking we will entertain this Cancel notification
                        * */

                        if (Utils.isGpsEnable(DriverApp.getContext()) || AppPreferences.isOnTrip()) {
                            Intent intent = new Intent(Keys.BROADCAST_CANCEL_RIDE);
                            intent.putExtra("action", Keys.BROADCAST_CANCEL_RIDE);
                            intent.putExtra("msg", normalCallData.getMessage());
                            Utils.setCallIncomingState();
                            if (AppPreferences.isJobActivityOnForeground() ||
                                    AppPreferences.isCallingActivityOnForeground()) {
//                                DriverApp.getContext().sendBroadcast(intent);
                                EventBus.getDefault().post(intent);
                            } else {
                                EventBus.getDefault().post(intent);
//                                DriverApp.getContext().sendBroadcast(intent);
                                Notifications.createCancelNotification(DriverApp.getContext(), "Passenger has cancelled the Trip", 23);
                            }
                            getInstance().unRegisterChatListener();
                        }
                    } else {
                        Utils.appToastDebug(DriverApp.getContext(), normalCallData.getMessage());
                    }
                } else {
                    Utils.updateTripData(normalCallData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Call Driver listener for listening multiple delivery request
     */
    public static class CallDriverListener implements Emitter.Listener {

        @Override
        public void call(Object... args) {
            String serverResponse = args[0].toString();
            Gson gson = new Gson();
            try{
                MultiDeliveryCallDriverResponse response = gson.fromJson(
                        serverResponse,
                        MultiDeliveryCallDriverResponse.class);
                if (response != null) {
                    ActivityStackManager
                            .getInstance()
                            .startMultiDeliveryCallingActivity(
                                    response,
                                    false,
                                    DriverApp.getContext()
                            );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
