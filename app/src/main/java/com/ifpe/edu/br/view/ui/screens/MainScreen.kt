package com.ifpe.edu.br.view.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.BottomNavItem
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomNavigationBar
import com.ifpe.edu.br.common.components.FailureDialog
import com.ifpe.edu.br.common.components.GradientBackground
import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientDark
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientLight
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.AuthActivity
import com.ifpe.edu.br.view.ui.theme.DefaultTransparentGradient
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.view.ui.theme.tb_tertiary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel,
    componentActivity: ComponentActivity
) {
    val TAG = "MainScreen"

    val uiState by mainViewModel.uiStateManager.observeUIState(id = Constants.UIState.AUTH_STATE)
        .collectAsState()

    LaunchedEffect(Unit) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "LaunchedEffect()")
        mainViewModel.startDataFetchers()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tb_tertiary_light)
    ) {
        CustomColumn(
            alignmentStrategy = CommonConstants.Ui.ALIGNMENT_TOP,
            layouts = listOf {
                Scaffold(
                    bottomBar = {
                        CustomNavigationBar(
                            navController = navController,
                            listOf(
                                BottomNavItem.Home,
                                BottomNavItem.Devices,
                                BottomNavItem.Profile
                            )
                        )
                    }
                ) { innerPadding ->
                    GradientBackground(
                        if (isSystemInDarkTheme()) defaultBackgroundGradientDark
                        else defaultBackgroundGradientLight
                    )
                    NavHostContainer(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        mainViewModel = mainViewModel
                    )
                }
            }
        )
    }

    when (uiState.stateCode) {
        CommonConstants.State.STATE_AUTH_FAILURE -> {
            AuthFailure(navController, componentActivity)
        }

        CommonConstants.State.STATE_SERVER_INTERNAL_ISSUE -> {
            NetworkIssue(navController, componentActivity)
        }

        Constants.DeprecatedValues.THINGS_BOARD_ERROR_CODE_AUTHENTICATION_FAILED -> {
            UpdateSessionFailure(navController, componentActivity)
        }

        Constants.ResponseErrorId.AP_GENERIC_ERROR -> {
            mainViewModel.updateSession(
                onSuccessCallback = {
                    mainViewModel.startDataFetchers()
                },
                onFailureCallback = {}
            )
        }
    }
}

@Composable
fun UpdateSessionFailure(
    navController: NavHostController,
    componentActivity: ComponentActivity
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        FailureDialog(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            drawableResId = R.drawable.auth_issue,
            iconSize = 150.dp,
            text = "Sua sessão expirou, por favor faça login novamente",
            textColor = tb_primary_light,
            retryCallback = {
                navigateAuthScreen(navController, componentActivity)
            }
        ) { modifier -> DefaultTransparentGradient(modifier) }
    }
}

@Composable
private fun NetworkIssue(
    navController: NavHostController,
    componentActivity: ComponentActivity
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        FailureDialog(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            drawableResId = R.drawable.network_issue,
            iconSize = 150.dp,
            text = "Houve um erro de conexão",
            textColor = tb_primary_light,
            retryCallback = {
                navigateAuthScreen(navController, componentActivity)
            }
        ) { modifier -> DefaultTransparentGradient(modifier) }
    }
}


@Composable
fun AuthFailure(
    navController: NavHostController,
    componentActivity: ComponentActivity
) {
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
            text = "Credenciais inválidas",
            textColor = tb_primary_light,
            retryCallback = {
                navigateAuthScreen(navController, componentActivity)
            }
        ) { DefaultTransparentGradient() }
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    mainViewModel: AirPowerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                HomeScreen(navController, mainViewModel)
            }
        }
        composable(BottomNavItem.Devices.route) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                DeviceScreen(navController, mainViewModel)
            }
        }
        composable(BottomNavItem.Profile.route) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                ProfileScreen(navController, mainViewModel)
            }
        }
    }
}

private fun navigateAuthScreen(
    navController: NavController,
    componentActivity: ComponentActivity
) {
    navController.popBackStack()
    AirPowerUtil.launchActivity(
        componentActivity,
        AuthActivity::class.java
    )
    componentActivity.finish()
}
