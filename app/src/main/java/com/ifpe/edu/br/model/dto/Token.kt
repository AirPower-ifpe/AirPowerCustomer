package com.ifpe.edu.br.model.dto


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class Token(
    val token: String,
    val refreshToken: String,
    val scope: String
) {
    override fun toString(): String {
        return "Token(token='$token', " +
                "refreshToken='$refreshToken', " +
                "scope=$scope)"
    }
}
