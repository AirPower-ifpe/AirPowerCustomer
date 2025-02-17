package com.ifpe.edu.br.common.contracts

import androidx.lifecycle.LiveData


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


interface UIStateManagerContract {
    fun setBooleanState(id: String, value: Boolean)
    fun observeBoolean(id: String): LiveData<Boolean>
    fun setStringState(id: String, value: String)
    fun observeString(id: String): LiveData<String>
    fun setIntState(id: String, value: Int)
    fun observeInt(id: String): LiveData<Int>
}