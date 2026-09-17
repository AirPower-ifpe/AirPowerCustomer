package com.ifpe.edu.br.view.ui.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomInputText
import com.ifpe.edu.br.common.components.CustomProgressDialog
import com.ifpe.edu.br.common.components.FailureDialog
import com.ifpe.edu.br.common.components.RectButton
import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.common.ui.theme.White
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.persistence.manager.SharedPrefManager
import com.ifpe.edu.br.model.repository.remote.dto.auth.AuthUser
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.MainActivity
import com.ifpe.edu.br.view.ui.components.CustomFullScreenGradientBackground
import com.ifpe.edu.br.view.ui.components.ServerConfigBottomSheet
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AndroidViewModel,
    componentActivity: ComponentActivity
) {
    val scrollState = rememberScrollState()
    val airPowerViewModel = viewModel as AirPowerViewModel
    val authStateKey = Constants.UIStateKey.LOGIN_KEY
    val sessionState = airPowerViewModel.uiStateManager.observeUIState(authStateKey)
        .collectAsState(initial = UIState(Constants.UIState.EMPTY_STATE))
    var showServerConfig by remember { mutableStateOf(false) }
    val theme = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        if (SharedPrefManager.getInstance().isFirstRun) {
            showServerConfig = true
        }
    }

    CustomColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background), // screen background color
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
        layouts = listOf {
            val cardColor = theme.primary
            var isSelectionHandlerFocused by remember { mutableStateOf(false) }
            val appDimens = AirPowerTheme.dimens
            CustomCard(
                paddingStart = appDimens.paddingMedium,
                paddingEnd = appDimens.paddingMedium,
                paddingTop = appDimens.paddingMedium,
                paddingBottom = appDimens.paddingMedium,
                modifier = Modifier
                    .clip(RoundedCornerShape(appDimens.cardCornerRadius))
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isSelectionHandlerFocused = false
                    }
                    .background(cardColor), // background card color
                layouts = listOf {
                    Column {
                        var login by rememberSaveable { mutableStateOf("") }
                        var password by rememberSaveable { mutableStateOf("") }

                        Spacer(modifier = Modifier.padding(vertical = 20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(appDimens.cardCornerRadius))
                                    .wrapContentSize()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "App icon",
                                    modifier = Modifier.height(150.dp),
                                    alignment = Alignment.Center
                                )
                            }
                        }

                        val inputBackgroundColor = lerp(
                            cardColor,
                            MaterialTheme.colorScheme.onPrimary,
                            0.05f
                        )

                        Spacer(modifier = Modifier.padding(vertical = 20.dp))

                        val customSelectionColors = remember(isSelectionHandlerFocused) {
                            TextSelectionColors(
                                handleColor = if (isSelectionHandlerFocused) theme.secondary
                                else Color.Transparent,
                                backgroundColor = theme.secondary.copy(alpha = 0.4f)
                            )
                        }

                        CustomInputText(
                            value = login,
                            onFocusChanged = { focused -> isSelectionHandlerFocused = focused },
                            onValueChange = { login = it },
                            label = "Email",
                            labelColor = theme.onPrimary,
                            labelFontStyle = AirPowerTheme.typography.bodySmall,
                            placeholderFontStyle = AirPowerTheme.typography.button,
                            placeholder = "Digite seu email",
                            placeHolderColor = theme.onPrimary,
                            inputFieldColors = getInputColors(
                                inputBackgroundColor,
                                customSelectionColors,
                                cardColor
                            ),
                            modifier = Modifier
                        )

                        CustomInputText(
                            value = password,
                            onValueChange = { password = it },
                            onFocusChanged = { focused -> isSelectionHandlerFocused = focused },
                            label = "Senha",
                            labelColor = theme.onPrimary,
                            placeholder = "Digite sua senha",
                            placeHolderColor = theme.onPrimary,
                            isPassword = true,
                            labelFontStyle = AirPowerTheme.typography.bodySmall,
                            placeholderFontStyle = AirPowerTheme.typography.button,
                            inputFieldColors = getInputColors(
                                inputBackgroundColor,
                                customSelectionColors,
                                cardColor
                            ),
                            modifier = Modifier,
                            iconColor = White
                        )

                        Spacer(modifier = Modifier.padding(vertical = 20.dp))

                        RectButton(
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                                containerColor = MaterialTheme.colorScheme.secondary,
                                disabledContentColor = MaterialTheme.colorScheme.secondary,
                                disabledContainerColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            fontStyle = AirPowerTheme.typography.button,
                            text = "Login",
                            onClick = {
                                isSelectionHandlerFocused = false
                                viewModel.initSession(
                                    AuthUser(
                                        username = login,
                                        password = password
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 3.dp)
                        )

                        Spacer(modifier = Modifier.padding(vertical = 15.dp))

                        RectButton(
                            colors = ButtonDefaults.buttonColors(
                                contentColor = theme.onPrimary,
                                containerColor = cardColor,
                                disabledContentColor = theme.primary,
                                disabledContainerColor = theme.primary
                            ),
                            fontStyle = AirPowerTheme.typography.button,
                            text = "Configurações de rede",
                            onClick = {
                                isSelectionHandlerFocused = false
                                showServerConfig = true
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                        )

                        Spacer(modifier = Modifier.padding(vertical = 20.dp))
                    }
                })
        }
    )

    if (showServerConfig) {
        ServerConfigBottomSheet(
            onDismiss = { showServerConfig = false },
            onSave = {
                showServerConfig = false
            }
        )
    }

    when (sessionState.value.state) {
        Constants.UIState.STATE_REQUEST_LOGIN -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent.copy(alpha = .8f))
            ) {
                FailureDialog(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    drawableResId = R.drawable.auth_issue,
                    iconSize = 150.dp,
                    text = sessionState.value.state,
                    textColor = theme.onSurface,
                    textStyle = AirPowerTheme.typography.displayLarge,
                    retryCallback = {
                        viewModel.resetUIState(authStateKey)
                    }
                ) {
                    CustomFullScreenGradientBackground(
                        listColor = listOf(theme.surface, theme.surface.copy(alpha = 0.6f))
                    )
                }
            }
        }

        Constants.UIState.GENERIC_ERROR -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent.copy(alpha = .8f))
            ) {
                FailureDialog(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    drawableResId = R.drawable.generic_error,
                    iconSize = 150.dp,
                    text = "Um erro inesperado ocorreu",
                    textStyle = AirPowerTheme.typography.displayLarge,
                    textColor = theme.onSurface,
                    retryCallback = {
                        viewModel.resetUIState(authStateKey)
                    }
                ) { modifier ->
                    CustomFullScreenGradientBackground(
                        modifier,
                        listOf(theme.surface, theme.surface.copy(alpha = 0.6f))
                    )
                }
            }
        }

        Constants.UIState.STATE_NETWORK_ISSUE -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent.copy(alpha = .8f))
            ) {
                FailureDialog(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    drawableResId = R.drawable.network_issue,
                    iconSize = 150.dp,
                    text = "Houve um problema de conexão com o servidor",
                    textColor = theme.onSurface,
                    textStyle = AirPowerTheme.typography.displayLarge,
                    retryCallback = {
                        viewModel.resetUIState(authStateKey)
                    }
                ) { modifier ->
                    CustomFullScreenGradientBackground(
                        modifier,
                        listOf(theme.surface, theme.surface.copy(alpha = 0.6f))
                    )
                }
            }
        }

        Constants.UIState.STATE_LOADING -> {
            Box(
                modifier = Modifier
                    .background(Color.Transparent.copy(alpha = .8f))
            ) {
                CustomProgressDialog(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    indicatorColor = theme.secondary,
                    textColor = theme.onSurface,
                    fontStyle = AirPowerTheme.typography.displayMedium,
                ) { modifier ->
                    CustomFullScreenGradientBackground(
                        modifier,
                        listOf(theme.surface, theme.surface.copy(alpha = 0.6f))
                    )
                }
            }
        }

        Constants.UIState.STATE_SUCCESS -> {
            navController.popBackStack()
            AirPowerUtil.launchActivity(
                componentActivity,
                MainActivity::class.java
            )
            viewModel.resetUIState(authStateKey)
            componentActivity.finish()
        }
    }
}

@Composable
private fun getInputColors(
    inputBackgroundColor: Color,
    customSelectionColors: TextSelectionColors,
    cardColor: Color
): TextFieldColors {
    val theme = MaterialTheme.colorScheme
    return TextFieldDefaults.colors(
        focusedTextColor = theme.onPrimary,
        unfocusedTextColor = theme.onPrimary,
        focusedLabelColor = theme.onPrimary,
        unfocusedLabelColor = theme.onPrimary,
        focusedContainerColor = inputBackgroundColor,
        unfocusedPlaceholderColor = cardColor,
        unfocusedContainerColor = inputBackgroundColor,
        focusedIndicatorColor = cardColor,
        unfocusedIndicatorColor = cardColor,
        selectionColors = customSelectionColors,
        cursorColor = theme.secondary
    )
}