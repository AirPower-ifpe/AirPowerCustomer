package com.ifpe.edu.br.model.repository.remote.api

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import com.google.gson.Gson
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.remote.dto.error.AirPowerErrorResponse
import com.ifpe.edu.br.model.repository.remote.dto.error.ErrorCode
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResultWrapper<T> {
    val TAG = "safeApiCall"
    return withContext(Dispatchers.IO) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> {
                    if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "IOException")
                    ResultWrapper.NetworkError
                }

                is HttpException -> {
                    if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "HttpException")
                    val errorBody = throwable.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        try {
                            val apiError =
                                Gson().fromJson(errorBody, AirPowerErrorResponse::class.java)
                            val errorCode = mapErrorCode(apiError.errorCode)
                            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "Error code: $errorCode")
                            ResultWrapper.ApiError(errorCode)
                        } catch (e: Exception) {
                            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(
                                TAG,
                                "Error while parsing server error object: ${e.message}"
                            )
                            ResultWrapper.ApiError(ErrorCode.UNKNOWN_INTERNAL_ERROR)
                        }
                    } else {
                        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "Not mapped error")
                        ResultWrapper.ApiError(ErrorCode.UNKNOWN_INTERNAL_ERROR)
                    }
                }

                else -> {
                    ResultWrapper.ApiError(ErrorCode.UNKNOWN_INTERNAL_ERROR)
                }
            }
        }
    }
}

private fun mapErrorCode(errorID: Int): ErrorCode {
    if (AirPowerLog.ISVERBOSE) AirPowerLog.d("mapErrorCode", "Error id from server: $errorID")

    return when (errorID) {
        Constants.ResponseErrorCode.TB_INVALID_CREDENTIALS -> ErrorCode.TB_INVALID_CREDENTIALS
        Constants.ResponseErrorCode.TB_REFRESH_TOKEN_EXPIRED -> ErrorCode.TB_REFRESH_TOKEN_EXPIRED
        Constants.ResponseErrorCode.TB_GENERIC_ERROR -> ErrorCode.TB_GENERIC_ERROR

        Constants.ResponseErrorCode.AP_JWT_EXPIRED -> ErrorCode.AP_JWT_EXPIRED
        Constants.ResponseErrorCode.AP_REFRESH_TOKEN_EXPIRED -> ErrorCode.AP_REFRESH_TOKEN_EXPIRED
        Constants.ResponseErrorCode.AP_GENERIC_ERROR -> ErrorCode.AP_GENERIC_ERROR

        else -> ErrorCode.UNKNOWN_INTERNAL_ERROR
    }
}