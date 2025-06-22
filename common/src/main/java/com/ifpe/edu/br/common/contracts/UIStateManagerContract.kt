package com.ifpe.edu.br.common.contracts

import kotlinx.coroutines.flow.StateFlow


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


interface UIStateManagerContract {
    fun setBooleanState(id: String, value: Boolean)
    fun observeBoolean(id: String): StateFlow<Boolean>
    fun setStringState(id: String, value: String)
    fun observeString(id: String): StateFlow<String>
    fun setIntState(id: String, value: Int)
    fun observeInt(id: String): StateFlow<Int>
    fun setUIState(id: String, value: UIState)
    fun observeUIState(id: String): StateFlow<UIState>
}