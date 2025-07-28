package com.ifpe.edu.br.common.contracts


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class UIState(
    val state: String,
) {
    override fun toString(): String {
        return "UIState(message='$state')"
    }
}

