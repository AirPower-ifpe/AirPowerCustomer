package com.ifpe.edu.br.model.repository.remote.api

import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.TelemetryAggregationResponse
import com.ifpe.edu.br.model.repository.remote.dto.user.AirPowerBoardUser
import com.ifpe.edu.br.model.repository.remote.dto.auth.Token
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


interface AirPowerServerAPIService {

    @POST("/api/v1/auth/token")
    suspend fun refreshToken(@Body requestBody: RequestBody): Response<Token>

    @POST("/api/v1/auth/login")
    suspend fun auth(@Body requestBody: RequestBody): Token

    @GET("/api/v1/user/me")
    suspend fun getCurrentUser(): AirPowerBoardUser

    @POST("/test/api/v1/devices/telemetry/aggregate")
    suspend fun getAggregatedTelemetry(@Body requestBody: RequestBody): Response<TelemetryAggregationResponse>

    @GET("/test/api/v1/user/{userId}/devices-summary")
    suspend fun getDeviceSummariesForUser(
        @Path("userId") userId: String
    ): Response<List<DeviceSummary>>
}