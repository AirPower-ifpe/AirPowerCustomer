package com.ifpe.edu.br.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.dto.AuthUser
import com.ifpe.edu.br.model.dto.ThingsBoardUser
import com.ifpe.edu.br.model.repo.ThingsBoardManager
import com.ifpe.edu.br.viewmodel.manager.JWTManager
import com.ifpe.edu.br.viewmodel.manager.ThingsBoardConnectionContractImpl
import com.ifpe.edu.br.viewmodel.manager.UIStateManager
import com.ifpe.edu.br.viewmodel.util.AirPowerLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private val thingsBoardMgr = ThingsBoardManager(connection)
    private var currentUser: ThingsBoardUser? = null;

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
                thingsBoardMgr.auth(
                    user = user,
                    onSuccess = onSuccessCallback
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

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                currentUser = thingsBoardMgr.getCurrentUser()
            } catch (e: Exception) {
                AirPowerLog.e(TAG, "getCurrentUser error ${e.message}")
            }
        }
    }

    fun updateSession(
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                thingsBoardMgr.refreshToken { onSuccessCallback.invoke() }
            } catch (e: Exception) {
                AirPowerLog.e(TAG, "updateSession error ${e.message}")
                onFailureCallback.invoke()
            }
        }
    }

    fun isSessionExpired(): Boolean {
        val connectionId = ThingsBoardConnectionContractImpl.getConnectionId()
        return JWTManager.getInstance().isTokenExpiredForConnection(connectionId)
    }
}