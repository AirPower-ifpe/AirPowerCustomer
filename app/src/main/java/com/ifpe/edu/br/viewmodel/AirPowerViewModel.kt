package com.ifpe.edu.br.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.contracts.ErrorState
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser
import com.ifpe.edu.br.model.repository.remote.dto.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.Device
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AuthenticateFailureException
import com.ifpe.edu.br.model.util.TokenExpiredException
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

//    fun startFetchingDevices(
//        onFailureCallback: () -> Unit
//    ) {
//        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "startFetchingDevices()")
//        viewModelScope.launch {
//            while (true) {
//                try {
//                    repository.getDevicesForCurrentUser()
//                } catch (e: TokenExpiredException) {
//                    AirPowerLog.e(TAG, "o erro aconteceu aqui ohhh: ${e.message}")
//                    updateSession(
//                        onSuccessCallback = {},
//                        onFailureCallback = { onFailureCallback.invoke() })
//                    uiStateManager.setErrorState(
//                        Constants.STATE_ERROR,
//                        ErrorState(
//                            "coisa coisa",
//                            Constants.THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED
//                        )
//                    )
//                } catch (e: Exception) {
//                    AirPowerLog.e(TAG, "Error fetching devices: ${e.message}")
//                    uiStateManager.setErrorState(
//                        Constants.STATE_ERROR,
//                        ErrorState(
//                            "coisa coisa",
//                            Constants.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED
//                        )
//                    )
//                    //onFailureCallback.invoke()
//                }
//                delay(fetchInterval)
//            }
//        }
//    }

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
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                val isExpired = repository.isSessionExpired()
//                if (!isExpired) {
//                    uiStateManager.setErrorState(
//                        Constants.STATE_ERROR, getDefaultErrorState()
//                    )
//                }
                callback(isExpired)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
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

//    fun fetchData() {
//        viewModelScope.launch {
//            if (AirPowerLog.ISVERBOSE) {
//                AirPowerLog.e(TAG, "fetchData()")
//            }
//            while (true) {
//                try {
//                    if (currentUser.value == null) {
//                        repository.retrieveCurrentUser { }
//                    }
//                    repository.getDevicesForCurrentUser()
//                    uiStateManager.setErrorState(Constants.STATE_ERROR, getDefaultErrorState())
//                } catch (e: TokenExpiredException) {
//                    uiStateManager.setErrorState(
//                        Constants.STATE_ERROR,
//                        ErrorState(
//                            "[$TAG] : -> ${e.message}",
//                            Constants.THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED
//                        )
//                    )
//                } catch (e: Exception) {
//                    uiStateManager.setErrorState(
//                        Constants.STATE_ERROR,
//                        ErrorState(
//                            "${e.message}",
//                            Constants.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED
//                        )
//                    )
//                }
//                delay(fetchInterval)
//            }
//        }
//    }

    fun startDataFetchers() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "startDataFetchers()")
        fetchCurrentUserIfNeeded()
        startDevicesFetcher()
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

    private fun fetchCurrentUserIfNeeded() {
        viewModelScope.launch {
            try {
                if (!repository.isCurrentUserValid()) {
                    repository.retrieveCurrentUser { }
                }
            } catch (e: TokenExpiredException) {
                handleTokenExpiredException(e)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun startDevicesFetcher() {
        viewModelScope.launch {
            try {
                while (true) {
                    if (repository.isCurrentUserValid()) {
                        repository.getDevicesForCurrentUser()
                    }
                    delay(5 * 1000L)
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