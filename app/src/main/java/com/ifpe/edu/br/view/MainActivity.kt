package com.ifpe.edu.br.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.view.screens.MainScreen
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
            val viewmodel = AirPowerViewModelProvider.getInstance()
            AirPowerCostumerTheme {
                MainScreen(
                    navController = navController,
                    mainViewModel = viewmodel,
                    componentActivity = this
                )
            }
        }
    }
}