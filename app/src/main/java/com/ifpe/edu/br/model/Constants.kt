package com.ifpe.edu.br.model

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

object Constants {
    const val STATE_ERROR = "STATE_ERROR"
    const val AUTH_STATE = "AUTH_STATE"
    const val THINGS_BOARD_ERROR_CODE_TOKEN_EXPIRED = 11
    const val THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED = 10
    const val STATE_AUTH_REQUIRED = "STATE_AUTH_REQUIRED"

    const val STATE_CONNECTION_FAILURE = "STATE_CONNECTION_FAILURE"
    const val STATE_AUTH_FAILURE = "STATE_AUTH_FAILURE"
    const val STATE_AUTH_LOADING = "STATE_AUTH_LOADING"

    const val NAVIGATION_INITIAL = "START"
    const val NAVIGATION_MAIN = "MAIN"
    const val NAVIGATION_AUTH = "AUTH"

    const val THINGSBOARD_BASE_URL_API = "https://192.168.1.17:8080"
    const val AIRPOWER_SERVER_BASE_URL_API = "https://192.168.1.17:8443"

    const val CONNECTION_ID_THINGSBOARD = 1
    const val CONNECTION_ID_AIR_POWER_SERVER = 2
    const val KEY_COD_DRAWABLE: String = "drawable"
}