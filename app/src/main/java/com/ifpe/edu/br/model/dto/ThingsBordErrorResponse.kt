package com.ifpe.edu.br.model.dto


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class ThingsBordErrorResponse(
    val status: Int,
    val message: String,
    val errorCode: Int,
    val timestamp: Long
)
