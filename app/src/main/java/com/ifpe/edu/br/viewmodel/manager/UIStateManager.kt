package com.ifpe.edu.br.viewmodel.manager

// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifpe.edu.br.common.contracts.UIStateManagerContract

class UIStateManager : UIStateManagerContract {
    private val booleanStates = mutableMapOf<String, MutableLiveData<Boolean>>()
    private val stringStates = mutableMapOf<String, MutableLiveData<String>>()
    private val intStates = mutableMapOf<String, MutableLiveData<Int>>()

    override fun setBooleanState(id: String, value: Boolean) {
        booleanStates.getOrPut(id) { MutableLiveData() }.postValue(value)
    }

    override fun observeBoolean(id: String): LiveData<Boolean> {
        return booleanStates.getOrPut(id) { MutableLiveData() }
    }

    override fun setStringState(id: String, value: String) {
        stringStates.getOrPut(id) { MutableLiveData() }.postValue(value)
    }

    override fun observeString(id: String): LiveData<String> {
        return stringStates.getOrPut(id) { MutableLiveData() }
    }

    override fun setIntState(id: String, value: Int) {
        intStates.getOrPut(id) { MutableLiveData() }.postValue(value)
    }

    override fun observeInt(id: String): LiveData<Int> {
        return intStates.getOrPut(id) { MutableLiveData() }
    }

    companion object {
        @Volatile
        private var instance: UIStateManagerContract? = null
        fun getInstance(): UIStateManagerContract {
            return instance ?: synchronized(this) {
                instance ?: UIStateManager().also { instance = it }
            }
        }
    }
}