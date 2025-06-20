package com.ifpe.edu.br.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AuthenticateFailureException
import com.ifpe.edu.br.model.util.TokenExpiredException
import com.ifpe.edu.br.view.manager.UIStateManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
    private var repository = Repository.getInstance()
    private var currentUserJob: Job? = null
    private var devicesJob: Job? = null
    private var telemetryJob: Job? = null
    private val devicesFetchInterval = 5_000L

    fun initSession(
        user: AuthUser,
    ) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val minDelay = 1500L
            uiStateManager.setUIState(
                Constants.UIState.AUTH_STATE,
                UIState(
                    message = "Loading",
                    stateCode = CommonConstants.State.STATE_LOADING
                )
            )
            var uiState = getDefaultUIState()
            try {
                repository.authenticate(user = user)
                repository.retrieveCurrentUser()
                uiState = UIState("", CommonConstants.State.STATE_SUCCESS)
            } catch (e: AuthenticateFailureException) {
                uiState = UIState("${e.message}", CommonConstants.State.STATE_AUTH_FAILURE)
            } catch (e: Exception) {
                uiState = UIState("${e.message}", CommonConstants.State.STATE_SERVER_INTERNAL_ISSUE)
            } finally {
                val timeDelayed = System.currentTimeMillis() - startTime
                val timeLeft = (minDelay - timeDelayed).coerceAtLeast(0L)
                delay(timeLeft)
                uiStateManager.setUIState(Constants.UIState.AUTH_STATE, uiState)
            }
        }
    }

    fun getDevicesSummary(): LiveData<List<DeviceSummary>> {
        return repository.devicesSummary
    }

    fun getAggregatedTelemetry(
        query: AggregatedTelemetryQuery?,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.getAggregatedTelemetry(
                    query = AggregatedTelemetryQuery(
                        deviceIds = listOf(
                            "e6dfad10-416b-11f0-918d-8b1a89ef9dab",
                            "655eae80-4148-11f0-918d-8b1a89ef9dab"
                        ),
                        telemetryKeys = listOf("voltage", "current", "power"),
                        aggregationFunction = "AVG",
                        timeWindowHours = 12
                    ),
                    onSuccess = {},
                    onFailureCallback = {}
                )
            } catch (e: Exception) {
                AirPowerLog.e(TAG, "DEU MERDA AQUI HEIN: ${e.message}")
                // todo adicionar trabatamento aqui
            }
        }
    }

    fun updateSession(
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateSession {
                    uiStateManager.setUIState(
                        Constants.UIState.STATE_ERROR, getDefaultUIState()
                    )
                    onSuccessCallback.invoke()
                }
            } catch (e: Exception) {
                uiStateManager.setUIState(
                    Constants.UIState.STATE_ERROR,
                    UIState(
                        "[$TAG]: -> ${e.message}",
                        Constants.ResponseErrorCodes.INVALID_AIRPOWER_TOKEN
                    )
                )
                onFailureCallback.invoke()
            }
        }
    }


    fun isTokenExpired(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isExpired = repository.isSessionExpired()
            callback(isExpired)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            currentUserJob?.cancel()
            devicesJob?.cancel()
        }
    }

    fun resetUIState(stateId: String) {
        uiStateManager.setUIState(stateId, getDefaultUIState())
    }

    fun startDataFetchers() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "startDataFetchers()")

        if (devicesJob?.isActive != true) {
            devicesJob = startDevicesFetcher()
        }
    }

    private fun handleException(e: Exception) {
        uiStateManager.setUIState(
            Constants.UIState.STATE_ERROR,
            UIState(
                "[$TAG] : -> ${e.message}",
                Constants.DeprecatedValues.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED
            )
        )
    }

    private fun handleTokenExpiredException(e: TokenExpiredException) {
        uiStateManager.setUIState(
            Constants.UIState.STATE_ERROR,
            UIState(
                "[$TAG] : -> ${e.message}",
                Constants.DeprecatedValues.THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED
            )
        )
    }


    private fun startDevicesFetcher(): Job {
        return viewModelScope.launch {
            try {
                while (isActive) {
                    repository.retrieveDeviceSummaryForCurrentUser()
                    delay(devicesFetchInterval)
                }
            } catch (e: TokenExpiredException) {
                handleTokenExpiredException(e)
                // todo adicionar tratamento aqui
            } catch (e: Exception) {
                handleException(e)
                // todo adicionar tratamento aqui
            }
        }
    }

    private fun getDefaultUIState(): UIState {
        return UIState(
            CommonConstants.State.STATE_DEFAULT_MESSAGE,
            CommonConstants.State.STATE_DEFAULT_SATATE_CODE
        )
    }
}