package com.bykea.pk.partner.communication.socket;

import android.content.Context;


import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import java.net.URISyntaxException;

import javax.net.ssl.SSLContext;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;


public class WebIO {

    private Socket mSocket;
    private static WebIO mWebIO;
    private static Context mContext;

    /*  private WebIO() {
          try {

              IO.Options options = new IO.Options();
  //            String token = AppPreferences.getAccessToken(mContext);
              options.query = "appName=Terminal&AppId=7002738&AppSecret=cI790Mf";
              options.timeout = 5 * 1000;

              mSocket = IO.socket(ApiTags.SOCKET_BASE_SERVER_URL, options);

          } catch (URISyntaxException e) {
              e.printStackTrace();
              mWebIO = null;
          }
      }
  */
    private WebIO() {
        try {

            IO.Options options = new IO.Options();
            //            String token = AppPreferences.getAccessToken(mContext);
            options.query = "appName=Terminal&AppId=7002738&AppSecret=cI790Mf&user_type=p";
            options.timeout = 15 * 1000;
            options.secure = true;
            options.forceNew = true;
            options.transports = new String[]{WebSocket.NAME, Polling.NAME};
            SSLContext sslContext = Utils.getSSLContext(DriverApp.getContext());
            if (sslContext != null) {
                options.sslContext = sslContext;
            }
            mSocket = IO.socket(ApiTags.BASE_SERVER_URL, options);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            mWebIO = null;
        }
    }

    public synchronized static WebIO getInstance() {
        if (mWebIO == null) {
            mWebIO = new WebIO();
        }
        return mWebIO;
    }

    public boolean isSocketConnected() {
        if (WebIO.getInstance() != null && WebIO.getInstance().getSocket() != null
                && WebIO.getInstance().getSocket().connected()) {
            return true;
        } else {
            return false;
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void resetAccessToken() {
        WebIO.getInstance().getSocket().close();
        mWebIO = new WebIO();
    }

    public void clearConnectionData() {
        WebIO.getInstance().getSocket().close();
        mWebIO = null;

    }

    public void onConnect(Emitter.Listener connectCallBack) {

        try {
            WebIO.getInstance().getSocket().on(Socket.EVENT_CONNECT, connectCallBack);
            WebIO.getInstance().getSocket().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean emit(String event, Object... params) {
        if (!WebIO.getInstance().isSocketConnected()) {
            return false;

        }
        WebIO.getInstance().getSocket().emit(event, params);
        return true;
    }

    public synchronized boolean emitLocation(String event, Object... params) {
        if (!WebIO.getInstance().isSocketConnected()) {
            return false;

        }
          /*if(BuildConfig.DEBUG){
              Log.d("emit","event:"+event+" params:"+params.toString());
          }*/
        if (AppPreferences.isLoggedIn()
                && (AppPreferences.getAvailableStatus() ||
                AppPreferences.isOutOfFence())) {
            AppPreferences.setLocationEmitTime();
            WebIO.getInstance().getSocket().emit(event, params);
        }
        return true;
    }


    public void on(String eventName, Emitter.Listener callBack) {
        if (isListenerRequired(eventName)) {
            WebIO.getInstance().getSocket().on(eventName, callBack);
        }
    }

    private boolean isListenerRequired(String eventName) {
        if (WebIO.getInstance() != null && WebIO.getInstance().getSocket() != null &&
                (WebIO.getInstance().getSocket().listeners(eventName).size() > 0)) {
            return false;
        } else {
            return true;
        }
    }

    public void once(String EventName, Emitter.Listener callBack) {
        WebIO.getInstance().getSocket().once(EventName, callBack);
    }

    public void off(String EventName, Emitter.Listener callBack) {

        WebIO.getInstance().getSocket().off(EventName, callBack);

    }

    /* public static void onError(Emitter.Listener callBack){
         WebIO.getInstance().getSocket().on(Socket.EVENT_CONNECT_TIMEOUT, callBack)
                 .on(Socket.EVENT_CONNECT_ERROR, callBack).on(Socket.EVENT_DISCONNECT, callBack);
     }

     public static void offError(Emitter.Listener callBack){
         WebIO.getInstance().getSocket().off(Socket.EVENT_CONNECT_TIMEOUT, callBack)
                 .off(Socket.EVENT_CONNECT_ERROR, callBack).off(Socket.EVENT_DISCONNECT, callBack);
     }*/
    public void onError(Emitter.Listener callBack) {
        WebIO.getInstance().getSocket().on(Socket.EVENT_ERROR, callBack)
//                .on(Socket.EVENT_CONNECT_ERROR, callBack)
                .on(Socket.EVENT_DISCONNECT, callBack);
        Dialogs.INSTANCE.dismissDialog();

    }

    public void offError(Emitter.Listener callBack) {
        WebIO.getInstance().getSocket().off(Socket.EVENT_ERROR, callBack)
//                .off(Socket.EVENT_CONNECT_ERROR, callBack)
                .off(Socket.EVENT_DISCONNECT, callBack);
        Dialogs.INSTANCE.dismissDialog();
    }
}
