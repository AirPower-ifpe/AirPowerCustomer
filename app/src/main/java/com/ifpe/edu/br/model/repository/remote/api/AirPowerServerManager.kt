package com.ifpe.edu.br.model.repository.remote.api

import com.google.gson.Gson
import com.ifpe.edu.br.model.repository.remote.dto.DeviceAggregatedTelemetry
import com.ifpe.edu.br.model.repository.remote.dto.ThingsBordErrorResponse
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AuthenticateFailureException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import javax.net.ssl.HttpsURLConnection


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


class AirPowerServerManager(connection: Retrofit) {
    private val apiService = connection.create(AirPowerServerAPIService::class.java)
    private val TAG = AirPowerServerManager::class.simpleName

    suspend fun getAggregatedTelemetry(
        query: AggregatedTelemetryQuery,
        onSuccess: () -> Unit
    ): List<DeviceAggregatedTelemetry> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getAggregatedTelemetry()")
        val queryJson = Gson().toJson(query)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, queryJson)
        val serverResponse = apiService.getAggregatedTelemetry(requestBody)
        val responseCode = serverResponse.code()
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getAggregatedTelemetry: HTTP_OK")
            serverResponse.body()?.let {
                onSuccess.invoke()
                return it.results
            }
        } else {
            val serverErrorWrapper = getServerErrorWrapper(serverResponse)
            throw AuthenticateFailureException("[$TAG]: -> getAggregatedTelemetry failure: message:${serverErrorWrapper.message}")
        }
        return emptyList()
    }

    private fun getServerErrorWrapper(serverResponse: Response<out Any>): ThingsBordErrorResponse {
        return try {
            val errorBody = serverResponse.errorBody()?.string()
            if (errorBody != null) {
                try {
                    Gson().fromJson(errorBody, ThingsBordErrorResponse::class.java)
                } catch (jsonException: Exception) {
                    AirPowerLog.e(TAG, "Error parsing JSON response: ${jsonException.message}")
                    ThingsBordErrorResponse(-1, "Invalid JSON format", -1, "-1")
                }
            } else {
                if (AirPowerLog.ISLOGABLE) AirPowerLog.e(TAG, "errorBody is null")
                ThingsBordErrorResponse(-1, "Empty error body", -1, "-1")
            }
        } catch (e: Exception) {
            AirPowerLog.e(TAG, "Unexpected error while handling server response: ${e.message}")
            ThingsBordErrorResponse(-1, "Unexpected error", -1, "-1")
        }
    }
}