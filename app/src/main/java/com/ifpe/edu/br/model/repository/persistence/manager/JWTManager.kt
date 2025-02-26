package com.ifpe.edu.br.model.repository.persistence.manager

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import com.ifpe.edu.br.model.repository.Repository
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerToken
import com.ifpe.edu.br.model.repository.remote.dto.Token
import com.ifpe.edu.br.model.util.AirPowerLog
import java.util.Base64

object JWTManager {
    private val TAG = JWTManager::class.java.simpleName
    private val repository: Repository = Repository.getInstance()

    suspend fun handleAuthentication(
        connectionId: Int,
        token: Token,
        authCallback: suspend (AirPowerToken) -> Unit
    ) {
        require(isTokenValid(token)) { "Token is not valid" }

        val persistToken = AirPowerToken(
            connectionId,
            token.token,
            token.refreshToken,
            token.scope ?: ""
        )

        val clientToken = repository.getTokenByConnectionId(connectionId)

        if (clientToken == null) {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "No token found. Creating!")
            repository.save(persistToken)
        } else {
            if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "Token found. Updating!")
            repository.update(persistToken)
        }

        authCallback(persistToken)
    }

    suspend fun handleRefreshToken(
        connectionId: Int,
        incomingToken: Token,
        authCallback: suspend (AirPowerToken) -> Unit
    ) {
        val storedToken = repository.getTokenByConnectionId(connectionId)
            ?: throw IllegalStateException("Stored Token not found")

        require(isTokenValid(getTokenFromAirPowerToken(storedToken))) { "Stored Token is not valid" }
        require(isTokenValid(incomingToken)) { "Incoming Token is not valid" }

        storedToken.apply {
            jwt = incomingToken.token
            refreshToken = incomingToken.refreshToken
            scope = incomingToken.scope ?: ""
        }

        repository.update(storedToken)
        authCallback(storedToken)
    }

    suspend fun isTokenExpiredForConnection(connectionId: Int): Boolean {
        val tokenByClient = repository.getTokenByConnectionId(connectionId) ?: return true.also {
            AirPowerLog.w(TAG, "Token not found for connectionId: $connectionId")
        }

        val jwtParts = tokenByClient.jwt.split(".")
        if (jwtParts.size != 3) return true.also {
            AirPowerLog.w(TAG, "Invalid JWT format")
        }

        return try {
            val decodedPayload = Base64.getUrlDecoder().decode(jwtParts[1]).decodeToString()
            val expIndex = decodedPayload.indexOf("\"exp\"")

            if (expIndex != -1) {
                val expValue = decodedPayload.substring(expIndex)
                    .substringAfter(":")
                    .substringBefore(",")
                    .trim()
                    .toLong()

                val now = System.currentTimeMillis() / 1000
                if (AirPowerLog.ISLOGABLE)
                    AirPowerLog.d(TAG, "exp: $expValue, system time: $now")
                expValue < now
            } else {
                AirPowerLog.e(TAG, "Expiration time not found")
                true
            }
        } catch (e: Exception) {
            AirPowerLog.e(TAG, "Error parsing JWT: ${e.message}")
            true
        }
    }

    suspend fun getJwtForConnectionId(connectionId: Int): String {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "getJWTForConnectionId(): $connectionId")
        return getTokenForConnectionId(connectionId)?.token ?: ""
    }

    suspend fun getTokenForConnectionId(connectionId: Int): Token? {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "getTokenForConnectionId(): $connectionId")
        val token = repository.getTokenByConnectionId(connectionId)

        return token?.let { getTokenFromAirPowerToken(it) }
            ?: run {
                AirPowerLog.w(TAG, "Token is null for connection: $connectionId")
                null
            }
    }

    suspend fun resetTokenForConnection(connectionId: Int) {
        repository.getTokenByConnectionId(connectionId)?.apply {
            scope = "empty"
            jwt = "empty"
            refreshToken = "empty"
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.d(TAG, "Resetting token for connectionId: $connectionId")
            repository.update(this)
        } ?: AirPowerLog.w(TAG, "Token not found for connectionId: $connectionId")
    }

    private fun getTokenFromAirPowerToken(airPowerToken: AirPowerToken): Token {
        return try {
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.d(TAG, "getTokenFromAirPowerToken: $airPowerToken")
            Token(
                airPowerToken.jwt,
                airPowerToken.refreshToken,
                airPowerToken.scope
            )
        } catch (e: Exception) {
            AirPowerLog.e(TAG, "Error while building Token object: ${e.message}")
            Token("", "", "")
        }
    }

    fun isTokenValid(token: Token?): Boolean {
        return when {
            token == null -> {
                if (AirPowerLog.ISLOGABLE)
                    AirPowerLog.d(TAG, "Token is null")
                false
            }

            token.token.isNullOrBlank() || token.token.length < 50 -> {
                if (AirPowerLog.ISLOGABLE)
                    AirPowerLog.d(TAG, "JWT is NOT valid")
                false
            }

            token.refreshToken.isNullOrBlank() || token.refreshToken.length < 50 -> {
                if (AirPowerLog.ISLOGABLE)
                    AirPowerLog.d(TAG, "Refresh token is NOT valid")
                false
            }

            else -> {
                if (AirPowerLog.ISLOGABLE)
                    AirPowerLog.d(TAG, "Token is valid")
                true
            }
        }
    }
}