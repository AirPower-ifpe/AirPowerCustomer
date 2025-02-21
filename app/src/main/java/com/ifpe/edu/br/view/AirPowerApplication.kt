package com.ifpe.edu.br.view

import android.app.Application
import com.ifpe.edu.br.model.repo.AirPowerRepository
import com.ifpe.edu.br.viewmodel.AirPowerViewModelProvider
import com.ifpe.edu.br.viewmodel.manager.SharedPrefManager
import com.ifpe.edu.br.viewmodel.util.AirPowerLog

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

class AirPowerApplication : Application() {
    private val tag = AirPowerApplication::class.simpleName
    override fun onCreate() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(tag, "onCreate()")
        SharedPrefManager.getInstance(applicationContext)
        AirPowerRepository.build(applicationContext)
        AirPowerViewModelProvider.getInstance(this)
        super.onCreate()
    }
}