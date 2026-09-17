package com.ifpe.edu.br.model.repository.remote.api

import android.Manifest
import androidx.annotation.RequiresPermission
import com.ifpe.edu.br.AirPowerApplication
import com.ifpe.edu.br.BuildConfig
import com.ifpe.edu.br.core.contracts.IConnectionManager
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.repository.persistence.manager.SharedPrefManager
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.NetworkUtils
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
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
    private val apiUrl = BuildConfig.API_URL
    override fun getJwtInterceptor(): Interceptor {
        if (AirPowerLog.ISVERBOSE) {
            AirPowerLog.d(TAG, "getJwtInterceptor()")
        }
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val jwtToken = runBlocking {
                JWTManager.getJwtForConnectionId(getConnectionId())
            }
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $jwtToken")
                .build()
            chain.proceed(newRequest)
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun getDynamicHostInterceptor(): Interceptor {
        if (AirPowerLog.ISVERBOSE) {
            AirPowerLog.d(TAG, "getDynamicHostInterceptor()")
        }
        val spM = SharedPrefManager.getInstance()
        return Interceptor { chain ->
            var request = chain.request()
            val isVpnActive = NetworkUtils.isVpnActive(AirPowerApplication.getContext())
            val forceVpn = spM.isForceVpn()

            val targetBaseUrlString = if (isVpnActive || forceVpn) {
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.d(TAG, "using VPN base URL")
                }
                spM.vpnIp
            } else {
                if (AirPowerLog.ISLOGABLE) {
                    AirPowerLog.d(TAG, "using local base URL")
                }
                spM.localIp
            }

            val newBaseUrl = targetBaseUrlString.toHttpUrlOrNull()

            if (newBaseUrl != null) {
                val newUrl = request.url.newBuilder()
                    .scheme(newBaseUrl.scheme)
                    .host(newBaseUrl.host)
                    .port(newBaseUrl.port)
                    .build()

                request = request.newBuilder()
                    .url(newUrl)
                    .build()
            }

            chain.proceed(request)
        }
    }

    override fun getSSLSocketFactory(): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(
            null,
            arrayOf(getX509TrustManager()),
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
        return Constants.ServerConnectionIds.CONNECTION_ID_AIR_POWER_SERVER
    }

    override fun getBaseURL(): String {
        return apiUrl
    }

    override fun getConnectionTimeout(): Long {
        return 3
    }
}