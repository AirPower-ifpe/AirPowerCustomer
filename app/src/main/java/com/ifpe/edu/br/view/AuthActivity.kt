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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.ui.theme.AirPowerCostumerTheme
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.view.ui.screens.AuthScreen
import com.ifpe.edu.br.view.ui.screens.SplashScreen
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import com.ifpe.edu.br.viewmodel.AirPowerViewModelProvider

class AuthActivity : ComponentActivity() {
    val TAG = "AuthActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "onCreate()")
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        enableEdgeToEdge()
        setContent {
            val adjustedContext = LocalContext.current.adjustedFontScale()
            val navController = rememberNavController()
            val viewModel = AirPowerViewModelProvider.getInstance()

            CompositionLocalProvider(LocalContext provides adjustedContext) {
                AirPowerCostumerTheme {
                    Surface {
                        InitializeNavigation(viewModel, navController, this)
                    }
                }
            }
        }
    }

    override fun finish() {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "finish()")
        super.finish()
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
    mainViewModel: AirPowerViewModel,
    navController: NavHostController,
    componentActivity: ComponentActivity
) {

    NavHost(
        navController = navController,
        startDestination = Constants.NAVIGATION_INITIAL
    ) {
        composable(Constants.NAVIGATION_INITIAL) {
            SplashScreen(
                navController = navController,
                viewModel = mainViewModel,
                componentActivity = componentActivity
            )
        }

        composable(Constants.NAVIGATION_AUTH) {
            AuthScreen(
                navController = navController,
                viewModel = mainViewModel,
                componentActivity = componentActivity
            )
        }
    }
}