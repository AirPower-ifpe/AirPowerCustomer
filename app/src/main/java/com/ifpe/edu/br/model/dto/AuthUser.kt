package com.ifpe.edu.br.model.dto


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.

data class AuthUser(
    val username: String,
    val password: String
) {
    override fun toString(): String {
        return "AuthUser" +
                "username='$username', " +
                "password='$password')"
    }
}