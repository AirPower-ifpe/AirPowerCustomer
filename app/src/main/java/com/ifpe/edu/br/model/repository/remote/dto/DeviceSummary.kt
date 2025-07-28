package com.ifpe.edu.br.model.repository.remote.dto

import java.util.UUID

// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class DeviceSummary(
    val id: UUID,
    val name: String,
    val label: String?,
    val type: String?,
    val isActive: Boolean
) {
    override fun toString(): String {
        return "DeviceSummary(id=$id, " +
                "name='$name', " +
                "label=$label, " +
                "type=$type, " +
                "isActive=$isActive)"
    }
}