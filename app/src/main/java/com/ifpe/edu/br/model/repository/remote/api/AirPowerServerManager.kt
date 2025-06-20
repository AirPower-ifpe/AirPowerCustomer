package com.ifpe.edu.br.model.repository.remote.api

import com.google.gson.Gson
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.repository.remote.dto.DeviceAggregatedTelemetry
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.auth.Token
import com.ifpe.edu.br.model.repository.remote.dto.user.AirPowerBoardUser
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.repository.remote.query.RefreshTokenQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AuthenticateFailureException
import com.ifpe.edu.br.model.util.ExceptionHandler
import com.ifpe.edu.br.model.util.InvalidStateException
import com.ifpe.edu.br.model.util.ServerUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Retrofit
import javax.net.ssl.HttpsURLConnection


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


class AirPowerServerManager(connection: Retrofit) {
    private val apiService = connection.create(AirPowerServerAPIService::class.java)
    private val TAG = AirPowerServerManager::class.simpleName

    suspend fun authenticate(
        user: AuthUser,
    ) {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "authenticate()")
        val userJson = Gson().toJson(user)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, userJson)
        val serverResponse = apiService.auth(requestBody)
        val responseCode = serverResponse.code()
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "Authentication success: HTTP_OK")
            serverResponse.body()?.let {
                JWTManager.handleAuthentication(
                    AirPowerServerConnectionContractImpl.getConnectionId(),
                    it
                ) { }
            }
        } else {
            val serverErrorWrapper = ServerUtils.getServerErrorWrapper(serverResponse)
            if (serverErrorWrapper.errorCode == Constants.ResponseErrorCodes.INVALID_AIRPOWER_TOKEN) {
                if (AirPowerLog.ISVERBOSE) AirPowerLog.e(
                    TAG,
                    "Authentication failed: ${serverErrorWrapper.message}"
                )
                throw AuthenticateFailureException("[$TAG]: -> Authentication failure: message:${serverErrorWrapper.message}")
            } else {
                throw InvalidStateException("[$TAG]: -> Unhandled issue message:${serverErrorWrapper.message} code: ${serverErrorWrapper.errorCode}")
            }
        }
    }

    suspend fun refreshToken(
        onSuccess: () -> Unit
    ) {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "refreshToken()")
        val jwtManager = JWTManager
        val token = jwtManager
            .getTokenForConnectionId(AirPowerServerConnectionContractImpl.getConnectionId())
        if (!jwtManager.isTokenValid(token))
            throw InvalidStateException("[$TAG]: -> Token is not valid")
        val refreshTokenQuery = Gson().toJson(token?.let { RefreshTokenQuery(it.refreshToken) })
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, refreshTokenQuery)
        val serverResponse = apiService.refreshToken(requestBody)
        if (serverResponse.code() == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "refreshToken(): HTTP_OK")
            serverResponse.body()?.let {
                jwtManager.handleRefreshToken(
                    AirPowerServerConnectionContractImpl.getConnectionId(),
                    it
                ) {
                    onSuccess.invoke()
                }
            }
        } else {
            val serverErrorWrapper = ServerUtils.getServerErrorWrapper(serverResponse)
            if (serverErrorWrapper.errorCode == Constants.ResponseErrorCodes.INVALID_AIRPOWER_TOKEN) {
                if (AirPowerLog.ISVERBOSE) AirPowerLog.e(
                    TAG,
                    "RefreshToken failed: ${serverErrorWrapper.message}"
                )
                throw AuthenticateFailureException("[$TAG]: -> RefreshToken failure: message:${serverErrorWrapper.message}")
            } else {
                throw InvalidStateException("[$TAG]: -> Unhandled issue message:${serverErrorWrapper.message} code: ${serverErrorWrapper.errorCode}")
            }
        }
    }

    suspend fun getCurrentUser(): AirPowerBoardUser {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getCurrentUser()")
        val serverResponse = apiService.getCurrentUser()
        if (serverResponse.code() == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getCurrentUser(): HTTP_OK")
            val thingsBoardUser = serverResponse.body()
            return thingsBoardUser ?: throw InvalidStateException("[$TAG]: ThingsBoardUser is null")
        } else {
            val serverErrorWrapper = ServerUtils.getServerErrorWrapper(serverResponse)
            if (serverErrorWrapper.errorCode == Constants.ResponseErrorCodes.INVALID_AIRPOWER_TOKEN) {
                if (AirPowerLog.ISVERBOSE) AirPowerLog.w(
                    TAG,
                    "INVALID_AIRPOWER_TOKEN"
                )
                throw AuthenticateFailureException("[$TAG]: message: ${serverErrorWrapper.message}")
            } else {
                throw InvalidStateException("[$TAG]: untracked failure: server " +
                        "code: ${serverErrorWrapper.errorCode} " +
                        "message: ${serverErrorWrapper.message}"
                )
            }
        }
    }

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
            val serverErrorWrapper = ServerUtils.getServerErrorWrapper(serverResponse)
            if (serverErrorWrapper.errorCode == Constants.ResponseErrorCodes.INVALID_AIRPOWER_TOKEN) {
                if (AirPowerLog.ISVERBOSE) AirPowerLog.w(
                    TAG,
                    "INVALID_AIRPOWER_TOKEN"
                )
                throw AuthenticateFailureException("[$TAG]: -> getAggregatedTelemetry failure: message ${serverErrorWrapper.message}")
            } else {
                throw InvalidStateException("[$TAG]: untracked failure: server " +
                        "code: ${serverErrorWrapper.errorCode} " +
                        "message: ${serverErrorWrapper.message}"
                )
            }
        }
        return emptyList()
    }

    suspend fun getDeviceSummariesForUser(
        user: AirPowerBoardUser
    ): List<DeviceSummary> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getDeviceSummariesForUser()")
        val serverResponse = apiService.getDeviceSummariesForUser(user.id.id)
        val responseCode = serverResponse.code()
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getDeviceSummariesForUser: HTTP_OK")
            serverResponse.body()?.let {
                return it
            }
        } else {
            val serverErrorWrapper = ServerUtils.getServerErrorWrapper(serverResponse)
            throw AuthenticateFailureException("[$TAG]: -> getDeviceSummariesForUser failure: message:${serverErrorWrapper.message}")
        }
        return emptyList()
    }
}