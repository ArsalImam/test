package com.bykea.pk.partner.dal.source.remote

import android.content.res.Resources
import com.bykea.pk.partner.dal.R
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object Utils {

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

}