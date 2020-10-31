package com.bykea.pk.partner.communication.rest;

import android.content.Context;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.dal.source.remote.NetworkUtil;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.LoggingInterceptor;
import com.bykea.pk.partner.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RestClient {
    private static IRestClient retrofitCalls;
    private static IRestClient retrofitChatAudio;
    private static IRestClient retrofitGoogleApiCalls;
    private static IRestClient bykea2retrofitCalls;
    private static IRestClient bykeaSignUpretrofitCalls;


    static IRestClient getClient(Context context) {
        if (retrofitCalls == null) {
            /*creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = Utils.getSSLContext(context);
            if (sslContext != null) {
                TODO sslSocketFactory method is deprecated.. will be fixed in refactoring which is further child of this branch
                need to pass X509 trust manager externally
                this PR is for checking retrofit2 is working fine
                builder.sslSocketFactory(sslContext.getSocketFactory());
            }
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
            HttpLoggingInterceptor.Level.NONE);
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setRetryOnConnectionFailure(false);*/

            OkHttpClient.Builder builder = NetworkUtil.INSTANCE.enableTls12OnPreLollipop();
            if (BuildConfig.DEBUG)
                builder.interceptors().add(new LoggingInterceptor());
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            Retrofit client = retrofitBuilder.baseUrl(ApiTags.BASE_SERVER_URL)
                   .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitCalls = client.create(IRestClient.class);
        }
        return retrofitCalls;
    }

    /***
     *  Retrofit {@link OkHttpClient} configuration setup for chat audio module
     * @param context Calling context.
     *
     * @return Retrofit client for Chat Audio Feature.
     */
    static IRestClient getChatAudioClient(Context context) {
        if (retrofitChatAudio == null) {
            /* creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = Utils.getSSLContext(context);
            if (sslContext != null) {
                builder.sslSocketFactory(sslContext.getSocketFactory());
            }
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setRetryOnConnectionFailure(false);*/

            OkHttpClient.Builder builder = NetworkUtil.INSTANCE.enableTls12OnPreLollipop();
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            Retrofit client = retrofitBuilder.baseUrl(ApiTags.BASE_SERVER_URL)
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitChatAudio = client.create(IRestClient.class);
        }
        return retrofitChatAudio;
    }

    static IRestClient getGooglePlaceApiClient() {
        if (retrofitGoogleApiCalls == null) {
            /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                    HttpLoggingInterceptor.Level.NONE);
            okHttpClient.connectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.readTimeout(60, TimeUnit.SECONDS);
            okHttpClient.retryOnConnectionFailure(false);*/

            OkHttpClient.Builder okHttpClient = NetworkUtil.INSTANCE.enableTls12OnPreLollipop();
            if (BuildConfig.DEBUG)
                okHttpClient.interceptors().add(new LoggingInterceptor());
            Retrofit.Builder builder = new Retrofit.Builder();
            Retrofit client = builder.baseUrl(ApiTags.GOOGLE_API_BASE_URL)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitGoogleApiCalls = client.create(IRestClient.class);
        }
        return retrofitGoogleApiCalls;
    }

    static IRestClient getBykea2ApiClient(Context context) {
        if (bykea2retrofitCalls == null) {
            /*okHttpClient.connectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.readTimeout(60, TimeUnit.SECONDS);
            okHttpClient.retryOnConnectionFailure(false);*/

            OkHttpClient.Builder okHttpClient = NetworkUtil.INSTANCE.enableTls12OnPreLollipop();
            if (BuildConfig.DEBUG)
                okHttpClient.interceptors().add(new LoggingInterceptor());
            Retrofit.Builder builder = new Retrofit.Builder();
            Retrofit client = builder.baseUrl(ApiTags.BASE_SERVER_URL_2)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            bykea2retrofitCalls = client.create(IRestClient.class);
        }
        return bykea2retrofitCalls;
    }

    static IRestClient getBykeaSignUpApiClient() {
        if (bykeaSignUpretrofitCalls == null) {
            OkHttpClient.Builder okHttpClient = NetworkUtil.INSTANCE.enableTls12OnPreLollipop();
            if (BuildConfig.DEBUG)
                okHttpClient.interceptors().add(new LoggingInterceptor());
            Retrofit.Builder builder = new Retrofit.Builder();
            String signUpUrl = "http://54.189.207.7:5050";
            if (AppPreferences.getSettings() != null
                    && AppPreferences.getSettings().getSettings() != null
                    && StringUtils.isNotBlank(AppPreferences.getSettings().getSettings().getPartner_signup_url())) {
                signUpUrl = AppPreferences.getSettings().getSettings().getPartner_signup_url();
            }
            Retrofit client = builder.baseUrl(signUpUrl)
                    .client(okHttpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            bykeaSignUpretrofitCalls = client.create(IRestClient.class);
        }
        return bykeaSignUpretrofitCalls;
    }

    /**
     * This method sets null value to retrofitCalls so that it creates new instance of retrofitCalls
     * to avoid session logout issue when dynamic url is set for Local builds
     */
    static void clearBykeaRetrofitClient() {
        retrofitCalls = null;
    }

}
