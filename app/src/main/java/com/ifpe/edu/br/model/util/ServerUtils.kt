package com.ifpe.edu.br.model.util

import com.google.gson.Gson
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.remote.dto.error.AirPowerErrorResponse
import retrofit2.Response
import javax.net.ssl.HttpsURLConnection

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
object ServerUtils {
    val TAG = ServerUtils::class.simpleName

    fun getServerErrorWrapper(serverResponse: Response<out Any>): AirPowerErrorResponse {
        return try {
            val errorBody = serverResponse.errorBody()?.string()
            errorBody ?: AirPowerErrorResponse(
                HttpsURLConnection.HTTP_INTERNAL_ERROR,
                Constants.ResponseErrorCodes.INVALID_RESPONSE,
                "Invalid server response error",
                System.currentTimeMillis()
            )
            Gson().fromJson(errorBody, AirPowerErrorResponse::class.java)
        } catch (e: Exception) {
            AirPowerLog.e(TAG, "Unexpected error while handling server response: ${e.message}")
            AirPowerErrorResponse(
                HttpsURLConnection.HTTP_INTERNAL_ERROR,
                Constants.ResponseErrorCodes.INVALID_RESPONSE,
                "Unexpected error while handling server response",
                System.currentTimeMillis()
            )
        }
    }
}