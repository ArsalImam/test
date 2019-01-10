package com.bykea.pk.partner.communication.rest;

import android.content.Context;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.ApiTags;
import com.bykea.pk.partner.utils.LoggingInterceptor;
import com.bykea.pk.partner.utils.Utils;
import com.squareup.okhttp.OkHttpClient;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


class RestClient {
    private static IRestClient retrofitCalls;
    private static IRestClient retrofitChatAudio;
    private static IRestClient retrofitGoogleApiCalls;
    private static IRestClient bykea2retrofitCalls;
    private static IRestClient bykeaSignUpretrofitCalls;


    static IRestClient getClient(Context context) {
        if (retrofitCalls == null) {

            OkHttpClient okHttpClient = new OkHttpClient();

            // creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = Utils.getSSLContext(context);
            if (sslContext != null) {
                okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
            }

           /* HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                    HttpLoggingInterceptor.Level.NONE);*/

            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            //okHttpClient.setRetryOnConnectionFailure(false);
            if (BuildConfig.DEBUG)
                okHttpClient.interceptors().add(new LoggingInterceptor());
            Retrofit.Builder builder = new Retrofit.Builder();
            Retrofit client = builder.baseUrl(ApiTags.BASE_SERVER_URL)
                    .client(okHttpClient)
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

            OkHttpClient okHttpClient = new OkHttpClient();

            // creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = Utils.getSSLContext(context);
            if (sslContext != null) {
                okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
            }
            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            //okHttpClient.setRetryOnConnectionFailure(false);
            Retrofit.Builder builder = new Retrofit.Builder();
            Retrofit client = builder.baseUrl(ApiTags.BASE_SERVER_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitChatAudio = client.create(IRestClient.class);
        }
        return retrofitChatAudio;
    }


    /*   public static IRestClient getClient(Context context) {
           if (retrofitCalls == null) {
               OkHttpClient okHttpClient;
               HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
               loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                       HttpLoggingInterceptor.Level.NONE);
               okHttpClient = new OkHttpClient();
               okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
               okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
               okHttpClient.interceptors().add(loggingInterceptor);
               Retrofit.Builder builder = new Retrofit.Builder();
               Retrofit client = builder.baseUrl(ApiTags.BASE_SERVER_URL)
                       .client(okHttpClient)
                       .addConverterFactory(GsonConverterFactory.create())
                       .build();
               retrofitCalls = client.create(IRestClient.class);
           }
           return retrofitCalls;
       }
   */
    static IRestClient getGooglePlaceApiClient() {
        if (retrofitGoogleApiCalls == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                    HttpLoggingInterceptor.Level.NONE);*/

            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setRetryOnConnectionFailure(false);
            if (BuildConfig.DEBUG)
                okHttpClient.interceptors().add(new LoggingInterceptor());
            Retrofit.Builder builder = new Retrofit.Builder();
            Retrofit client = builder.baseUrl(ApiTags.GOOGLE_API_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitGoogleApiCalls = client.create(IRestClient.class);
        }
        return retrofitGoogleApiCalls;
    }

    static IRestClient getBykea2ApiClient(Context context) {
        if (bykea2retrofitCalls == null) {
            OkHttpClient okHttpClient = new OkHttpClient();

            // creating an SSLSocketFactory that uses our TrustManager
//            SSLContext sslContext = Utils.getSSLContext(context);
//            if (sslContext != null) {
//                okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
//            }

            /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                    HttpLoggingInterceptor.Level.NONE);*/


            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setRetryOnConnectionFailure(false);
            if (BuildConfig.DEBUG)
                okHttpClient.interceptors().add(new LoggingInterceptor());
            Retrofit.Builder builder = new Retrofit.Builder();
            Retrofit client = builder.baseUrl(ApiTags.BASE_SERVER_URL_2)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            bykea2retrofitCalls = client.create(IRestClient.class);
        }
        return bykea2retrofitCalls;
    }

    static IRestClient getBykeaSignUpApiClient() {
        if (bykeaSignUpretrofitCalls == null) {
            OkHttpClient okHttpClient = new OkHttpClient();

            // creating an SSLSocketFactory that uses our TrustManager
//            SSLContext sslContext = Utils.getSSLContext(context);
//            if (sslContext != null) {
//                okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
//            }

           /* HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                    HttpLoggingInterceptor.Level.NONE);*/


            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setRetryOnConnectionFailure(false);
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
                    .client(okHttpClient)
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
