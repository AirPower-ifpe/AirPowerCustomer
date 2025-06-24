package com.ifpe.edu.br.common.components

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.ui.theme.cardBackgroundGradientDark
import com.ifpe.edu.br.common.ui.theme.cardBackgroundGradientLight
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius

@Composable
fun CustomColumn(
    alignmentStrategy: Int = CommonConstants.Ui.ALIGNMENT_TOP,
    layouts: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    if (alignmentStrategy == CommonConstants.Ui.ALIGNMENT_TOP) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            layouts.forEach { layout ->
                layout()
            }
        }
    } else if (alignmentStrategy == CommonConstants.Ui.ALIGNMENT_CENTER) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            layouts.forEach { layout ->
                layout()
            }
        }
    }
}

@Composable
fun CustomCard(
    layouts: List<@Composable () -> Unit>,
    paddingStart: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
    modifier: Modifier = Modifier
        .clip(RoundedCornerShape(cardCornerRadius))
        .fillMaxWidth()
        .background(
            brush = Brush.linearGradient(
                colors = if (isSystemInDarkTheme()) cardBackgroundGradientDark else cardBackgroundGradientLight,
                start = Offset(0f, 0f),
                end = Offset(1000f, 1000f)
            )
        )
) {
    Surface(
        modifier = Modifier.padding(
            start = paddingStart,
            end = paddingEnd,
            top = paddingTop,
            bottom = paddingBottom
        ),
        color = Color.Transparent
    ) {
        Box(
            modifier = modifier
        ) {
            CustomColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                layouts = listOf {
                    layouts.forEach { layout ->
                        layout()
                    }
                })
        }
    }
}

@Composable
fun CustomNavigationBar(
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = Color.Black,
    navController: NavHostController,
    showLabel: Boolean = true,
    items: List<BottomNavItem>
) {
    NavigationBar(
        containerColor = backgroundColor,
        contentColor = contentColor
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = item.icon,
                label = { Text(item.label) },
                alwaysShowLabel = showLabel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    leftContent: @Composable (() -> Unit)? = null,
    centerContent: @Composable () -> Unit,
    rightContent: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                centerContent()
            }
        },
        navigationIcon = {
            if (leftContent != null) {
                Box(modifier = Modifier.padding(start = 0.dp)) {
                    leftContent()
                }
            }
        },
        actions = {
            if (rightContent != null) {
                Box(modifier = Modifier.padding(end = 0.dp)) {
                    rightContent()
                }
            }
        }
    )
}