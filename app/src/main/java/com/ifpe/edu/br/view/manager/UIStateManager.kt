package com.ifpe.edu.br.view.manager

// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.

import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.common.contracts.UIStateManagerContract
import com.ifpe.edu.br.model.util.AirPowerLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UIStateManager : UIStateManagerContract {
    private val TAG = UIStateManager::class.simpleName

    private val booleanStates = mutableMapOf<String, MutableStateFlow<Boolean>>()
    private val stringStates = mutableMapOf<String, MutableStateFlow<String>>()
    private val intStates = mutableMapOf<String, MutableStateFlow<Int>>()
    private val uiStates = mutableMapOf<String, MutableStateFlow<UIState>>()

    override fun setBooleanState(id: String, value: Boolean) {
        val stateFlow = booleanStates.getOrPut(id) { MutableStateFlow(false) }
        if (AirPowerLog.ISVERBOSE) {
            AirPowerLog.d(TAG, "setBooleanState($id): current=${stateFlow.value}, new=$value")
        }
        stateFlow.value = value
    }

    override fun observeBoolean(id: String): StateFlow<Boolean> {
        return booleanStates.getOrPut(id) { MutableStateFlow(false) }.asStateFlow()
    }

    override fun setStringState(id: String, value: String) {
        val stateFlow = stringStates.getOrPut(id) { MutableStateFlow("") }
        if (AirPowerLog.ISVERBOSE) {
            AirPowerLog.d(TAG, "setStringState($id): current=\"${stateFlow.value}\", new=\"$value\"")
        }
        stateFlow.value = value
    }

    override fun observeString(id: String): StateFlow<String> {
        return stringStates.getOrPut(id) { MutableStateFlow("") }.asStateFlow()

    }

    override fun setIntState(id: String, value: Int) {
        val stateFlow = intStates.getOrPut(id) { MutableStateFlow(0) }
        if (AirPowerLog.ISVERBOSE) {
            AirPowerLog.d(TAG, "setIntState($id): current=${stateFlow.value}, new=$value")
        }
        stateFlow.value = value
    }

    override fun observeInt(id: String): StateFlow<Int> {
        return intStates.getOrPut(id) { MutableStateFlow(0) }.asStateFlow()
    }

    override fun setUIState(id: String, value: UIState) {
        val stateFlow = uiStates.getOrPut(id) { MutableStateFlow(UIState("", 0)) }
        if (AirPowerLog.ISVERBOSE) {
            AirPowerLog.d(
                TAG,
                "setUIState($id): current={message=\"${stateFlow.value.message}\", code=${stateFlow.value.stateCode}}, " +
                        "new={message=\"${value.message}\", code=${value.stateCode}}"
            )
        }
        stateFlow.value = value
    }

    override fun observeUIState(id: String): StateFlow<UIState> {
        return uiStates.getOrPut(id) {
            MutableStateFlow(UIState("", 0))
        }.asStateFlow()
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