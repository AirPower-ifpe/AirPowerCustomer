package com.ifpe.edu.br.model.repository
/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import android.content.Context
import android.content.res.Resources.NotFoundException
import com.ifpe.edu.br.core.api.ConnectionManager
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.persistence.AirPowerDatabase
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.repository.persistence.manager.SharedPrefManager
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerToken
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser
import com.ifpe.edu.br.model.repository.persistence.model.toThingsBoardUser
import com.ifpe.edu.br.model.repository.remote.api.AirPowerServerConnectionContractImpl
import com.ifpe.edu.br.model.repository.remote.api.AirPowerServerManager
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.AllMetricsWrapper
import com.ifpe.edu.br.model.repository.remote.dto.DeviceConsumption
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.DevicesStatusSummary
import com.ifpe.edu.br.model.repository.remote.dto.AirPowerNotificationItem
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggDataWrapperResponse
import com.ifpe.edu.br.model.repository.remote.dto.agg.AggregationRequest
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.auth.Token
import com.ifpe.edu.br.model.repository.remote.dto.error.ErrorCode
import com.ifpe.edu.br.model.repository.remote.dto.user.ThingsBoardUser
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class Repository private constructor(context: Context) {
    private val db = AirPowerDatabase.getDataBaseInstance(context)
    private val tokenDao = db.getTokenDaoInstance()
    private val userDao = db.getUserDaoInstance()
    private val spManager = SharedPrefManager.getInstance(context)
    private val airPowerServerConnection =
        ConnectionManager.getInstance().getConnectionById(AirPowerServerConnectionContractImpl)
    private val airPowerServerMgr = AirPowerServerManager(airPowerServerConnection)

    private val _devicesSummary = MutableStateFlow<List<DeviceSummary>>(emptyList())
    val devicesSummary: StateFlow<List<DeviceSummary>> get() = _devicesSummary

    private val _alarmInfo = MutableStateFlow<List<AlarmInfo>>(emptyList())
    val alarmInfo: StateFlow<List<AlarmInfo>> = _alarmInfo.asStateFlow()

    private val _allDevicesMetricsWrapper = MutableStateFlow(getEmptyAllDevicesMetricsWrapper())
    val allDevicesMetricsWrapper: StateFlow<AllMetricsWrapper> =
        _allDevicesMetricsWrapper.asStateFlow()

    private val _dashBoardsMetricsWrapper =
        MutableStateFlow(listOf(getEmptyAllDevicesMetricsWrapper()))
    val dashBoardsMetricsWrapper: StateFlow<List<AllMetricsWrapper>> =
        _dashBoardsMetricsWrapper.asStateFlow()

    private val _notification = MutableStateFlow(getEmptyNotification())
    private val notification: StateFlow<List<AirPowerNotificationItem>> = _notification.asStateFlow()

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

    suspend fun retrieveDeviceSummaryForCurrentUser(): ResultWrapper<List<DeviceSummary>> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "retrieveDeviceSummaryForCurrentUser()")
        val user = getCurrentUser()
        if (user != null) {
            if (AirPowerLog.ISVERBOSE)
                AirPowerLog.d(TAG, "Current user is valid")
            val devicesSummaryResponseWrapper =
                airPowerServerMgr.getDeviceSummariesForUser(user.toThingsBoardUser())
            if (devicesSummaryResponseWrapper is ResultWrapper.Success) {
                _devicesSummary.value = devicesSummaryResponseWrapper.value
            }
            return devicesSummaryResponseWrapper
        } else {
            return ResultWrapper.ApiError(ErrorCode.AP_REFRESH_TOKEN_EXPIRED)
        }
    }

    suspend fun retrieveAlarmInfo(): ResultWrapper<List<AlarmInfo>> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "retrieveDeviceSummaryForCurrentUser()")
        val resultWrapper = airPowerServerMgr.getAlarmsForCurrentUser()
        if (resultWrapper is ResultWrapper.Success) {
            if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "updating alarms data")
            _alarmInfo.value = resultWrapper.value
        }
        return resultWrapper
    }

    suspend fun updateSession(): ResultWrapper<Token> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "updateSession()")
        return airPowerServerMgr.refreshToken()
    }

    suspend fun retrieveCurrentUser(): ResultWrapper<ThingsBoardUser> {
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
                if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "save user:${incomingUser}")
            } else {
                if (storedUser.id == incomingUser.id) {
                    userDao.update(incomingUser)
                } else {
                    userDao.deleteAll()
                    userDao.insert(incomingUser)
                    if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "save user:${incomingUser}")
                }
            }
        }
        return currentUserResult
    }

    suspend fun retrieveAllDeviceAggregatedDataWrapper(
        request: AggregationRequest
    ): ResultWrapper<AggDataWrapperResponse> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "retrieveAllDeviceAggregatedDataWrapper()")
        return airPowerServerMgr.getDeviceAggregatedDataWrapper(request)
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

    private suspend fun save(user: ThingsBoardUser) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "save: $user")
        require(isUserValid(user)) { "ThingsBoardUser is invalid" }

        withContext(Dispatchers.IO) {
            try {
                val airPowerUser = user.toAirPowerUser()
                userDao.insert(airPowerUser)
                if (AirPowerLog.ISVERBOSE) AirPowerLog.d(TAG, "save user:${airPowerUser}")
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

    private fun isUserValid(user: ThingsBoardUser): Boolean {
        return when {
            user.authority.isEmpty() -> {
                AirPowerLog.d(TAG, "User authority is null or empty")
                false
            }

            user.customerId.id == null -> {
                AirPowerLog.d(TAG, "Customer ID is null or empty")
                false
            }

            else -> true
        }
    }

    private fun ThingsBoardUser.toAirPowerUser(): AirPowerUser {
        return AirPowerUser(
            id = id.id.toString(),
            authority = authority,
            email = email,
            customerId = customerId.id.toString(),
            tenantId = tenantId.id.toString(),
            firstName = firstName,
            lastName = lastName,
            name = name,
            phone = phone
        )
    }


    private fun getCurrentUser(): AirPowerUser? {
        return userDao.findAll()[0]
    }

    fun isUserLoggedIn(): Boolean {
        return userDao.findAll().size == 1
    }

    fun getDeviceById(id: String): DeviceSummary {
        devicesSummary.value?.forEach { devicesSummary ->
            if (AirPowerLog.ISLOGABLE)
                AirPowerLog.e(TAG, "[$TAG]: devicesSummary:$devicesSummary   id: $id")
            if (devicesSummary.id.toString() == id) {
                return devicesSummary
            }
        }
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.e(TAG, "[$TAG]: Exception: -> device not found")
        throw NotFoundException("[$TAG]: Exception: -> device not found")
    }

    suspend fun fetchAllDashboardsMetricsWrapper(): ResultWrapper<List<AllMetricsWrapper>> {
        if (AirPowerLog.ISLOGABLE)
            AirPowerLog.d(TAG, "fetchAllDashboardsMetricsWrapper()")
        val resultWrapper =
            airPowerServerMgr.getDevicesMetricsWrapper(Constants.MetricsGroup.DASHBOARDS)
        if (resultWrapper is ResultWrapper.Success) {
            _dashBoardsMetricsWrapper.value = resultWrapper.value
        }
        return resultWrapper
    }

    private fun getEmptyAllDevicesMetricsWrapper(): AllMetricsWrapper {
        return AllMetricsWrapper(
            totalConsumption = "",
            devicesCount = 0,
            label = "",
            deviceConsumptionSet = listOf(
                DeviceConsumption("", 0.0)
            ),
            statusSummaries = listOf(
                DevicesStatusSummary("", 0)
            )
        )
    }

    fun getNotifications(): StateFlow<List<AirPowerNotificationItem>> {
        return notification
    }

    private fun getEmptyNotification(): List<AirPowerNotificationItem> {
        return emptyList()
    }

    suspend fun retrieveNotifications(): ResultWrapper<List<AirPowerNotificationItem>> {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "retrieveNotifications()")
        val resultWrapper = airPowerServerMgr.getNotificationsForCurrentUser()
        if (resultWrapper is ResultWrapper.Success) {
            if (AirPowerLog.ISVERBOSE)
                AirPowerLog.d(TAG, "Updating notifications data with ${resultWrapper.value.size} items.")
            _notification.value = resultWrapper.value
        }
        return resultWrapper
    }
}
