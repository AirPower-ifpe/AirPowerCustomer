package com.ifpe.edu.br.model.api

import com.ifpe.edu.br.model.dto.Token
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


interface ThingsBoardAPIService {

    @POST("/api/auth/login")
    suspend fun auth(@Body requestBody: RequestBody): Response<Token>
}