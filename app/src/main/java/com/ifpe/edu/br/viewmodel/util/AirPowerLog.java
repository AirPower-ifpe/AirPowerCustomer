package com.ifpe.edu.br.viewmodel.util;
// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class AirPowerLog {
    private static final String TAG = "AirPowerApp:";
    public static final boolean ISLOGABLE = AirPowerUtil.isDebugVersion();

    public static void d(String subtag, String message) {
        Log.d(TAG, " [" + subtag + "]: " + message);
    }

    public static void d(String subtag, Thread thread, String message) {
        Log.d(TAG, " [" + subtag + "] " + " [" + thread.getName() + "] " + message);
    }

    public static void e(String subtag, String message) {
        Log.e(TAG, " [" + subtag + "]: " + message);
    }

    public static void w(String subtag, String message) {
        Log.w(TAG, " [" + subtag + "]: " + message);
    }

    public static OkHttpClient.Builder getLoggerClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(loggingInterceptor);
        return httpClient;
    }
}
