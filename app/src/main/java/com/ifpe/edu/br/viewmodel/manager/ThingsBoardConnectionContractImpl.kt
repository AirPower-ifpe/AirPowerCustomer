package com.ifpe.edu.br.viewmodel.manager

import com.ifpe.edu.br.core.contracts.IConnectionManager
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.viewmodel.util.AirPowerLog
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


object ThingsBoardConnectionContractImpl : IConnectionManager {

    private val TAG: String = ThingsBoardConnectionContractImpl.javaClass.simpleName

    private fun loadCustomCertificate(inputStream: InputStream): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificate = certificateFactory.generateCertificate(inputStream) as X509Certificate

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("server", certificate)

        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)

        return trustManagerFactory.trustManagers.first { it is X509TrustManager } as X509TrustManager
    }

    override fun getConnectionId(): Int {
        return Constants.CONNECTION_ID_THINGSBOARD
    }

    override fun getJwtInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader(
                    "Authorization", "Bearer " +
                            JWTManager.getInstance().getJwtForConnectionId(getConnectionId())
                )
                .build()
            chain.proceed(newRequest)
        }
    }

    override fun getSSLSocketFactory(): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(getX509TrustManager()), SecureRandom())
        return sslContext.socketFactory
    }

    override fun getX509TrustManager(): X509TrustManager {
        // val inputStream = context.assets.open("my_certificate.crt") // todo future feature
        // return loadCustomCertificate(inputStream)

        // IGNORES CERTIFICATE VERIFICATION DUE DEVELOPMENT ENVIRONMENT
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.w(
                        TAG, "checkClientTrusted: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED THINGS BOARD CERTIFICATE"
                    )
                }
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.w(
                        TAG, "checkServerTrusted: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED THINGS BOARD CERTIFICATE"
                    )
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.w(
                        TAG, "getAcceptedIssuers: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED THINGS BOARD CERTIFICATE"
                    )
                }
                return arrayOf()
            }
        }
    }

    override fun getLoggerClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
    }

    override fun getBaseURL(): String {
        return Constants.URL_API
    }

    override fun getConnectionTimeout(): Long {
        return 3
    }
}