package com.bykea.pk.partner.communication.socket;

import android.annotation.SuppressLint;


import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.DateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;


public class WebIO {

    private Socket mSocket;
    private static WebIO mWebIO;

    private static final String TAG = WebIO.class.getSimpleName();


    private WebIO() {
        try {

            IO.Options options = new IO.Options();
            options.query = "appName=Terminal&AppId=7002738&AppSecret=cI790Mf&user_type=p";
            options.timeout = 15 * 1000;
            options.secure = true;
            options.forceNew = true;
            options.transports = new String[]{WebSocket.NAME, Polling.NAME};
            //for socket io 0.8.2
                /*SSLContext sslContext = Utils.getSSLContext(PassengerApp.getContext());
                if (sslContext != null) {
                    options.sslContext = sslContext;
                }*/

            //for socket io 1.0.0
            addSslCertificates(options);
            mSocket = IO.socket(ApiTags.BASE_SERVER_URL, options);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            mWebIO = null;
        }
    }


    private void addSslCertificates(IO.Options options) {
        SSLContext sslContext = null;
        try {
            // loading CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = DriverApp.getContext().getResources().openRawResource(R.raw.star_bykea_net);
            Certificate ca;
            try {
                ca = cf.generateCertificate(cert);
            } finally {
                cert.close();
            }

            // creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // creating a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // creating an SSLSocketFactory that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(new HostnameVerifier() {
                        @SuppressLint("BadHostnameVerifier")
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                    .build();

// default settings for all sockets
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);

            options.callFactory = okHttpClient;
            options.webSocketFactory = okHttpClient;


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized static WebIO getInstance() {
        if (mWebIO == null) {
            mWebIO = new WebIO();
        }
        return mWebIO;
    }

    public boolean isSocketConnected() {
        if (getSocket() != null
                && getSocket().connected()) {
            Utils.redLogLocation(TAG, "isSocketConnected: true");
            return true;
        } else {
            Utils.redLogLocation(TAG, "isSocketConnected: false");
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
        try {
            if (getSocket() != null) {
                getSocket().off();
                getSocket().io().off();
                getSocket().disconnect();
                getSocket().close();
            }
            mWebIO = null;
            mSocket = null;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Utils.redLog("Socket", "Connection Cleared !");
    }


    private Emitter.Listener onTimeOutError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                Exception err = (Exception) args[0];
                //Utils.redLogLocation("onError", err.getMessage());
                //Utils.redLogLocation("onError", args[0].toString());
                Utils.redLog(TAG, "Socket Timeout onError: " + err.toString(), err);
                clearConnectionData();
            }
        }
    };

    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                Exception err = (Exception) args[0];
                //Utils.redLogLocation("onError", err.getMessage());
                Utils.redLog(TAG, "Socket onError: " + err.toString(), err);
                //clearConnectionData();
            }
        }
    };

    public void onConnect(Emitter.Listener connectCallBack) {

        try {
            if (!isConnectionListenerAttached()) {
                WebIO.getInstance().getSocket().on(Socket.EVENT_CONNECT, connectCallBack);
            }
            on(Socket.EVENT_CONNECT_TIMEOUT, onTimeOutError); //timeout
            on(Socket.EVENT_ERROR, onError);
            on(Socket.EVENT_CONNECT_ERROR, onError);
            on(Socket.EVENT_DISCONNECT, args -> Utils.redLogLocation(TAG, "Socket disconnected: " + Socket.EVENT_DISCONNECT));
            on(Socket.EVENT_PING, args -> {
                WebIO.getInstance().getSocket().emit(Socket.EVENT_PONG);
                Utils.redLogLocation(TAG, "Socket Ping: " + Socket.EVENT_PING);
            });
            on(Socket.EVENT_PONG, args -> Utils.redLogLocation(TAG, "Socket Pong: " + Socket.EVENT_PONG));
            on(Socket.EVENT_CONNECTING, args -> Utils.redLogLocation(TAG, "Socket connecting: " + Socket.EVENT_CONNECTING));
            on(Socket.EVENT_RECONNECT, args -> Utils.redLogLocation(TAG, "Socket reconnect: " + Socket.EVENT_RECONNECT));
            on(Socket.EVENT_RECONNECT_ATTEMPT, args -> Utils.redLogLocation(TAG, "Socket reconnect attempt: " + Socket.EVENT_RECONNECT_ATTEMPT));
            on(Socket.EVENT_RECONNECT_ERROR, args -> Utils.redLogLocation(TAG, "Socket reconnect error: " + Socket.EVENT_RECONNECT_ERROR));
            on(Socket.EVENT_RECONNECT_FAILED, args -> Utils.redLogLocation(TAG, "Socket reconnect failed: " + Socket.EVENT_RECONNECT_FAILED));
            WebIO.getInstance().getSocket().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Separate function to check Socket.EVENT_CONNECT event because Library is also attaching its own listener
     * from Manager Class and callback is important for our app as we are requesting our server to
     * updating socket for particular user when app establish connection
     */
    private boolean isConnectionListenerAttached() {
        if (getSocket() != null &&
                (getSocket().listeners(Socket.EVENT_CONNECT).size() > 0)) {
            boolean isAttached = false;
            for (Emitter.Listener listener : getSocket().listeners(Socket.EVENT_CONNECT)) {
                if (listener.getClass().getName().contains(WebIORequestHandler.class.getName())) {
                    isAttached = true;
                }
            }
            return isAttached;
        } else {
            return false;
        }
    }

    public boolean emit(String event, Object... params) {
        if (!WebIO.getInstance().isSocketConnected()) {
            return false;

        }
        WebIO.getInstance().getSocket().emit(event, params);
        return true;
    }

    synchronized boolean emitLocation(String event, Object... params) {
        if (!WebIO.getInstance().isSocketConnected()) {
            Utils.redLogLocation(TAG, "socket_emit failed due to socket not connected: "
                    + event + " " + DateFormat.getDateTimeInstance().format(new Date()));
            return false;

        }

        if (Connectivity.isConnectedFast(DriverApp.getContext()))
            if (Utils.canSendLocation()) {
                AppPreferences.setLocationEmitTime();
                WebIO.getInstance().getSocket().emit(event, params);
                Utils.redLogLocation(TAG, "socket_emit :" + event + " "
                        + DateFormat.getDateTimeInstance().format(new Date()));
            } else {
                Utils.redLogLocation(TAG, "socket_emit failed due to no internet: "
                        + event + " " + DateFormat.getDateTimeInstance().format(new Date()));
            }
        return true;
    }


    public void on(String eventName, Emitter.Listener callBack) {
        if (isListenerRequired(eventName)) {
            WebIO.getInstance().getSocket().on(eventName, callBack);
        }
    }

    private boolean isListenerRequired(String eventName) {
        if (getSocket() != null &&
                (getSocket().listeners(eventName).size() > 0)) {
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
//
//    .off(Socket.EVENT_CONNECT_ERROR, callBack)
                .off(Socket.EVENT_DISCONNECT, callBack);
        Dialogs.INSTANCE.dismissDialog();
    }
}
