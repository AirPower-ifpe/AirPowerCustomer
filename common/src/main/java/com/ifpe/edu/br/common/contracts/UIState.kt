package com.ifpe.edu.br.common.contracts


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class UIState(
    val message: String,
    val stateCode: Int
) {
    override fun toString(): String {
        return "UIState(message='$message', stateCode=$stateCode)"
    }
}

