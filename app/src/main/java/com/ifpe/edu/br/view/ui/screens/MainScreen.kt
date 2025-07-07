package com.ifpe.edu.br.view.ui.screens

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.ifpe.edu.br.common.components.FailureDialog
import com.ifpe.edu.br.common.components.GradientBackground
import com.ifpe.edu.br.model.repository.remote.dto.NotificationItem
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.AuthActivity
import com.ifpe.edu.br.view.ui.theme.DefaultTransparentGradient
import com.ifpe.edu.br.view.ui.theme.appBackgroundGradientDark
import com.ifpe.edu.br.view.ui.theme.appBackgroundGradientLight
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.view.ui.theme.tb_secondary_light
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

    val notification = mainViewModel.getNotifications().collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screensWithBottomBar = listOf(
        Screen.Home.route,
        Screen.Devices.route,
        Screen.Profile.route
    )

    val context = LocalContext.current
    val shouldShowBottomBar = currentRoute in screensWithBottomBar

    CustomColumn(
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_TOP,
        layouts = listOf {
            Scaffold(
                topBar = {
                    val title = when (currentRoute) {
                        Screen.Home.route -> "Início"
                        Screen.Devices.route -> "Dispositivos"
                        Screen.Profile.route -> "Dashboards"
                        Screen.DeviceDetail.route -> "Detalhes do Dispositivo"
                        Screen.NotificationCenter.route -> "Centro de Notificações"
                        else -> ""
                    }

                    CustomTopBar(
                        backgroundColor = Color.Transparent,
                        leftContent = {
                            if (shouldShowBottomBar) {
                                CustomIconButton(
                                    iconResId = R.drawable.notification_icon,
                                    iconTint = if (hasNotification(notification.value)) tb_secondary_light else tb_primary_light,
                                    contentDescription = "ícone de notificações",
                                    backgroundColor = Color.Transparent,
                                    onClick = {
                                        navController.navigate(Screen.NotificationCenter.route)
                                    }
                                )
                            } else {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Voltar"
                                    )
                                }
                            }

                        },

                        centerContent = {
                            CustomText(
                                text = title,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Medium,
                                color = tb_primary_light,
                                alignment = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },

                        rightContent = {
                            if (shouldShowBottomBar) {
                                CustomIconButton(
                                    iconResId = R.drawable.menu_icon,
                                    iconTint = tb_primary_light,
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
                                BottomNavItem.Profile
                            )
                        )
                    }
                }
            ) { innerPadding ->
                GradientBackground(
                    if (isSystemInDarkTheme()) appBackgroundGradientDark
                    else appBackgroundGradientLight
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
        composable(Screen.Profile.route) {
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
            val deviceId = backStackEntry.arguments?.getString("deviceId")
            if (deviceId != null) {
                DeviceDetailScreen(
                    deviceId = deviceId,
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            } else {
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
    notificationSet: List<NotificationItem>
): Boolean {
    notificationSet.forEach { item ->
        if (item.isNew) {
            return true
        }
    }
    return false
}
