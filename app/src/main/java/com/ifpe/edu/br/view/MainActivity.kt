package com.ifpe.edu.br.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.components.FailureDialog
import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.ui.screens.ExpiredSessionWarningScreen
import com.ifpe.edu.br.view.ui.screens.MainScreen
import com.ifpe.edu.br.view.ui.theme.AirPowerCostumerTheme
import com.ifpe.edu.br.view.ui.theme.DefaultTransparentGradient
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModelProvider

class MainActivity : ComponentActivity() {
    val TAG = MainActivity::class.simpleName
    private val viewModel = AirPowerViewModelProvider.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val updateTokenkey = Constants.UIStateKey.REFRESH_TOKEN_KEY
            val updateTokenState = viewModel.uiStateManager.observeUIState(updateTokenkey)
                .collectAsState(initial = UIState(Constants.UIState.EMPTY_STATE))
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
                if (updateTokenState.value.state == Constants.UIState.STATE_REQUEST_LOGIN) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.9f))
                    ) {
                        FailureDialog(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize(),
                            drawableResId = R.drawable.auth_issue,
                            iconSize = 150.dp,
                            text = "A sessão expirou, faça login novamente",
                            textColor = tb_primary_light,
                            retryCallback = {
                                viewModel.logout()
                                viewModel.resetUIState(stateKey)
                                navigateAuthScreen(navController, this@MainActivity)
                            }
                        ) { DefaultTransparentGradient() }
                    }
                } else {
                    MainScreen(
                        navController = navController,
                        mainViewModel = viewModel,
                        componentActivity = this
                    )
                }
            }
        }
    }

    override fun onResume() {
        viewModel.startDataFetchers()
        super.onResume()
    }

    override fun onStop() {
        viewModel.stopAllFetchers()
        super.onStop()
    }
}

private fun navigateAuthScreen(
    navController: NavController,
    componentActivity: ComponentActivity
) {
    navController.popBackStack()
    AirPowerUtil.launchActivity(
        navController.context,
        AuthActivity::class.java
    )
    componentActivity.finish()
}