package com.ifpe.edu.br.model.repository.remote.dto


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class ThingsBordErrorResponse(
    val status: Int,
    val message: String,
    val errorCode: Int,
    val timestamp: String
){
    override fun toString(): String {
        return "ThingsBordErrorResponse(" +
                "status=$status, " +
                "message='$message', " +
                "errorCode=$errorCode, " +
                "timestamp=$timestamp)"
    }
}
