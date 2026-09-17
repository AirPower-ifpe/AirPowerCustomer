/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
package com.ifpe.edu.br.view.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.remote.dto.AirPowerNotificationItem
import com.ifpe.edu.br.view.ui.components.NotificationCard
import com.ifpe.edu.br.viewmodel.AirPowerViewModel


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
            if (notification.value.isEmpty()) {
                EmptyStateNotificationCard()
            } else {
                NotificationGrid(
                    notificationSet = notification.value,
                    viewModel = mainViewModel
                )
            }
        }
    )
}

@Composable
fun EmptyStateNotificationCard() {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        CustomCard(
            modifier = Modifier
                .clip(RoundedCornerShape(cardCornerRadius))
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AirPowerTheme.color.background,
                            AirPowerTheme.color.background.copy(alpha = 0.7f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                ),
            layouts = listOf {
                Spacer(modifier = Modifier.padding(vertical = 65.dp))
                CustomText(
                    alignment = TextAlign.Center,
                    color = AirPowerTheme.color.onBackground,
                    text = "Você não possui notificações",
                    fontStyle = AirPowerTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.padding(vertical = 65.dp))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun NotificationGrid(
    notificationSet: List<AirPowerNotificationItem>,
    viewModel: AirPowerViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = notificationSet,
            key = { notification -> notification.id.id }
        ) { notification ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart ||
                        dismissValue == SwipeToDismissBoxValue.StartToEnd
                    ) {
                        if (notification.status != Constants.NotificationState.READ) {
                            viewModel.markNotificationAsRead(notification.id)
                        }
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = true,
                enableDismissFromEndToStart = true,
                modifier = Modifier.animateItem(
                    fadeInSpec = null,
                    fadeOutSpec = null,
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioNoBouncy
                    )
                ),
                backgroundContent = {
                    val isDismissing = dismissState.targetValue != SwipeToDismissBoxValue.Settled
                    val alignment =
                        if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                            Alignment.CenterStart
                        } else {
                            Alignment.CenterEnd
                        }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        if (isDismissing) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Marcar como lida",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            ) {
                NotificationCard(
                    item = notification,
                    onClick = {}
                )
            }
        }
    }
}