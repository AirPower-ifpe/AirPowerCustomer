package com.ifpe.edu.br.model.repo


import com.google.gson.Gson
import com.ifpe.edu.br.model.api.ThingsBoardAPIService
import com.ifpe.edu.br.model.dto.AuthUser
import com.ifpe.edu.br.model.dto.Device
import com.ifpe.edu.br.model.dto.ThingsBoardUser
import com.ifpe.edu.br.model.dto.ThingsBordErrorResponse
import com.ifpe.edu.br.model.model.auth.AirPowerUser
import com.ifpe.edu.br.model.query.RefreshTokenQuery
import com.ifpe.edu.br.viewmodel.manager.JWTManager
import com.ifpe.edu.br.viewmodel.manager.ThingsBoardConnectionContractImpl
import com.ifpe.edu.br.viewmodel.util.AirPowerLog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import javax.net.ssl.HttpsURLConnection


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


class ThingsBoardManager(connection: Retrofit) {
    private val apiService = connection.create(ThingsBoardAPIService::class.java)
    private val TAG = ThingsBoardManager::class.simpleName

    suspend fun auth(
        user: AuthUser,
        onSuccess: () -> Unit
    ) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "auth()")
        val userJson = Gson().toJson(user)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, userJson)
        val serverResponse = apiService.auth(requestBody)
        val responseCode = serverResponse.code()
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "auth(): HTTP_OK")
            JWTManager.getInstance().handleAuthentication(
                ThingsBoardConnectionContractImpl.getConnectionId(),
                serverResponse.body()
            ) { onSuccess.invoke() }
        } else {
            throw IllegalStateException("auth() Error! ${getServerErrorMessage(serverResponse)}")
        }
    }

    suspend fun getCurrentUser(): ThingsBoardUser {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getCurrentUser()")
        val serverResponse = apiService.getCurrentUser()
        if (serverResponse.code() == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getCurrentUser(): HTTP_OK")
            val thingsBoardUser = serverResponse.body()
            if (thingsBoardUser != null) {
                return thingsBoardUser
            } else {
                throw IllegalStateException("ThingsBoardUser is null")
            }
        } else {
            throw IllegalStateException(
                "getCurrentUser() Error! ${
                    getServerErrorMessage(
                        serverResponse
                    )
                }"
            )
        }
    }

    suspend fun getAllDevicesForCustomer(
        customerUser: AirPowerUser
    ): List<Device> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getAllDevicesForCustomer()")
        val serverResponse = apiService.getCustomerDevices(
            customerId = customerUser.customerId,
            pageSize = 100, // TODO fixed page size, maybe in the future must use pagination
            page = 0
        )
        if (serverResponse.code() == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getAllDevicesForCustomer(): HTTP_OK")
            val devices = serverResponse.body()
            if (devices != null) {
                return devices.data
            } else {
                throw IllegalStateException("Devices list is null")
            }
        } else {
            throw IllegalStateException(
                "getDevices() Error! ${
                    getServerErrorMessage(
                        serverResponse
                    )
                }"
            )
        }

    }

    suspend fun refreshToken(
        onSuccess: () -> Unit
    ) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "refreshToken()")
        val jwtManager = JWTManager.getInstance()
        val token = jwtManager
            .getTokenForConnectionId(ThingsBoardConnectionContractImpl.getConnectionId())
        if (!jwtManager.isTokenValid(token)) throw IllegalStateException("Token is not valid")
        val refreshTokenQuery = Gson().toJson(RefreshTokenQuery(token.refreshToken))
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, refreshTokenQuery)
        val serverResponse = apiService.refreshToken(requestBody)
        if (serverResponse.code() == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "refreshToken(): HTTP_OK")
            jwtManager.handleRefreshToken(
                ThingsBoardConnectionContractImpl.getConnectionId(),
                serverResponse.body()
            ) {
                onSuccess.invoke()
            }
        } else {
            throw IllegalStateException(
                "refreshToken() Error! ${
                    getServerErrorMessage(
                        serverResponse
                    )
                }"
            )
        }
    }

    private fun getServerErrorMessage(
        serverResponse: Response<out Any>
    ): String {
        val responseCode = serverResponse.code().toString()
        var errorMessage = "unknown error"
        val errorBody = serverResponse.errorBody()?.string()
        if (errorBody != null) {
            errorMessage =
                Gson().fromJson(errorBody, ThingsBordErrorResponse::class.java).message
        }
        return "code: $responseCode message: $errorMessage"
    }
}