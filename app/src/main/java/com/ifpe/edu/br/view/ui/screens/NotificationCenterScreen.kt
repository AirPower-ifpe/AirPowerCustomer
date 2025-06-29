package com.ifpe.edu.br.view.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.model.repository.remote.dto.NotificationItem
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
    val context = LocalContext.current
    val notificationSet: List<NotificationItem> = listOf(
        NotificationItem(
            "messagem1",
            System.currentTimeMillis()
        ),
        NotificationItem(
            "messagem12",
            System.currentTimeMillis()
        ),
        NotificationItem(
            "messagem13",
            System.currentTimeMillis()
        ),
        NotificationItem(
            "messagem14",
            System.currentTimeMillis()
        ),
        NotificationItem(
            "messagem15",
            System.currentTimeMillis()
        )
    )

    CustomColumn(
        modifier = Modifier
            .fillMaxSize(),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_TOP,
        layouts = listOf {
            NotificationGrid(
                notificationSet = notificationSet,
                onClick = {
                    Toast.makeText(
                        context,
                        "Essa funcionalidade está em desenvolvimento",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    )
}

@Composable
private fun NotificationGrid(
    notificationSet: List<NotificationItem>,
    onClick: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notificationSet) { deviceItem ->
            NotificationCard(
                item = deviceItem,
                onClick = onClick
            )
        }
    }
}