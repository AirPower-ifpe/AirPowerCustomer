package com.ifpe.edu.br.view.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomIconButton
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.components.CustomTopBar
import com.ifpe.edu.br.common.components.TextTitle
import com.ifpe.edu.br.model.repository.remote.dto.DeviceSummary
import com.ifpe.edu.br.view.ui.components.DeviceCard
import com.ifpe.edu.br.view.ui.theme.app_default_solid_background_color
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import java.util.UUID

@Composable
fun DeviceScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val deviceSummary by mainViewModel.getDevicesSummary().observeAsState(initial = emptyList())

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mainViewModel.startDataFetchers()
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                backgroundColor = app_default_solid_background_color,
                leftContent = {
                    CustomIconButton(
                        iconResId = R.drawable.notification_icon,
                        iconTint = tb_primary_light,
                        contentDescription = "ícone de notificações",
                        backgroundColor = Color.Transparent,
                        onClick = {
                            Toast.makeText(
                                context,
                                "Ainda nao implementado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
                centerContent = {
                    CustomText(
                        text = "Dispositivos",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        color = tb_primary_light,
                        alignment = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                rightContent = {
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
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(app_default_solid_background_color)
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            when {
//                isLoading && deviceSummary.isEmpty() -> {
//                    CircularProgressIndicator()
//                }
                deviceSummary.isEmpty() -> {
                    CustomColumn(
                        modifier = Modifier.fillMaxSize(),
                        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
                        layouts = listOf {
                            TextTitle(
                                textColor = tb_primary_light,
                                textAlign = TextAlign.Center,
                                message = "Nenhum dispositivo encontrado.\nVerifique sua conexão ou adicione novos dispositivos."
                            )
                        }
                    )
                }

                else -> {
                    DeviceGrid(deviceCards = deviceSummary) { deviceId ->
                        // navController.navigate("deviceDetail/${deviceId}")
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceGrid(
    deviceCards: List<DeviceSummary>,
    onClick: (UUID) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(deviceCards, key = { it.id }) { deviceItem ->
            DeviceCard(device = deviceItem, onClick = onClick)
        }
    }
}