package com.ifpe.edu.br.model.repository
/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifpe.edu.br.core.api.ConnectionManager
import com.ifpe.edu.br.model.repository.persistence.AirPowerDatabase
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.repository.persistence.manager.SharedPrefManager
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerToken
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser
import com.ifpe.edu.br.model.repository.persistence.model.toThingsBoardUser
import com.ifpe.edu.br.model.repository.remote.api.AirPowerServerConnectionContractImpl
import com.ifpe.edu.br.model.repository.remote.api.AirPowerServerManager
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.TelemetryAggregationResponse
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.auth.Token
import com.ifpe.edu.br.model.repository.remote.dto.user.AirPowerBoardUser
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository private constructor(context: Context) {
    private val db = AirPowerDatabase.getDataBaseInstance(context)
    private val tokenDao = db.getTokenDaoInstance()
    private val userDao = db.getUserDaoInstance()
    private val spManager = SharedPrefManager.getInstance(context)
    private val airPowerServerConnection =
        ConnectionManager.getInstance().getConnectionById(AirPowerServerConnectionContractImpl)
    private val airPowerServerMgr = AirPowerServerManager(airPowerServerConnection)

    private val _devicesSummary = MutableLiveData<List<DeviceSummary>>(emptyList())
    val devicesSummary: LiveData<List<DeviceSummary>> get() = _devicesSummary

//    private val _deviceCardsState = MutableStateFlow<List<DeviceCardModel>>(emptyList())
//    val deviceCards: StateFlow<List<DeviceCardModel>> = _deviceCardsState.asStateFlow()

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun build(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        if (AirPowerLog.ISLOGABLE)
                            AirPowerLog.d(TAG, "build()")
                        instance = Repository(context)
                    }
                }
            }
        }

        fun getInstance(): Repository {
            return instance
                ?: throw IllegalStateException("AirPowerRepository not initialized. Call build() first.")
        }

        private val TAG = Repository::class.simpleName
    }

    suspend fun authenticate(
        user: AuthUser,
    ): ResultWrapper<Token> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "authenticate()")
        return airPowerServerMgr.authenticate(user)
    }

    suspend fun getAggregatedTelemetry(
        query: AggregatedTelemetryQuery
    ): ResultWrapper<TelemetryAggregationResponse> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "getAggregatedTelemetry()")
        return airPowerServerMgr.getAggregatedTelemetry(query)
    }

    suspend fun retrieveDeviceSummaryForCurrentUser() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "retrieveDeviceSummaryForCurrentUser()")
        try {
            _devicesSummary.value =
                getCurrentUser().let {
                    AirPowerLog.d(TAG, "user is valid")
                    airPowerServerMgr.getDeviceSummariesForUser(it.toThingsBoardUser())
                }
            AirPowerLog.e(TAG, devicesSummary.value.toString())
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateSession(): ResultWrapper<Token> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "updateSession()")
        return airPowerServerMgr.refreshToken()
    }

    suspend fun retrieveCurrentUser(): ResultWrapper<AirPowerBoardUser> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "retrieveCurrentUser()")
        val currentUserResult = airPowerServerMgr.getCurrentUser()
        if (currentUserResult is ResultWrapper.Success) {
            val storedUserSet = userDao.findAll()
            var storedUser: AirPowerUser? = null
            if (storedUserSet.isNotEmpty()) {
                storedUser = storedUserSet[0]
            }
            val incomingUser = currentUserResult.value.toAirPowerUser()
            if (storedUser == null) {
                userDao.insert(incomingUser)
            } else {
                if (storedUser.id == incomingUser.id) {
                    userDao.update(incomingUser)
                } else {
                    userDao.deleteAll()
                    userDao.insert(incomingUser)
                }
            }
        }
        return currentUserResult
    }

    suspend fun isSessionExpired(): Boolean {
        val connectionId = AirPowerServerConnectionContractImpl.getConnectionId()
        val isExpired = connectionId.let { JWTManager.isTokenExpiredForConnection(it) }
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "isSessionExpired(): $isExpired")
        return isExpired
    }

    suspend fun logout() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "logout()")
        val connectionId = AirPowerServerConnectionContractImpl.getConnectionId()
        JWTManager.resetTokenForConnection(connectionId)
        userDao.deleteAll()
    }

    suspend fun getTokenByConnectionId(connection: Int): AirPowerToken? {
        return withContext(Dispatchers.IO) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "getTokenByConnectionId: $connection")
            tokenDao.getTokenByClient(connection)
        }
    }

    private suspend fun save(user: AirPowerBoardUser) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "save: $user")
        require(isUserValid(user)) { "ThingsBoardUser is invalid" }

        withContext(Dispatchers.IO) {
            try {
                val airPowerUser = user.toAirPowerUser()
                userDao.insert(airPowerUser)
            } catch (e: Exception) {
                throw IllegalStateException("Error persisting user in DB: ${e.message}")
            }
        }
    }

    private suspend fun delete(user: AirPowerUser) {
        withContext(Dispatchers.IO) {
            try {
                val persistUser = userDao.getUserById(user.id)
                persistUser?.let { userDao.delete(it) }
            } catch (e: Exception) {
                throw IllegalStateException("Error deleting user in DB: ${e.message}")
            }
        }
    }

    suspend fun getCurrentAirPowerUser(): AirPowerUser? {
        return withContext(Dispatchers.IO) {
            try {
                userDao.findAll().firstOrNull()
            } catch (e: Exception) {
                throw IllegalStateException("Error getting current user from DB: ${e.message}")
            }
        }
    }

    suspend fun save(token: AirPowerToken) {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "save() token: $token")
        require(!token.jwt.isNullOrEmpty() && !token.refreshToken.isNullOrEmpty() && token.client != null) {
            "Token info is null or empty!"
        }

        withContext(Dispatchers.IO) {
            val existingToken = tokenDao.getTokenByClient(token.client)
            if (existingToken != null) {
                AirPowerLog.e(TAG, "save(): ERROR: Token exists for client! $existingToken")
                return@withContext
            }
            tokenDao.insert(token)
        }
    }

    suspend fun update(token: AirPowerToken) {
        if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "update() token: $token")
        require(!token.jwt.isNullOrEmpty() && !token.refreshToken.isNullOrEmpty() && token.client != null) {
            "Token info is null or empty!"
        }

        withContext(Dispatchers.IO) {
            val existingToken = tokenDao.getTokenByClient(token.client)
            existingToken?.let {
                it.jwt = token.jwt
                it.refreshToken = token.refreshToken
                it.scope = token.scope
                tokenDao.update(it)
            } ?: AirPowerLog.e(TAG, "update(): Can't update token. Token does not exist")
        }
    }

    fun writeString(key: String, value: String) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "writeString key:$key value:$value")
        spManager.writeString(key, value)
    }

    fun readString(key: String): String {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "readString key:$key")
        return spManager.readString(key)
    }

    private fun isUserValid(user: AirPowerBoardUser): Boolean {
        return when {
            user.authority.isEmpty() -> {
                AirPowerLog.d(TAG, "User authority is null or empty")
                false
            }

            user.customerId.id.isNullOrEmpty() -> {
                AirPowerLog.d(TAG, "Customer ID is null or empty")
                false
            }

            else -> true
        }
    }

    private fun AirPowerBoardUser.toAirPowerUser(): AirPowerUser {
        return AirPowerUser(
            id = id.id,
            authority = authority,
            email = email,
            customerId = customerId.id,
            firstName = firstName,
            lastName = lastName,
            name = name,
            phone = phone
        )
    }

    private fun getCurrentUser(): AirPowerUser {
        return userDao.findAll()[0]
    }

    fun isUserLoggedIn(): Boolean {
        return userDao.findAll().size == 1
    }
}