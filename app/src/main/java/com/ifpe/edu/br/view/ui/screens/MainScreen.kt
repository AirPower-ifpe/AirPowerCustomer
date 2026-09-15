package com.ifpe.edu.br.view.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.BottomNavItem
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomIconButton
import com.ifpe.edu.br.common.components.CustomNavigationBar
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.components.CustomTopBar
import com.ifpe.edu.br.common.components.ImageIcon
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.model.repository.remote.dto.AirPowerNotificationItem
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.AuthActivity
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.util.UUID

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


    val notification = mainViewModel.getNotifications().collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screensWithBottomBar = listOf(
        Screen.Home.route,
        Screen.Devices.route,
        Screen.Dashboards.route
    )

    val context = LocalContext.current
    val shouldShowBottomBar = currentRoute in screensWithBottomBar
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Alertas desativados. Ative as notificações nas configurações para receber avisos de alarmes.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    CustomColumn(
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_TOP,
        layouts = listOf {
            Scaffold(
                topBar = {
                    val title = when (currentRoute) {
                        Screen.Home.route -> "Resumo"
                        Screen.Devices.route -> "Dispositivos"
                        Screen.Dashboards.route -> "Dashboards"
                        Screen.DeviceDetail.route -> "Detalhes"
                        Screen.NotificationCenter.route -> "Notificações"
                        "alarms" -> "Central de Alarmes"
                        else -> ""
                    }

                    CustomTopBar(
                        backgroundColor = Color.Transparent,
                        leftContent = {
                            if (shouldShowBottomBar) {
                                CustomIconButton(
                                    iconResId = R.drawable.notification_icon,
                                    iconTint = if (hasNotification(notification.value)) AirPowerTheme.color.secondary else AirPowerTheme.color.onBackground,
                                    contentDescription = "ícone de notificações",
                                    backgroundColor = Color.Transparent,
                                    onClick = {
                                        navController.navigate(Screen.NotificationCenter.route)
                                    }
                                )
                            } else {
                                ImageIcon(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable(
                                            onClick = { navController.popBackStack() }
                                        ),
                                    description = "device icon",
                                    iconResId = R.drawable.arrow_back,
                                    iconTint = AirPowerTheme.color.onPrimaryContainer
                                )
                            }
                        },

                        centerContent = {
                            CustomText(
                                text = title,
                                fontStyle = AirPowerTheme.typography.displayLarge,
                                color = AirPowerTheme.color.onBackground,
                                alignment = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },

                        rightContent = {
                            if (shouldShowBottomBar) {
                                CustomIconButton(
                                    iconResId = R.drawable.menu_icon,
                                    iconTint = AirPowerTheme.color.onBackground,
                                    backgroundColor = Color.Transparent,
                                    contentDescription = "Ícone de menu",
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "Ainda nao implementado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }
                        }
                    )
                },

                bottomBar = {
                    if (shouldShowBottomBar) {
                        CustomNavigationBar(
                            backgroundColor = Color.Transparent,
                            navController = navController,
                            items = listOf(
                                BottomNavItem.Home,
                                BottomNavItem.Devices,
                                BottomNavItem.DashBoards
                            )
                        )
                    }
                }
            ) { innerPadding ->
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
        composable(Screen.Home.route) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                HomeScreen(navController, mainViewModel)
            }
        }
        composable(Screen.Devices.route) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                DeviceScreen(navController, mainViewModel)
            }
        }
        composable(Screen.Dashboards.route) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                DashBoardsScreen(navController, mainViewModel)
            }
        }

        composable(
            route = Screen.DeviceDetail.route,
            arguments = listOf(navArgument("deviceId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val result = runCatching {
                val deviceIdString = backStackEntry.arguments?.getString("deviceId")
                val deviceUuid = UUID.fromString(deviceIdString)
                DeviceDetailScreen(
                    deviceId = deviceUuid,
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
            if (!result.isSuccess) {
                navController.popBackStack()
            }
        }

        composable(
            route = Screen.NotificationCenter.route
        ) {
            NotificationCenterScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }

        composable("alarms") {
            AlarmCenterScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
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

private fun hasNotification(
    notificationSet: List<AirPowerNotificationItem>
): Boolean {
    notificationSet.forEach { item ->
        if (item.status == "SENT") {
            return true
        }
    }
    return false
}
