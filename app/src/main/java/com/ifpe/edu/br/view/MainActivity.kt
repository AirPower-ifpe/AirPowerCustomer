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
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn

import com.ifpe.edu.br.common.ui.theme.AirPowerCostumerTheme
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<AirPowerViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val adjustedContext = LocalContext.current.adjustedFontScale()
            CompositionLocalProvider(LocalContext provides adjustedContext) {
                AirPowerCostumerTheme {
                    Surface {
                        val navController = rememberNavController()
                        InitializeNavigation(navController, viewModel)
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
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    NavHost(navController = navController, startDestination = CommonConstants.NAVIGATION_INITIAL,) {
        composable(CommonConstants.NAVIGATION_INITIAL) {
            SplashScreen(navController = navController)
        }

        composable(CommonConstants.NAVIGATION_MAIN) {
            //MainScreen(mainViewModel = mainViewModel)
        }

        composable(CommonConstants.NAVIGATION_AUTH) {
            //AuthScreen(navController = navController, mainViewModel = mainViewModel)
        }
    }
}