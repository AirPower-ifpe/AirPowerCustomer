package com.ifpe.edu.br.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.model.repository.model.TelemetryDataWrapper
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.AllMetricsWrapper
import com.ifpe.edu.br.model.repository.remote.dto.DashBoardDataWrapper
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.NotificationItem
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.error.ErrorCode
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.ResultWrapper
import com.ifpe.edu.br.view.manager.UIStateManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.util.UUID

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
    private val jobs: MutableMap<String, Job> = mutableMapOf()
    private val devicesFetchInterval = 5_000L
    private val minDelay = 1500L
    private val minDelayCard = 800L
    private val DEVICE_JOB = "DEVICE_JOB"
    private val ALARMS_JOB = "ALARMS_JOB"

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
                handleSuccess(uiStateKey)
            }
        }
    }

    private fun getTimeLeftDelay(startTime: Long): Long {
        val timeDelayed = System.currentTimeMillis() - startTime
        return (minDelay - timeDelayed).coerceAtLeast(0L)
    }

    private fun getTimeLeftDelayCard(startTime: Long): Long {
        val timeDelayed = System.currentTimeMillis() - startTime
        return (minDelayCard - timeDelayed).coerceAtLeast(0L)
    }

    fun getDevicesSummary(): LiveData<List<DeviceSummary>> {
        return repository.devicesSummary
    }

    fun getAggregatedTelemetry(
        query: AggregatedTelemetryQuery
    ) {
        viewModelScope.launch {
            val aggStateKey = Constants.UIStateKey.AGG_TELEMETRY_STATE
            when (val aggResultWrapper = repository.getAggregatedTelemetry(query)) {
                is ResultWrapper.Success -> {
                    handleSuccess(aggStateKey)
                }

                is ResultWrapper.ApiError -> {
                    handleApiError(aggResultWrapper.errorCode, aggStateKey)
                }

                ResultWrapper.NetworkError -> {
                    handleNetworkError(aggStateKey)
                }
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
                    handleSuccess(uiStateKey)
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
                handleSuccess(uiStateKey)
            }
        }
    }

    private fun handleSuccess(aggStateKey: String) {
        uiStateManager.setUIState(
            aggStateKey,
            UIState(Constants.UIState.STATE_SUCCESS)
        )
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            stopAllFetchers()
        }
    }

    fun resetUIState(stateId: String) {
        uiStateManager.setUIState(stateId, getEmptyValueUIState())
    }

    fun startDataFetchers() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "startDataFetchers()")
        if (jobs[DEVICE_JOB]?.isActive != true) {
            jobs[DEVICE_JOB] = fetchDevicesData()
        }
        if (jobs[ALARMS_JOB]?.isActive != true) {
            jobs[ALARMS_JOB] = fetchAlarmData()
        }
    }

    private fun fetchDevicesData(): Job {
        return viewModelScope.launch {
            val deviceSummarySummaryKey = Constants.UIStateKey.DEVICE_SUMMARY_KEY
            val uiStateKey = Constants.UIStateKey.SESSION
            while (isActive) {
                when (val resultWrapper = repository.retrieveDeviceSummaryForCurrentUser()) {
                    is ResultWrapper.Success -> {
                        handleSuccess(deviceSummarySummaryKey)
                    }

                    is ResultWrapper.ApiError -> {
                        handleApiError(resultWrapper.errorCode, deviceSummarySummaryKey)
                        handleApiError(resultWrapper.errorCode, uiStateKey)
                    }

                    ResultWrapper.NetworkError -> {
                        handleNetworkError(deviceSummarySummaryKey)
                        handleNetworkError(uiStateKey)
                    }
                }
                delay(devicesFetchInterval)
            }
        }
    }

    fun fetchAllDevicesMetricsWrapper(): Job {
        return viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val sessionStateKey = Constants.UIStateKey.SESSION
            val fetchMetricsKey = Constants.UIStateKey.METRICS_KEY
            uiStateManager.setUIState(fetchMetricsKey, UIState(Constants.UIState.STATE_LOADING))
            when (val resultWrapper = repository.fetchAllDevicesMetricsWrapper()) {
                is ResultWrapper.Success -> {
                    handleSuccess(sessionStateKey)
                }

                is ResultWrapper.ApiError -> {
                    handleApiError(resultWrapper.errorCode, sessionStateKey)
                }

                ResultWrapper.NetworkError -> {
                    handleNetworkError(sessionStateKey)
                }
            }
            delay(getTimeLeftDelayCard(startTime))
            uiStateManager.setUIState(fetchMetricsKey, UIState(Constants.UIState.STATE_SUCCESS))
        }
    }

    fun fetchAllDashboardsMetricsWrapper(): Job {
        return viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val sessionStateKey = Constants.UIStateKey.SESSION
            val fetchMetricsKey = Constants.UIStateKey.METRICS_KEY
            uiStateManager.setUIState(fetchMetricsKey, UIState(Constants.UIState.STATE_LOADING))
            when (val resultWrapper = repository.fetchAllDashboardsMetricsWrapper()) {
                is ResultWrapper.Success -> {
                    handleSuccess(sessionStateKey)
                }

                is ResultWrapper.ApiError -> {
                    handleApiError(resultWrapper.errorCode, sessionStateKey)
                }

                ResultWrapper.NetworkError -> {
                    handleNetworkError(sessionStateKey)
                }
            }
            delay(getTimeLeftDelayCard(startTime))
            uiStateManager.setUIState(fetchMetricsKey, UIState(Constants.UIState.STATE_SUCCESS))
        }
    }

    private fun fetchAlarmData(): Job {
        return viewModelScope.launch {
            val alarmsKey = Constants.UIStateKey.ALARMS_KEY
            val uiStateKey = Constants.UIStateKey.SESSION
            while (isActive) {
                when (val resultWrapper = repository.retrieveAlarmInfo()) {
                    is ResultWrapper.Success -> {
                        handleSuccess(alarmsKey)
                    }

                    is ResultWrapper.ApiError -> {
                        handleApiError(resultWrapper.errorCode, alarmsKey)
                        handleApiError(resultWrapper.errorCode, uiStateKey)
                    }

                    ResultWrapper.NetworkError -> {
                        handleNetworkError(alarmsKey)
                        handleNetworkError(uiStateKey)
                    }
                }
                delay(devicesFetchInterval)
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
        if (AirPowerLog.ISVERBOSE)
            AirPowerLog.d(TAG, "handleApiError: code:$code stateKey:$uiStateKey")
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
                    uiStateKey,
                    UIState(Constants.UIState.STATE_UPDATE_SESSION)
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

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun getDeviceById(deviceId: String): DeviceSummary {
        return repository.getDeviceById(deviceId)
    }


    fun getAlarmInfoSet(): StateFlow<List<AlarmInfo>> {
        return repository.alarmInfo
    }


    fun getChartDataWrapper(id: UUID): StateFlow<TelemetryDataWrapper> {
        return repository.getChartDataWrapper(id)
    }

    fun getAllDevicesMetricsWrapper(): StateFlow<AllMetricsWrapper> {
        return repository.allDevicesMetricsWrapper
    }

    fun getUserDashBoardsDataWrapper(): StateFlow<List<AllMetricsWrapper>> {
        return repository.dashBoardsMetricsWrapper
    }

    fun getNotifications(): StateFlow<List<NotificationItem>> {
        return repository.getNotifications()
    }

    fun stopAllFetchers() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "stopAllFetchers()")
        jobs.values.forEach { job ->
            job.cancel()
        }
    }
}