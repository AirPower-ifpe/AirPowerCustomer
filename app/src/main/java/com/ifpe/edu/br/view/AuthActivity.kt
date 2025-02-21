package com.ifpe.edu.br.view
/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.ui.theme.AirPowerCostumerTheme
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.view.screens.AuthScreen
import com.ifpe.edu.br.view.screens.SplashScreen
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import com.ifpe.edu.br.viewmodel.AirPowerViewModelProvider

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        enableEdgeToEdge()
        setContent {
            val adjustedContext = LocalContext.current.adjustedFontScale()
            CompositionLocalProvider(LocalContext provides adjustedContext) {
                AirPowerCostumerTheme {
                    Surface {
                        InitializeNavigation(AirPowerViewModelProvider.getInstance())
                    }
                }
            }
        }
    }

    private fun Context.adjustedFontScale(): Context {
        val configuration = Configuration(this.resources.configuration)
        configuration.fontScale = 1.0f
        configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE
        return createConfigurationContext(configuration)
    }
}

@Composable
private fun InitializeNavigation(
    mainViewModel: AirPowerViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Constants.NAVIGATION_INITIAL
    ) {
        composable(Constants.NAVIGATION_INITIAL) {
            SplashScreen(navController = navController)
        }

        composable(Constants.NAVIGATION_AUTH) {
            AuthScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
    }
}