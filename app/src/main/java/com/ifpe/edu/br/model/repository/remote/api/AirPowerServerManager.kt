package com.ifpe.edu.br.model.repository.remote.api

import com.google.gson.Gson
import com.ifpe.edu.br.model.repository.model.TelemetryDataWrapper
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.AllMetricsWrapper
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.TelemetryAggregationResponse
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggDataWrapperResponse
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggregationRequest
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.auth.Token
import com.ifpe.edu.br.model.repository.remote.dto.error.ErrorCode
import com.ifpe.edu.br.model.repository.remote.dto.user.ThingsBoardUser
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.repository.remote.query.RefreshTokenQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.ResultWrapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.util.UUID


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


class AirPowerServerManager(connection: Retrofit) {
    private val apiService = connection.create(AirPowerServerAPIService::class.java)
    private val TAG = AirPowerServerManager::class.simpleName

    suspend fun authenticate(
        user: AuthUser,
    ): ResultWrapper<Token> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "authenticate()")
        val userJson = Gson().toJson(user)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, userJson)
        val response = safeApiCall { apiService.auth(requestBody) }
        if (response is ResultWrapper.Success) {
            JWTManager.handleAuthentication(
                AirPowerServerConnectionContractImpl.getConnectionId(),
                response.value
            ) { }
        }
        return response
    }

    suspend fun refreshToken(): ResultWrapper<Token> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "refreshToken()")
        val jwtManager = JWTManager
        val token = jwtManager
            .getTokenForConnectionId(AirPowerServerConnectionContractImpl.getConnectionId())
        if (!jwtManager.isTokenValid(token)) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "Token is not valid")
            return ResultWrapper.ApiError(ErrorCode.AP_REFRESH_TOKEN_EXPIRED)
        }

        val refreshTokenQuery = Gson().toJson(token?.let { RefreshTokenQuery(it.refreshToken) })
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, refreshTokenQuery)

        val tokenResultWrapper = safeApiCall { apiService.refreshToken(requestBody) }
        if (tokenResultWrapper is ResultWrapper.Success) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "refreshToken(): HTTP_OK")
            jwtManager.handleRefreshToken(
                AirPowerServerConnectionContractImpl.getConnectionId(),
                tokenResultWrapper.value
            ) {}
        }
        return tokenResultWrapper
    }

    suspend fun getCurrentUser(): ResultWrapper<ThingsBoardUser> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getCurrentUser()")
        return safeApiCall { apiService.getCurrentUser() }
    }

    suspend fun getAggregatedTelemetry(
        query: AggregatedTelemetryQuery
    ): ResultWrapper<TelemetryAggregationResponse> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getAggregatedTelemetry()")
        val queryJson = Gson().toJson(query)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, queryJson)
        return safeApiCall { apiService.getAggregatedTelemetry(requestBody) }
    }

    suspend fun getDeviceSummariesForUser(
        user: ThingsBoardUser
    ): ResultWrapper<List<DeviceSummary>> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getDeviceSummariesForUser()")
        return safeApiCall { apiService.getDeviceSummariesForUser(user.id.id.toString()) }
    }

    suspend fun getAlarmsForCurrentUser(): ResultWrapper<List<AlarmInfo>> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getAlarmsForCurrentUser()")
        return safeApiCall { apiService.getAlarmsForCurrentUser() }
    }

    suspend fun getDevicesMetricsWrapper(groupID: String): ResultWrapper<List<AllMetricsWrapper>> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getDevicesMetricsWrapper()")
        return safeApiCall { apiService.getDevicesMetricsWrapper(groupID) }
    }

    suspend fun getDeviceMetricsWrapperById(id: UUID): ResultWrapper<TelemetryDataWrapper> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getDevicesMetricsWrapper()")
        val result = safeApiCall { apiService.getDevicesMetricsWrapper(id.toString()) }
        return when (result) {
            is ResultWrapper.Success -> {
                if (result.value.isEmpty()) {
                    return ResultWrapper.ApiError(ErrorCode.TB_GENERIC_ERROR)
                }
                val allMetricsWrapper = result.value[0]
                val deviceConsumptionSet = allMetricsWrapper.deviceConsumptionSet
                val label = allMetricsWrapper.label
                val telemetryDataWrapper = TelemetryDataWrapper(label, deviceConsumptionSet)
                return ResultWrapper.Success(telemetryDataWrapper)
            }

            is ResultWrapper.ApiError -> {
                ResultWrapper.ApiError(result.errorCode)
            }

            ResultWrapper.NetworkError -> {
                ResultWrapper.NetworkError
            }

            ResultWrapper.Empty -> {
                ResultWrapper.Empty
            }
        }
    }

    suspend fun getDeviceAggregatedDataWrapper(
        request: AggregationRequest
    ): ResultWrapper<AggDataWrapperResponse> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getDeviceAggregatedDataWrapper(): " +
                "request: $request")
        val queryJson = Gson().toJson(request)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, queryJson)
        return safeApiCall { apiService.getDeviceAggregatedDataWrapper(requestBody) }
    }
}