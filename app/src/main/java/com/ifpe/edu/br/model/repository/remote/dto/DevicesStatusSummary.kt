package com.ifpe.edu.br.model.repository.remote.dto

import android.icu.text.AlphabeticIndex.Bucket.LabelType


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


data class DevicesStatusSummary(
    val label: String,
    val occurrence: Int
) {
    override fun toString(): String {
        return "DevicesStatusSummary(label='$label', occurrence=$occurrence)"
    }
}