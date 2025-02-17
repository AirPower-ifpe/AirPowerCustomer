package com.ifpe.edu.br.model.repo


import com.google.gson.Gson
import com.ifpe.edu.br.model.api.ThingsBoardAPIService
import com.ifpe.edu.br.model.dto.AuthUser
import com.ifpe.edu.br.model.dto.ThingsBordErrorResponse
import com.ifpe.edu.br.model.dto.Token
import com.ifpe.edu.br.viewmodel.util.AirPowerLog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Retrofit
import javax.net.ssl.HttpsURLConnection


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


private const val TAG = "ThingsBoardManager"

class ThingsBoardManager(connection: Retrofit) {
    val apiService = connection.create(ThingsBoardAPIService::class.java)

    suspend fun auth(
        user: AuthUser,
        onSuccess: (token: Token) -> Unit,
        onFailure: (errorCode: ThingsBordErrorResponse) -> Unit
    ) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "auth()")
        val userJson = Gson().toJson(user)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, userJson)
        val auth = apiService.auth(requestBody)

        val responseCode = auth.code()
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "Authorized")
            val token = auth.body()
            if (token != null) {
                onSuccess.invoke(token)
            } else {
                AirPowerLog.e(TAG, "Token is null")
            }
        } else if (responseCode == HttpsURLConnection.HTTP_UNAUTHORIZED) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.w(TAG, "Unauthorized")
            val errorBody = auth.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse =
                    Gson().fromJson(errorBody, ThingsBordErrorResponse::class.java)
                onFailure.invoke(errorResponse)
            }

        } else {
            AirPowerLog.e(TAG, "Error: $responseCode")
        }
    }
}