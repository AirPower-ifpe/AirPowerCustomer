package com.ifpe.edu.br.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.view.ui.screens.ExpiredSessionWarningScreen
import com.ifpe.edu.br.view.ui.screens.MainScreen
import com.ifpe.edu.br.view.ui.theme.AirPowerCostumerTheme
import com.ifpe.edu.br.viewmodel.AirPowerViewModelProvider

class MainActivity : ComponentActivity() {
    val TAG = MainActivity::class.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel = AirPowerViewModelProvider.getInstance()
            val stateKey = Constants.UIStateKey.SESSION
            val sessionState = viewModel.uiStateManager.observeUIState(stateKey)
                .collectAsState(initial = UIState(Constants.UIState.EMPTY_STATE))
            val updateSessionStateKey = Constants.UIStateKey.REFRESH_TOKEN_KEY
            val updateSessionUIState =
                viewModel.uiStateManager.observeUIState(updateSessionStateKey)
                    .collectAsState(initial = UIState(Constants.UIState.EMPTY_STATE))

            if (updateSessionUIState.value.state == Constants.UIState.STATE_SUCCESS) {
                viewModel.resetUIState(updateSessionStateKey)
            } else {
                if (updateSessionUIState.value.state != Constants.UIState.EMPTY_STATE) {
                    ExpiredSessionWarningScreen(viewModel, updateSessionStateKey, navController)
                }
            }

            AirPowerCostumerTheme {
                if (sessionState.value.state == Constants.UIState.STATE_UPDATE_SESSION) {
                    viewModel.resetUIState(stateKey)
                    viewModel.updateSession()
                }
                MainScreen(
                    navController = navController,
                    mainViewModel = viewModel,
                    componentActivity = this
                )
            }
        }
    }
}