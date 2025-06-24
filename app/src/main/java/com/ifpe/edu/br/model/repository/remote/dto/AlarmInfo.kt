package com.ifpe.edu.br.model.repository.remote.dto

import java.util.UUID


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class AlarmInfo(
    val id: UUID,
    val type: String,
    val message: String,
    val timestamp: Long,
    val occurrence: Int
) {
    override fun toString(): String {
        return "AlarmInfo(type='$type', message='$message', timestamp='$timestamp', occurrence=$occurrence)"
    }
}