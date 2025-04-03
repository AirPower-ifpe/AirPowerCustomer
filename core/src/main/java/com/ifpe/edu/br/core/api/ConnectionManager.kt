package com.ifpe.edu.br.core.api

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import com.ifpe.edu.br.core.contracts.IConnectionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ConnectionManager {
    private val connections = mutableMapOf<Int, Retrofit>()

    companion object {
        @Volatile
        private var instance: ConnectionManager? = null
        fun getInstance(): ConnectionManager {
            return instance ?: synchronized(this) {
                instance ?: ConnectionManager().also { instance = it }
            }
        }
    }

    private fun createRetrofitInstance(connectionManager: IConnectionManager): Retrofit {
        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(connectionManager.getJwtInterceptor())
            connectTimeout(connectionManager.getConnectionTimeout(), TimeUnit.SECONDS)
            readTimeout(connectionManager.getConnectionTimeout(), TimeUnit.SECONDS)
            writeTimeout(connectionManager.getConnectionTimeout(), TimeUnit.SECONDS)
            sslSocketFactory(
                connectionManager.getSSLSocketFactory(),
                connectionManager.getX509TrustManager()
            )
            hostnameVerifier { _, _ -> true }
        }.build()

        return Retrofit.Builder()
            .baseUrl(connectionManager.getBaseURL())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .client(connectionManager.getLoggerClient().build())
            .build()
    }

    fun getConnectionById(connectionManager: IConnectionManager): Retrofit {
        val connectionId = connectionManager.getConnectionId()
        return connections.getOrPut(connectionId) {
            createRetrofitInstance(connectionManager)
        }
    }

    fun getConnectionById(connectionId: Int): Retrofit {
        if (connections[connectionId] == null) throw IllegalStateException("Connection not found")
        return connections[connectionId]!!
    }
}