package com.ifpe.edu.br.common.contracts


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class ErrorState(
    val message: String,
    val errorCode: Int
) {
    override fun toString(): String {
        return "ErrorState(message='$message', errorCode=$errorCode)"
    }
}

