package com.ifpe.edu.br.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.model.repository.remote.dto.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.Device
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.view.manager.UIStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

class AirPowerViewModel(
    application: Application,
    connection: Retrofit
) : AndroidViewModel(application) {

    private val TAG: String = AirPowerViewModel::class.java.simpleName
    val uiStateManager = UIStateManager.getInstance()
    private var repository = Repository.getInstance()
    private val fetchInterval = 10_000L
    val devices: MutableLiveData<List<Device>> get() = repository.devices
    val currentUser = repository.currentUser

    fun startFetchingDevices() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "startFetchingDevices")
        viewModelScope.launch {
            while (true) {
                try {
                    repository.getDevicesForCurrentUser()
                } catch (e: Exception) {
                    AirPowerLog.e(TAG, "Error fetching devices periodically: ${e.message}")
                }
                delay(fetchInterval)
            }
        }
    }

    fun authenticate(
        user: AuthUser,
        onSuccessCallback: () -> Unit
    ) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val minDelay = 1000L
            try {
                uiStateManager.setBooleanState(
                    Constants.STATE_AUTH_LOADING,
                    true
                )
                repository.authenticate(
                    user = user,
                    onSuccessCallback = onSuccessCallback
                )
                val timeDelayed = System.currentTimeMillis() - startTime
                val timeLeft = (minDelay - timeDelayed).coerceAtLeast(0L)
                delay(timeLeft)
                uiStateManager.setBooleanState(Constants.STATE_CONNECTION_FAILURE, false)
                uiStateManager.setBooleanState(Constants.STATE_AUTH_FAILURE, false)
            } catch (e: Exception) {
                AirPowerLog.e(TAG, "Authentication error: ${e.message}")
                uiStateManager.setBooleanState(Constants.STATE_AUTH_FAILURE, true)
            } finally {
                uiStateManager.setBooleanState(
                    Constants.STATE_AUTH_LOADING,
                    false
                )
            }
        }
    }

    fun updateSession(
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateSession { onSuccessCallback.invoke() }
            } catch (e: Exception) {
                AirPowerLog.e(TAG, "updateSession error ${e.message}")
                onFailureCallback.invoke()
            }
        }
    }

    fun isSessionExpired(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                callback(repository.isSessionExpired())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}