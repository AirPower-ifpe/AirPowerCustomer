package com.ifpe.edu.br.view.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.GradientBackground
import com.ifpe.edu.br.common.components.RoundedImageIcon
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientDark
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientLight
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.view.MainActivity
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import com.ifpe.edu.br.viewmodel.util.AirPowerUtil

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: AirPowerViewModel
) {
    GradientBackground(if (isSystemInDarkTheme()) defaultBackgroundGradientDark else defaultBackgroundGradientLight)
    CustomColumn(
        modifier = Modifier
            .fillMaxSize(),
        alignmentStrategy = CommonConstants.ALIGNMENT_CENTER,
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
                viewModel = viewModel
            )
        }
    )
}

@Composable
private fun AuthScreenPostDelayed(
    navController: NavController,
    viewModel: AirPowerViewModel
) {
    var hasNavigated by remember { mutableStateOf(false) }
    if (!hasNavigated) {
        hasNavigated = true
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val options = ActivityOptionsCompat.makeCustomAnimation(
                navController.context,
                R.anim.enter_from_right,
                R.anim.exit_to_left
            )
            if (viewModel.isSessionExpired()) {
                viewModel.updateSession(
                    onSuccessCallback = {
                        navigateMainActivity(navController, options)
                    },
                    onFailureCallback = {
                        navigateAuthScreen(navController)
                    }
                )
            } else {
                navigateMainActivity(navController, options)
            }
        }, 1500)
    }
}


private fun navigateMainActivity(
    navController: NavController,
    options: ActivityOptionsCompat
) {
    AirPowerUtil.launchActivity(
        navController.context,
        MainActivity::class.java,
        options.toBundle()
    )
}

private fun navigateAuthScreen(navController: NavController) {
    navController.navigate(Constants.NAVIGATION_AUTH) {
        popUpTo(Constants.NAVIGATION_INITIAL) { inclusive = true }
    }
}