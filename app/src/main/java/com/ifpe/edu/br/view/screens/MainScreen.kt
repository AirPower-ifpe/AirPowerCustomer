package com.ifpe.edu.br.view.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.BottomNavItem
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomNavigationBar
import com.ifpe.edu.br.common.components.GradientBackground
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientDark
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientLight
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    CustomColumn(
        alignmentStrategy = CommonConstants.ALIGNMENT_TOP,
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
                GradientBackground(if (isSystemInDarkTheme()) defaultBackgroundGradientDark else defaultBackgroundGradientLight)
                NavHostContainer(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    mainViewModel = mainViewModel
                )
            }
        }
    )
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