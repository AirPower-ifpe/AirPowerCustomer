package com.ifpe.edu.br.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.contracts.ErrorState
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.model.repository.model.DeviceCardModel
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser
import com.ifpe.edu.br.model.repository.remote.dto.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.Id
import com.ifpe.edu.br.model.repository.remote.dto.ThingsBoardUser
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AuthenticateFailureException
import com.ifpe.edu.br.model.util.TokenExpiredException
import com.ifpe.edu.br.view.manager.UIStateManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val devicesFetchInterval = 120_000L

    // StateFlow para a lista de dispositivos a serem exibidos
    private val _deviceCardsState = MutableStateFlow<List<DeviceCardModel>>(emptyList())
    val deviceCards: StateFlow<List<DeviceCardModel>> = _deviceCardsState.asStateFlow()

    // Estado para o carregamento da lista de devices
    private val _isLoadingDevices = MutableStateFlow(false)
    val isLoadingDevices: StateFlow<Boolean> = _isLoadingDevices.asStateFlow()

    fun authenticate(
        user: AuthUser,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val minDelay = 1500L
            uiStateManager.setErrorState(
                Constants.STATE_ERROR,
                ErrorState(
                    message = "Loading",
                    errorCode = CommonConstants.State.STATE_LOADING
                )
            )
            var errorState = getDefaultErrorState()
            try {
                repository.authenticate(
                    user = user,
                    onSuccessCallback = {
                        onSuccessCallback.invoke()
                        uiStateManager.setErrorState(Constants.STATE_ERROR, errorState)
                    },
                    onFailureCallback = {
                        onFailureCallback.invoke()
                    }
                )
            } catch (e: AuthenticateFailureException) {
                errorState = ErrorState("${e.message}", CommonConstants.State.STATE_AUTH_FAILURE)
            } catch (e: Exception) {
                errorState = ErrorState("${e.message}", CommonConstants.State.STATE_NETWORK_ISSUE)
            } finally {
                val timeDelayed = System.currentTimeMillis() - startTime
                val timeLeft = (minDelay - timeDelayed).coerceAtLeast(0L)
                delay(timeLeft)
                uiStateManager.setErrorState(Constants.STATE_ERROR, errorState)
            }
        }
    }

    fun getDeviceSummariesForUser(
        user: ThingsBoardUser?,
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (repository.currentUser.value != null) {

                    val user = ThingsBoardUser(
                        id = Id(repository.currentUser.value!!.id, ""),
                        authority = "",
                        customerId = Id("", ""),
                        email = "",
                        firstName = "",
                        lastName = "",
                        name = "",
                        phone = "",
                        additionalInfo = mapOf(),
                        createdTime = 0,
                        tenantId = Id("", "")
                    )


                    repository.getDeviceSummariesForUser(
                        user = user,
                        onSuccess = {
                            AirPowerLog.d(TAG, "DEU BOM AQUI HEIN")
                        },
                        onFailureCallback = {
                            AirPowerLog.d(TAG, "NAO DEU BOM AQUI HEIN")
                        }
                    )
                }

            } catch (e: Exception) {
                AirPowerLog.e(TAG, "DEU algo ruim AQUI HEIN: ${e.message}")
            }
        }
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
                    uiStateManager.setErrorState(
                        Constants.STATE_ERROR, getDefaultErrorState()
                    )
                    onSuccessCallback.invoke()
                }
            } catch (e: Exception) {
                uiStateManager.setErrorState(
                    Constants.STATE_ERROR,
                    ErrorState(
                        "[$TAG]: -> ${e.message}",
                        Constants.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED
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

    fun retrieveCurrentUser(
        onSuccessCallback: (user: AirPowerUser) -> Unit,
        onFailureCallback: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.retrieveCurrentUser { onSuccessCallback(it!!) }
            } catch (e: Exception) {
                uiStateManager.setErrorState(
                    Constants.STATE_ERROR,
                    getDefaultErrorState()
                )
                onFailureCallback(e)
            }
        }
    }

    fun resetErrorState(stateId: String) {
        uiStateManager.setErrorState(stateId, getDefaultErrorState())
    }

    fun startDataFetchers() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "startDataFetchers()")
        fetchCurrentUser()
        if (devicesJob?.isActive != true) {
            devicesJob = startDevicesFetcher()
        }

        _deviceCardsState.value = listOf(
            DeviceCardModel(
                id = "1",
                label = "Ar condicionado do 2º andar",
                status = "online",
            ),
            DeviceCardModel(
                id = "2",
                label = "Ar condicionado do 2º andar",
                status = "offline",
            ),
            DeviceCardModel(
                id = "3",
                label = "Ar condicionado do 2º andar",
                status = "online",
            ),
            DeviceCardModel(
                id = "4",
                label = "Ar condicionado do 2º andar",
                status = "online",
            ),
            DeviceCardModel(
                id = "5",
                label = "Ar condicionado do 2º andar",
                status = "offline",
            )
        )
    }

    private fun handleException(e: Exception) {
        uiStateManager.setErrorState(
            Constants.STATE_ERROR,
            ErrorState(
                "[$TAG] : -> ${e.message}",
                Constants.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED
            )
        )
    }

    private fun handleTokenExpiredException(e: TokenExpiredException) {
        uiStateManager.setErrorState(
            Constants.STATE_ERROR,
            ErrorState(
                "[$TAG] : -> ${e.message}",
                Constants.THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED
            )
        )
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            try {
                repository.retrieveCurrentUser { }
            } catch (e: TokenExpiredException) {
                handleTokenExpiredException(e)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun startCurrentUserFetcher(): Job {
        return viewModelScope.launch {
            try {
                while (isActive) {
                    repository.retrieveCurrentUser { }
                    delay(1 * 1000L)
                }
            } catch (e: TokenExpiredException) {
                handleTokenExpiredException(e)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun startDevicesFetcher(): Job {
        return viewModelScope.launch {
            try {
                while (isActive) {
                    while (!repository.isCurrentUserValid()) {
                        delay(500)
                    }
                    repository.getDevicesForCurrentUser()
                    delay(devicesFetchInterval)
                }
            } catch (e: TokenExpiredException) {
                handleTokenExpiredException(e)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun getDefaultErrorState(): ErrorState {
        return ErrorState(
            CommonConstants.State.STATE_DEFAULT_MESSAGE,
            CommonConstants.State.STATE_DEFAULT_CODE
        )
    }
}