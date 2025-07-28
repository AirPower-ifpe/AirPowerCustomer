package com.ifpe.edu.br.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import retrofit2.Retrofit


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


class AirPowerViewModelFactory(
    private val application: Application,
    private val connection: Retrofit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AirPowerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AirPowerViewModel(application, connection) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}