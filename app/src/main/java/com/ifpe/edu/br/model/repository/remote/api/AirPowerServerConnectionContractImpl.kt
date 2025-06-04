package com.ifpe.edu.br.model.repository.remote.api

import com.ifpe.edu.br.core.contracts.IConnectionManager
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.util.AirPowerLog
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
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


object AirPowerServerConnectionContractImpl : IConnectionManager {

    private val TAG: String = AirPowerServerConnectionContractImpl.javaClass.simpleName

    override fun getJwtInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val jwtToken = runBlocking {
                JWTManager.getJwtForConnectionId(ThingsBoardConnectionContractImpl.getConnectionId())
            }
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $jwtToken")
                .build()
            chain.proceed(newRequest)
        }
    }

    override fun getSSLSocketFactory(): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(
            null,
            arrayOf(ThingsBoardConnectionContractImpl.getX509TrustManager()),
            SecureRandom()
        )
        return sslContext.socketFactory
    }

    override fun getX509TrustManager(): X509TrustManager {
        // val inputStream = context.assets.open("my_certificate.crt") // todo future feature
        // return loadCustomCertificate(inputStream)

        // IGNORES CERTIFICATE VERIFICATION DUE DEVELOPMENT ENVIRONMENT
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                if (AirPowerLog.ISVERBOSE) {
                    AirPowerLog.w(
                        TAG, "checkClientTrusted: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED THINGS BOARD CERTIFICATE"
                    )
                }
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                if (AirPowerLog.ISVERBOSE) {
                    AirPowerLog.w(
                        TAG, "checkServerTrusted: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED THINGS BOARD CERTIFICATE"
                    )
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                if (AirPowerLog.ISVERBOSE) {
                    AirPowerLog.w(
                        TAG, "getAcceptedIssuers: \n" +
                                "CERTIFICATE VERIFICATION DISABLED DUE SELF SIGNED THINGS BOARD CERTIFICATE"
                    )
                }
                return arrayOf()
            }
        }
    }

    override fun getLoggerClient(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
        return loggingInterceptor
    }

    override fun getConnectionId(): Int {
        return Constants.CONNECTION_ID_AIR_POWER_SERVER
    }

    override fun getBaseURL(): String {
        return Constants.AIRPOWER_SERVER_BASE_URL_API
    }

    override fun getConnectionTimeout(): Long {
        return 3
    }
}