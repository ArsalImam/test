package com.bykea.pk.partner.dal.source.remote

import android.content.res.Resources
import com.bykea.pk.partner.dal.R
import okhttp3.OkHttpClient
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object NetworkUtil {

    /**
     * Get SSL Context to obtain the SSL factory from it
     */
    fun getSSLContext(): SSLContext? {
        var sslContext: SSLContext? = null
        try {
            // loading CAs from an InputStream
            val cf = CertificateFactory.getInstance("X.509")
            val cert = Resources.getSystem().openRawResource(R.raw.star_bykea_net)
            val ca: Certificate
            try {
                ca = cf.generateCertificate(cert)
            } finally {
                cert.close()
            }

            // creating a KeyStore containing our trusted CAs
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)

            // creating a TrustManager that trusts the CAs in our KeyStore
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)

            // creating an SSLSocketFactory that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS")
            sslContext!!.init(null, tmf.trustManagers, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sslContext
    }

    /**
     * Obtain the Unsafe OkHttp Client
     */
    fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
