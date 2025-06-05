package com.ifpe.edu.br.model.repository.model


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


import androidx.annotation.DrawableRes

data class DeviceCardModel(
    val id: String,
    val label: String,
    val status: String,
    @DrawableRes val iconResId: Int? = null
)