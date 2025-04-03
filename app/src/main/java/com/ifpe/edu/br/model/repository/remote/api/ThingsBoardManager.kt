package com.ifpe.edu.br.model.repository.remote.api


import com.google.gson.Gson
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser
import com.ifpe.edu.br.model.repository.remote.dto.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.Device
import com.ifpe.edu.br.model.repository.remote.dto.ThingsBoardUser
import com.ifpe.edu.br.model.repository.remote.dto.ThingsBordErrorResponse
import com.ifpe.edu.br.model.repository.remote.query.RefreshTokenQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AuthenticateFailureException
import com.ifpe.edu.br.model.util.InvalidStateException
import com.ifpe.edu.br.model.util.TokenExpiredException
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
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "auth()")
        val userJson = Gson().toJson(user)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, userJson)
        val serverResponse = apiService.auth(requestBody)
        val responseCode = serverResponse.code()
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "Authentication success: HTTP_OK")
            serverResponse.body()?.let {
                JWTManager.handleAuthentication(
                    ThingsBoardConnectionContractImpl.getConnectionId(),
                    it
                ) { onSuccess.invoke() }
            }
        } else {
            val serverErrorWrapper = getServerErrorWrapper(serverResponse)
            throw AuthenticateFailureException("[$TAG]: -> Authentication failure: message:${serverErrorWrapper.message}")
        }
    }

    suspend fun getCurrentUser(): ThingsBoardUser {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getCurrentUser()")
        val serverResponse = apiService.getCurrentUser()
        if (serverResponse.code() == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getCurrentUser(): HTTP_OK")
            val thingsBoardUser = serverResponse.body()
            if (thingsBoardUser != null) {
                return thingsBoardUser
            } else {
                throw InvalidStateException("[$TAG]: ThingsBoardUser is null")
            }
        } else if (serverResponse.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
            val serverErrorWrapper = getServerErrorWrapper(serverResponse)
            when (serverErrorWrapper.errorCode) {
                Constants.THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED -> {
                    if (AirPowerLog.ISVERBOSE) AirPowerLog.w(TAG, "THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED")
                    throw TokenExpiredException("[$TAG]: message: ${serverErrorWrapper.message}")
                }

                Constants.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED -> {
                    if (AirPowerLog.ISVERBOSE) AirPowerLog.w(TAG, "THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED")
                    throw AuthenticateFailureException("[$TAG]: message: ${serverErrorWrapper.message}")
                }
            }
        }
        if (AirPowerLog.ISVERBOSE) AirPowerLog.e(TAG, "untracked failure:")
        throw InvalidStateException("[$TAG]: untracked failure: server code: ${serverResponse.code()}")
    }

    suspend fun getAllDevicesForCustomer(
        customerUser: AirPowerUser
    ): List<Device> {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG,
            "getAllDevicesForCustomer(): ${customerUser.firstName}"
        )
        val serverResponse = apiService.getCustomerDevices(
            customerId = customerUser.customerId,
            pageSize = 100, // TODO fixed page size, maybe in the future must use pagination
            page = 0
        )
        if (serverResponse.code() == HttpsURLConnection.HTTP_OK) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(
                TAG,
                "getAllDevicesForCustomer(): server response: ${serverResponse.code()}"
            )
            val devices = serverResponse.body()
            if (devices != null) {
                return devices.data
            } else {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.e(TAG, "[Devices list is null]") // todo remover
                throw IllegalStateException("$TAG: [Devices list is null]")
            }
        } else if (serverResponse.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
            val serverErrorWrapper = getServerErrorWrapper(serverResponse)
            when (serverErrorWrapper.errorCode) {
                Constants.THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED -> {
                    if (AirPowerLog.ISVERBOSE) AirPowerLog.w(TAG, "THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED")
                    throw TokenExpiredException("[$TAG]: message: ${serverErrorWrapper.message}")
                }

                Constants.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED -> {
                    if (AirPowerLog.ISVERBOSE) AirPowerLog.w(TAG, "THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED")
                    throw AuthenticateFailureException("[$TAG]: message: ${serverErrorWrapper.message}")
                }
            }
        } else {
            throw IllegalStateException("[$TAG]: untracked failure: server code: ${serverResponse.code()}")
        }
        return emptyList()
    }

    suspend fun refreshToken(
        onSuccess: () -> Unit
    ) {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "refreshToken()")
        val jwtManager = JWTManager
        val token = jwtManager
            .getTokenForConnectionId(ThingsBoardConnectionContractImpl.getConnectionId())
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
                    ThingsBoardConnectionContractImpl.getConnectionId(),
                    it
                ) {
                    onSuccess.invoke()
                }
            }
        } else if (serverResponse.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.w(TAG, "refreshToken(): HTTP_401")
            val serverErrorWrapper = getServerErrorWrapper(serverResponse)
            when (serverErrorWrapper.errorCode) {
                Constants.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED -> {
                    throw AuthenticateFailureException(
                        "[$TAG]:AUTHENTICATION_FAILED -> server message: ${serverErrorWrapper.message}")
                }
                else -> {throw InvalidStateException(
                        "[$TAG]:Unhandled state -> server message: ${serverErrorWrapper.message}")}
            }
        } else {
            throw InvalidStateException(
                "[$TAG]: Unhandled state: response code: ${serverResponse.code()}")
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