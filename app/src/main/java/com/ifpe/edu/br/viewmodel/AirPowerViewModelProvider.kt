package com.ifpe.edu.br.viewmodel

// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.ifpe.edu.br.core.api.ConnectionManager
import com.ifpe.edu.br.model.repository.remote.api.AirPowerServerConnectionContractImpl
import com.ifpe.edu.br.model.util.AirPowerLog

object AirPowerViewModelProvider {
    private val tag = AirPowerViewModelProvider::class.simpleName
    private var singletonViewModel: AirPowerViewModel? = null
    private val airPowerConnection =
        ConnectionManager.getInstance().getConnectionById(AirPowerServerConnectionContractImpl)
    fun getInstance(
        application: Application,
    ): AirPowerViewModel {
        if (singletonViewModel == null) {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.d(tag, "AirPowerViewModel -> create instance")
            val factory = AirPowerViewModelFactory(application, airPowerConnection)
            singletonViewModel = ViewModelProvider(
                ViewModelStore(),
                factory
            )[AirPowerViewModel::class.java]
        }
        return singletonViewModel!!
    }

    fun getInstance(): AirPowerViewModel {
        if (singletonViewModel == null) {
            val errorMessage =
                "${AirPowerViewModel::class.simpleName} build error: getInstance called before construction"
            throw IllegalStateException(errorMessage)
        }
        return singletonViewModel!!
    }
}