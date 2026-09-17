package com.ifpe.edu.br

import android.app.Application
import android.content.Context
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.viewmodel.AirPowerViewModelProvider
import com.ifpe.edu.br.model.repository.persistence.manager.SharedPrefManager
import com.ifpe.edu.br.model.util.AirPowerLog

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

class AirPowerApplication : Application() {
    private val tag = AirPowerApplication::class.simpleName

    companion object {
        private lateinit var instance: AirPowerApplication
        fun getContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(tag, "onCreate()")
        instance = this
        Repository.build(applicationContext)
        AirPowerViewModelProvider.getInstance(this)
        super.onCreate()
    }
}