package com.bykea.pk.partner.communication.rest;

import android.content.Context;

import com.bykea.pk.partner.BuildConfig;
import com.bykea.pk.partner.utils.ApiTags;

import com.bykea.pk.partner.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


public class RestClient {
    private static IRestClient retrofitCalls;
    private static IRestClient retrofitGoogleApiCalls;


    public static IRestClient getClient(Context context) {
        if (retrofitCalls == null) {

            OkHttpClient okHttpClient = new OkHttpClient();

            // creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = Utils.getSSLContext(context);
            if (sslContext != null) {
                okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
            }

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                    HttpLoggingInterceptor.Level.NONE);


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
    public static IRestClient getGooglePlaceApiClient() {
        if (retrofitGoogleApiCalls == null) {
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(ApiTags.GOOGLE_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofitGoogleApiCalls = client.create(IRestClient.class);
        }
        return retrofitGoogleApiCalls;
    }

}
