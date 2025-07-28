package com.ifpe.edu.br.model.repository.remote.api

import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.AllMetricsWrapper
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.NotificationItem
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggDataWrapperResponse
import com.ifpe.edu.br.model.repository.remote.dto.auth.Token
import com.ifpe.edu.br.model.repository.remote.dto.user.ThingsBoardUser
import okhttp3.RequestBody
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
    suspend fun refreshToken(@Body requestBody: RequestBody): Token

    @POST("/api/v1/auth/login")
    suspend fun auth(@Body requestBody: RequestBody): Token

    @GET("/api/v1/user/me")
    suspend fun getCurrentUser(): ThingsBoardUser

    @GET("/api/v1/user/{userId}/devices-summary")
    suspend fun getDeviceSummariesForUser(
        @Path("userId") userId: String
    ): List<DeviceSummary>

    @GET("api/v1/alarms/me")
    suspend fun getAlarmsForCurrentUser(): List<AlarmInfo>

    @GET("api/v1/telemetry/user/{groupID}/devices-metrics")
    suspend fun getDevicesMetricsWrapper(
        @Path("groupID") groupID: String
    ): List<AllMetricsWrapper>

    @POST("/api/v1/agg-data/telemetry")
    suspend fun getDeviceAggregatedDataWrapper(@Body requestBody: RequestBody): AggDataWrapperResponse

    @GET("api/v1/notifications/me")
    suspend fun getNotificationsForCurrentUser(): List<NotificationItem>
}