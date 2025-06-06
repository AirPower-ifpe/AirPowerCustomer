package com.ifpe.edu.br.view.ui.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.ifpe.edu.br.common.components.RoundedImageIcon
import com.ifpe.edu.br.common.contracts.UIState
import com.ifpe.edu.br.common.ui.theme.White
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.repository.remote.dto.AuthUser
import com.ifpe.edu.br.model.util.AirPowerLog
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.MainActivity
import com.ifpe.edu.br.view.ui.theme.DefaultTransparentGradient
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.view.ui.theme.tb_secondary_light
import com.ifpe.edu.br.view.ui.theme.tb_tertiary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AndroidViewModel,
    componentActivity: ComponentActivity
) {
    val TAG = "AuthScreen"
    LaunchedEffect(Unit) {
        if (AirPowerLog.ISLOGABLE) AirPowerLog.d(TAG, "LaunchedEffect()")
    }
    val scrollState = rememberScrollState()
    val airPowerViewModel = viewModel as AirPowerViewModel

    val uiState by airPowerViewModel.uiStateManager.observeUIState(id = Constants.AUTH_STATE)
        .observeAsState(initial = UIState("", CommonConstants.State.STATE_DEFAULT_SATATE_CODE))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tb_tertiary_light)
    ) {
        CustomColumn(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(tb_tertiary_light),
            alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
            layouts = listOf {
                CustomCard(
                    modifier = Modifier
                        .clip(RoundedCornerShape(cardCornerRadius))
                        .clip(RoundedCornerShape(cardCornerRadius))
                        .fillMaxWidth()
                        .background(tb_primary_light),
                    layouts = listOf {
                        var login by rememberSaveable { mutableStateOf("") }
                        var password by rememberSaveable { mutableStateOf("") }

                        Spacer(modifier = Modifier.padding(vertical = 10.dp))

                        RoundedImageIcon(
                            description = "",
                            iconResId = R.drawable.app_icon,
                            modifier = Modifier
                                .width(250.dp)
                                .height(70.dp)
                        )

                        CustomInputText(
                            value = login,
                            onValueChange = { login = it },
                            label = "Email",
                            placeholder = "Digite seu email",
                            inputFieldColors = TextFieldDefaults.colors(
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedLabelColor = White,
                                unfocusedLabelColor = White,
                                focusedContainerColor = tb_primary_light,
                                unfocusedContainerColor = tb_primary_light
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        CustomInputText(
                            value = password,
                            onValueChange = { password = it },
                            label = "Senha",
                            placeholder = "Digite sua senha",
                            isPassword = true,
                            inputFieldColors = TextFieldDefaults.colors(
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedLabelColor = White,
                                unfocusedLabelColor = White,
                                focusedContainerColor = tb_primary_light,
                                unfocusedContainerColor = tb_primary_light,
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp),
                            iconColor = White
                        )

                        Spacer(modifier = Modifier.padding(vertical = 20.dp))

                        RectButton(
                            colors = ButtonColors(
                                contentColor = White,
                                containerColor = tb_secondary_light,
                                disabledContentColor = Color.Gray,
                                disabledContainerColor = Color.Gray
                            ),
                            text = "Login",
                            fontSize = 15.sp,
                            onClick = {
                                viewModel.initSession(
                                    AuthUser(
                                        username = login,
                                        password = password
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                        )

                        Spacer(modifier = Modifier.padding(vertical = 15.dp))
                    })
            }
        )
    }

    when (uiState.stateCode) {
        CommonConstants.State.STATE_AUTH_FAILURE -> {
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
                        viewModel.resetUIState(Constants.AUTH_STATE)
                    }
                ) { DefaultTransparentGradient() }
            }
        }

        CommonConstants.State.STATE_NETWORK_ISSUE -> {
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
                        viewModel.resetUIState(Constants.AUTH_STATE)
                    }
                ) { modifier -> DefaultTransparentGradient(modifier) }
            }
        }

        CommonConstants.State.STATE_LOADING -> {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                CustomProgressDialog(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),
                    indicatorColor = tb_secondary_light,
                    textColor = tb_primary_light
                ) { modifier ->
                    DefaultTransparentGradient(modifier)
                }
            }
        }

        CommonConstants.State.STATE_SUCCESS -> {
            navController.popBackStack()
            AirPowerUtil.launchActivity(
                componentActivity,
                MainActivity::class.java
            )
            viewModel.resetUIState(Constants.AUTH_STATE)
            componentActivity.finish()
        }
    }
}