package com.ifpe.edu.br.model.repository.remote.dto.error


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class AirPowerErrorResponse(
    val status: Int,
    val errorCode: Int,
    val message: String,
    val timestamp: Long
){
    override fun toString(): String {
        return "AirPowerErrorResponse(" +
                "status=$status, " +
                "errorCode=$errorCode, " +
                "message='$message', " +
                "timestamp=$timestamp)"
    }
}
