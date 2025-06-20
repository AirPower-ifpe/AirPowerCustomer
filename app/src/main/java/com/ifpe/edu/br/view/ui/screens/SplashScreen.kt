package com.ifpe.edu.br.view.ui.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.FailureDialog
import com.ifpe.edu.br.common.components.GradientBackground
import com.ifpe.edu.br.common.components.RoundedImageIcon
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientDark
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientLight
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.MainActivity
import com.ifpe.edu.br.view.ui.theme.DefaultTransparentGradient
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: AirPowerViewModel,
    componentActivity: ComponentActivity
) {
    val stateId = Constants.UIStateId.SESSION
    val sessionState = viewModel.uiStateManager.observeUIState(stateId).collectAsState()

    GradientBackground(
        if (isSystemInDarkTheme()) defaultBackgroundGradientDark
        else defaultBackgroundGradientLight
    )

    CustomColumn(
        modifier = Modifier
            .fillMaxSize(),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
        layouts = listOf {
            Spacer(modifier = Modifier.padding(vertical = 100.dp))
            RoundedImageIcon(
                description = "custom icon",
                iconResId = R.drawable.app_icon,
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 100.dp))
            AuthScreenPostDelayed(
                navController = navController,
                viewModel = viewModel,
                componentActivity = componentActivity
            )
        }
    )

    if (sessionState.value.stateCode == Constants.ResponseErrorId.AP_REFRESH_TOKEN_EXPIRED) {
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
                text = sessionState.value.message,
                textColor = tb_primary_light,
                retryCallback = {
                    viewModel.resetUIState(stateId)
                    navigateAuthScreen(navController)
                }
            ) { DefaultTransparentGradient() }
        }
    }
}

@Composable
private fun AuthScreenPostDelayed(
    navController: NavController,
    viewModel: AirPowerViewModel,
    componentActivity: ComponentActivity
) {
    var hasNavigated by rememberSaveable { mutableStateOf(false) }
    val hasCheckedToken = rememberSaveable { mutableStateOf(false) }
    val stateId = Constants.UIStateId.SESSION
    val sessionState = viewModel.uiStateManager.observeUIState(stateId).collectAsState()

    LaunchedEffect(hasCheckedToken.value) {
        if (!hasCheckedToken.value) {
            delay(1500)
            viewModel.isTokenExpired()
            hasCheckedToken.value = true
        }
    }

    if (!hasNavigated) {
        when (sessionState.value.stateCode) {
            Constants.ResponseErrorId.AP_JWT_EXPIRED -> {
                hasNavigated = true
                viewModel.updateSession(
                    onSuccessCallback = {
                        viewModel.resetUIState(stateId)
                        navigateMainActivity(navController, componentActivity)
                    },
                    onFailureCallback = {
                        viewModel.requestLogin(stateId)
                    }
                )
            }

            Constants.UIState.STATE_SUCCESS -> {
                hasNavigated = true
                viewModel.resetUIState(stateId)
                navigateMainActivity(navController, componentActivity)
            }
        }
    }
}

private fun navigateMainActivity(
    navController: NavController,
    componentActivity: ComponentActivity
) {
    navController.popBackStack()
    AirPowerUtil.launchActivity(
        navController.context,
        MainActivity::class.java
    )
    componentActivity.finish()
}

private fun navigateAuthScreen(navController: NavController) {
    navController.navigate(Constants.Navigation.NAVIGATION_AUTH) {
        popUpTo(Constants.Navigation.NAVIGATION_INITIAL) { inclusive = true }
    }
}