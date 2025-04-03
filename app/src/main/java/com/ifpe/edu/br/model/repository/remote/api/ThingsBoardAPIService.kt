package com.ifpe.edu.br.model.repository.remote.api

import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.remote.dto.Device
import com.ifpe.edu.br.model.repository.remote.dto.PageData
import com.ifpe.edu.br.model.repository.remote.dto.ThingsBoardUser
import com.ifpe.edu.br.model.repository.remote.dto.Token
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


interface ThingsBoardAPIService {

    @POST("/api/auth/login")
    suspend fun auth(@Body requestBody: RequestBody): Response<Token>

    @GET("/api/auth/user")
    suspend fun getCurrentUser(): Response<ThingsBoardUser>

    @POST("/api/auth/token")
    suspend fun refreshToken(@Body requestBody: RequestBody): Response<Token>

    @GET("api/customer/{customerId}/devices")
    suspend fun getCustomerDevices(
        @Path("customerId") customerId: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Query("sortProperty") sortProperty: String = "name",
        @Query("sortOrder") sortOrder: String = "ASC"
    ): Response<PageData<Device>>
}