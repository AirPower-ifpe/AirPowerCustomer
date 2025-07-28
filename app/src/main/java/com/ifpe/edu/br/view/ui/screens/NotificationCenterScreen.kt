package com.ifpe.edu.br.view.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.remote.dto.AirPowerNotificationItem
import com.ifpe.edu.br.view.ui.components.NotificationCard
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
@Composable
fun NotificationCenterScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val notification = mainViewModel.getNotifications().collectAsState()
    CustomColumn(
        modifier = Modifier.fillMaxSize(),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_TOP,
        layouts = listOf {
            NotificationGrid(
                notificationSet = notification.value,
                viewModel = mainViewModel
            )
        }
    )
}

@Composable
private fun NotificationGrid(
    notificationSet: List<AirPowerNotificationItem>,
    viewModel: AirPowerViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notificationSet) { notification ->
            NotificationCard(
                item = notification,
                onClick = {
                    if (notification.status != Constants.NotificationState.READ) {
                        viewModel.markNotificationAsRead(notification.id)
                    }
                }
            )
        }
    }
}