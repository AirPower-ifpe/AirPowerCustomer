package com.ifpe.edu.br.viewmodel.manager

import com.ifpe.edu.br.core.contracts.IConnectionManager
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.viewmodel.util.AirPowerLog
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


object ThingsBoardContractImpl : IConnectionManager {

    private val TAG: String = ThingsBoardContractImpl.javaClass.simpleName
    val THINGSBOARD_CONNECTION_ID = 1

    override fun getConnectionId(): Int {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getConnectionId")
        return THINGSBOARD_CONNECTION_ID
    }

    override fun getJwtInterceptor(): Interceptor {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getJwtInterceptor")
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader(
                    "Authorization", "Bearer ${
                        JWTMgr.getInstance().getJwtForConnectionId(getConnectionId())
                    }}"
                )
                .build()
            chain.proceed(request)
        }
    }

    override fun getSSLSocketFactory(): SSLSocketFactory {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getSSLSocketFactory")
        return try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(getX509TrustManager()), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun getX509TrustManager(): X509TrustManager {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getX509TrustManager")
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.w(
                        TAG, "checkClientTrusted: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED T.B. CERTIFICATE"
                    )
                }
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.w(
                        TAG, "checkServerTrusted: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED T.B. CERTIFICATE"
                    )
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.w(
                        TAG, "getAcceptedIssuers: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED T.B. CERTIFICATE"
                    )
                }
                return arrayOf()
            }
        }
    }

    override fun getLoggerClient(): OkHttpClient.Builder {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getLoggerClient")
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
    }

    override fun getBaseURL(): String {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getBaseURL")
        return Constants.URL_API
    }

    override fun getConnectionTimeout(): Long {
        return 1
    }
}