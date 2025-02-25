package com.ifpe.edu.br.core.contracts

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer
// Copyright (c) 2025 IFPE. All rights reserved.

interface IConnectionManager {
    fun getJwtInterceptor(): Interceptor
    fun getSSLSocketFactory(): SSLSocketFactory
    fun getX509TrustManager(): X509TrustManager
    fun getLoggerClient(): HttpLoggingInterceptor
    fun getConnectionId(): Int
    fun getBaseURL(): String
    fun getConnectionTimeout(): Long
}