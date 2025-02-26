package com.ifpe.edu.br.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Observer
import androidx.navigation.compose.rememberNavController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.view.screens.MainScreen
import com.ifpe.edu.br.view.ui.theme.AirPowerCostumerTheme
import com.ifpe.edu.br.viewmodel.AirPowerViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
        enableEdgeToEdge()
        setContent {
            val viewmodel = AirPowerViewModelProvider.getInstance()
            val user by viewmodel.currentUser.observeAsState()
            if (user != null) {
                viewmodel.startFetchingDevices()
            } else {
                AirPowerLog.e("MainActivity", "current user is null")
            }

            val navController = rememberNavController()
            AirPowerCostumerTheme {
                MainScreen(
                    navController = navController,
                    mainViewModel = viewmodel
                )
            }
        }
    }
}
