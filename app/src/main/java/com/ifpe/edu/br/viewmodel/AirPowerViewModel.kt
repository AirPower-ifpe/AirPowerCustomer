package com.ifpe.edu.br.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.error.ErrorCode
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.ResultWrapper
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
    private val minDelay = 1500L

    fun initSession(
        user: AuthUser,
    ) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val uiStateKey = Constants.UIStateKey.LOGIN_KEY
            uiStateManager.setUIState(
                uiStateKey,
                UIState(Constants.UIState.STATE_LOADING)
            )
            var isAuthSuccess = false
            when (val authResponse = repository.authenticate(user = user)) {
                is ResultWrapper.ApiError -> {
                    delay(getTimeLeftDelay(startTime))
                    handleApiError(authResponse.errorCode, uiStateKey)
                }

                is ResultWrapper.NetworkError -> {
                    delay(getTimeLeftDelay(startTime))
                    handleNetworkError(uiStateKey)
                }

                is ResultWrapper.Success<*> -> {
                    isAuthSuccess = true
                }
            }

            var isGetUserSuccess = false
            when (val currentUserResponse = repository.retrieveCurrentUser()) {
                is ResultWrapper.ApiError -> {
                    delay(getTimeLeftDelay(startTime))
                    handleApiError(
                        currentUserResponse.errorCode,
                        uiStateKey
                    )
                }

                is ResultWrapper.NetworkError -> {
                    delay(getTimeLeftDelay(startTime))
                    handleNetworkError(uiStateKey)
                }

                is ResultWrapper.Success<*> -> {
                    isGetUserSuccess = true
                }
            }

            if (isAuthSuccess && isGetUserSuccess) {
                delay(getTimeLeftDelay(startTime))
                uiStateManager.setUIState(
                    uiStateKey,
                    UIState(Constants.UIState.STATE_SUCCESS)
                )
            }

        }
    }

    private fun getTimeLeftDelay(startTime: Long): Long {
        val timeDelayed = System.currentTimeMillis() - startTime
        return (minDelay - timeDelayed).coerceAtLeast(0L)
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

    fun updateSession() {
        viewModelScope.launch {
            val uiStateKey = Constants.UIStateKey.REFRESH_TOKEN_KEY
            when (val refreshTokenResultWrapper = repository.updateSession()) {
                is ResultWrapper.ApiError -> {
                    handleApiError(refreshTokenResultWrapper.errorCode, uiStateKey)
                }

                is ResultWrapper.NetworkError -> {
                    handleNetworkError(uiStateKey)
                }

                is ResultWrapper.Success<*> -> {
                    uiStateManager.setUIState(
                        uiStateKey,
                        UIState(Constants.UIState.STATE_SUCCESS)
                    )
                }
            }
        }
    }

    fun isSessionExpired() {
        viewModelScope.launch {
            val uiStateKey = Constants.UIStateKey.SESSION
            if (repository.isSessionExpired()) {
                uiStateManager.setUIState(
                    uiStateKey,
                    UIState(Constants.UIState.STATE_REFRESH_TOKEN)
                )
            } else {
                uiStateManager.setUIState(
                    uiStateKey,
                    UIState(Constants.UIState.STATE_SUCCESS)
                )
            }
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
        uiStateManager.setUIState(stateId, getEmptyValueUIState())
    }

    fun startDataFetchers() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "startDataFetchers()")

        if (devicesJob?.isActive != true) {
            devicesJob = startDevicesFetcher()
        }
    }

    private fun handleException(e: Exception) {
//        uiStateManager.setUIState(
//            Constants.UIState.STATE_ERROR,
//            UIState(
//                "[$TAG] : -> ${e.message}",
//                Constants.DeprecatedValues.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED
//            )
//        )
    }

    private fun handleTokenExpiredException(e: TokenExpiredException) {
//        uiStateManager.setUIState(
//            Constants.UIState.STATE_ERROR,
//            UIState(
//                "[$TAG] : -> ${e.message}",
//                Constants.DeprecatedValues.THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED
//            )
//        )
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

    private fun getEmptyValueUIState(): UIState {
        return UIState(Constants.UIState.EMPTY_STATE)
    }

    private fun handleApiError(
        code: ErrorCode,
        uiStateKey: String
    ) {

        when (code) {
            ErrorCode.TB_INVALID_CREDENTIALS -> {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.d(TAG, "TB_INVALID_CREDENTIALS -> STATE_AUTHENTICATION_FAILURE")
                uiStateManager.setUIState(
                    uiStateKey, UIState(Constants.UIState.STATE_AUTHENTICATION_FAILURE)
                )
            }

            ErrorCode.TB_REFRESH_TOKEN_EXPIRED -> {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.d(TAG, "TB_REFRESH_TOKEN_EXPIRED -> STATE_REQUEST_LOGIN")
                uiStateManager.setUIState(
                    uiStateKey,
                    UIState(Constants.UIState.STATE_REQUEST_LOGIN)
                )
            }

            ErrorCode.AP_REFRESH_TOKEN_EXPIRED -> {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.d(TAG, "AP_REFRESH_TOKEN_EXPIRED -> STATE_REQUEST_LOGIN")
                uiStateManager.setUIState(
                    uiStateKey,
                    UIState(Constants.UIState.STATE_REQUEST_LOGIN)
                )
            }

            ErrorCode.AP_JWT_EXPIRED -> {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.d(TAG, "AP_JWT_EXPIRED -> STATE_UPDATE_SESSION")
                uiStateManager.setUIState(
                    uiStateKey, UIState(Constants.UIState.STATE_UPDATE_SESSION)
                )
            }

            else -> {
                if (AirPowerLog.ISVERBOSE)
                    AirPowerLog.d(TAG, "else -> GENERIC_ERROR")
                uiStateManager.setUIState(
                    uiStateKey, UIState(Constants.UIState.GENERIC_ERROR)
                )
            }
        }
    }

    private fun handleNetworkError(uiStateKey: String) {
        uiStateManager.setUIState(
            uiStateKey, UIState(
                Constants.UIState.STATE_NETWORK_ISSUE
            )
        )
    }

    fun requestLogin(stateId: String) {
        uiStateManager.setUIState(
            stateId, UIState(Constants.UIState.STATE_REQUEST_LOGIN)
        )
    }

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }
}