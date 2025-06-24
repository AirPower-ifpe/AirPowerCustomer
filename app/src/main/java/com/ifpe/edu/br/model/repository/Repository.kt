package com.ifpe.edu.br.model.repository
/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import android.content.Context
import android.content.res.Resources.NotFoundException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ifpe.edu.br.core.api.ConnectionManager
import com.ifpe.edu.br.model.repository.model.TelemetryDataWrapper
import com.ifpe.edu.br.model.repository.persistence.AirPowerDatabase
import com.ifpe.edu.br.model.repository.persistence.manager.JWTManager
import com.ifpe.edu.br.model.repository.persistence.manager.SharedPrefManager
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerToken
import com.ifpe.edu.br.model.repository.persistence.model.AirPowerUser
import com.ifpe.edu.br.model.repository.persistence.model.toThingsBoardUser
import com.ifpe.edu.br.model.repository.remote.api.AirPowerServerConnectionContractImpl
import com.ifpe.edu.br.model.repository.remote.api.AirPowerServerManager
import com.ifpe.edu.br.model.repository.remote.dto.AlarmInfo
import com.ifpe.edu.br.model.repository.remote.dto.AllDevicesMetricsWrapper
import com.ifpe.edu.br.model.repository.remote.dto.DeviceConsumption
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.model.repository.remote.dto.TelemetryAggregationResponse
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.repository.remote.dto.auth.Token
import com.ifpe.edu.br.model.repository.remote.dto.error.ErrorCode
import com.ifpe.edu.br.model.repository.remote.dto.user.AirPowerBoardUser
import com.ifpe.edu.br.model.repository.remote.query.AggregatedTelemetryQuery
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID

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

    private val _alarmInfo = MutableStateFlow<List<AlarmInfo>>(emptyList())
    private val alarmInfo: StateFlow<List<AlarmInfo>> = _alarmInfo.asStateFlow()

    private val _chartDataWrapper = MutableStateFlow(getEmptyTelemetryDataWrapper())
    private val chartDataWrapper: StateFlow<TelemetryDataWrapper> = _chartDataWrapper.asStateFlow()

    private val _allDevicesMetricsWrapper = MutableStateFlow(getEmptyAllDevicesMetricsWrapper())
    private val allDevicesMetricsWrapper: StateFlow<AllDevicesMetricsWrapper> = _allDevicesMetricsWrapper.asStateFlow()




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

    fun getAlarmInfo(): StateFlow<List<AlarmInfo>> {
        // TODO change this
        _alarmInfo.value = listOf(
            AlarmInfo(
                UUID.randomUUID(),
                "Crítico",
                "",
                987342L,
                10
            ),
            AlarmInfo(
                UUID.randomUUID(),
                "Meus Alarmes",
                "",
                987342L,
                3
            ),

            AlarmInfo(
                UUID.randomUUID(),
                "Grupo",
                "",
                987342L,
                1
            ),
            AlarmInfo(
                UUID.randomUUID(),
                "Grupo",
                "",
                987342L,
                1
            )
        )
        return alarmInfo
    }

    fun getChartDataWrapper(id: UUID): StateFlow<TelemetryDataWrapper> {
        // TODO change this
        _chartDataWrapper.value = TelemetryDataWrapper(
            "KW/h",
            listOf(
                DeviceConsumption("1", 60.0),
                DeviceConsumption("2", 5.0),
                DeviceConsumption("3", 70.0),
                DeviceConsumption("4", 90.0),
                DeviceConsumption("5", 100.0),
                DeviceConsumption("6", 160.0),
                DeviceConsumption("7", 140.0),
                DeviceConsumption("8", 90.0),
                DeviceConsumption("9", 99.0),
                DeviceConsumption("10", 350.0),
                DeviceConsumption("11", 20.0),
                DeviceConsumption("12", 10.0),
            )
        )
        return chartDataWrapper
    }

    fun getAllDevicesChartDataWrapper(): StateFlow<TelemetryDataWrapper> {
        // TODO change this
        _chartDataWrapper.value = TelemetryDataWrapper(
            "KW/h",
            listOf(
                DeviceConsumption("1", 54.0),
                DeviceConsumption("2", 65.0),
                DeviceConsumption("3", 70.0),
                DeviceConsumption("4", 90.0),
                DeviceConsumption("5", 100.0),
                DeviceConsumption("6", 160.0),
                DeviceConsumption("7", 140.0),
                DeviceConsumption("8", 90.0),
                DeviceConsumption("9", 99.0),
                DeviceConsumption("10", 180.0),
                DeviceConsumption("11", 20.0),
                DeviceConsumption("12", 10.0),
            )
        )
        return chartDataWrapper
    }

    fun getAllDevicesMetricsWrapper(): StateFlow<AllDevicesMetricsWrapper> {
        // TODO change this
        _allDevicesMetricsWrapper.value = AllDevicesMetricsWrapper(
            totalConsumption = "150000KW/h",
            devicesCount = 350,
            label = "consumo",
            deviceConsumptionSet = listOf(
                DeviceConsumption("1", 54.0),
                DeviceConsumption("2", 65.0),
                DeviceConsumption("3", 70.0),
                DeviceConsumption("4", 90.0),
                DeviceConsumption("5", 100.0),
                DeviceConsumption("6", 160.0),
                DeviceConsumption("7", 140.0),
                DeviceConsumption("8", 90.0),
                DeviceConsumption("9", 99.0),
                DeviceConsumption("10", 180.0),
                DeviceConsumption("11", 20.0),
                DeviceConsumption("12", 10.0),
            )
        )
        return allDevicesMetricsWrapper
    }

    private fun getEmptyTelemetryDataWrapper(): TelemetryDataWrapper {
        return TelemetryDataWrapper("", emptyList())
    }

    private fun getEmptyAllDevicesMetricsWrapper(): AllDevicesMetricsWrapper {
        return AllDevicesMetricsWrapper(
            totalConsumption = "",
            devicesCount = 0,
            label = "",
            deviceConsumptionSet = emptyList()
        )
    }
}