package com.ifpe.edu.br.model.repository.remote.api

import com.ifpe.edu.br.model.repository.remote.dto.TelemetryAggregationResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


interface AirPowerServerAPIService {

    @POST("/test/api/v1/devices/telemetry/aggregate")
    suspend fun getAggregatedTelemetry(@Body requestBody: RequestBody): Response<TelemetryAggregationResponse>
}