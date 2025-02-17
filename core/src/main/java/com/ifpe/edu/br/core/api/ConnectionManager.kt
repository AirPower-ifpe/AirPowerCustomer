package com.ifpe.edu.br.core.api

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import com.google.gson.GsonBuilder
import com.ifpe.edu.br.core.contracts.IConnectionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ConnectionManager private constructor(
    private val connectionManager: IConnectionManager
) {

    companion object {
        @Volatile
        private var instance: ConnectionManager? = null

        fun getInstance(connectionManager: IConnectionManager): ConnectionManager {
            return instance ?: synchronized(this) {
                instance ?: ConnectionManager(connectionManager).also { instance = it }
            }
        }

        fun getInstance(): ConnectionManager {
            return instance ?: throw IllegalStateException("ConnectionManager not initialized")
        }
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            addInterceptor(connectionManager.getJwtInterceptor())
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            sslSocketFactory(
                connectionManager.getSSLSocketFactory(),
                connectionManager.getX509TrustManager()
            )
            hostnameVerifier { hostname, session -> true }
        }.build()
    }

    val connection: Retrofit by lazy {
        Retrofit.Builder().apply {
            baseUrl(connectionManager.getBaseURL())
            addConverterFactory(ScalarsConverterFactory.create())
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            client(connectionManager.getLoggerClient().build())
            client(httpClient)
        }.build()
    }
}