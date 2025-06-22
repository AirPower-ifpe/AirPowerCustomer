package com.ifpe.edu.br.common

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

class CommonConstants {

    object Ui {
        val ALIGNMENT_TOP = 1
        val ALIGNMENT_CENTER = 2
    }

    object State {
        @Deprecated("this will be removed")
        const val STATE_DEFAULT_MESSAGE = ""
        @Deprecated("this will be removed")
        const val STATE_DEFAULT_STATE_CODE = 0
        @Deprecated("this will be removed")
        const val STATE_SUCCESS = 1
        @Deprecated("this will be removed")
        const val STATE_AUTH_LOADING = 2
        @Deprecated("this will be removed")
        const val STATE_AUTH_FAILURE = 3
        @Deprecated("this will be removed")
        const val STATE_TOKEN_EXPIRED = 4
        @Deprecated("this will be removed")
        const val STATE_SERVER_INTERNAL_ISSUE = 5
        @Deprecated("this will be removed")
        const val STATE_LOADING = 6
    }
}