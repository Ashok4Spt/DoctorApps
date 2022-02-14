package com.tulasimultispecialityhospital.connections

import okhttp3.CertificatePinner
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class UnsafeOkHttpClient {

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
            builder.hostnameVerifier { hostname, session -> true }

            val certificatePinner = CertificatePinner.Builder()
                .add(ConnectionManager.SERVICE_ENDPOINT,
                    "sha256/iMBZ64iz8WpSP/cWaZGktIbQ7P+JgZzgbQvXN2vfNjY="
                )
                .build()


            return builder
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .certificatePinner(certificatePinner)
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .retryOnConnectionFailure(true)
                .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .build()
        } catch (e: Exception) {

            throw RuntimeException(e)
        }

    }
}