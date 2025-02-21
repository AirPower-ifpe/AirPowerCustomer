package com.ifpe.edu.br.view.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavHostController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomInputText
import com.ifpe.edu.br.common.components.CustomProgressDialog
import com.ifpe.edu.br.common.components.NetworkFailureDialog
import com.ifpe.edu.br.common.components.RectButton
import com.ifpe.edu.br.common.components.RoundedImageIcon
import com.ifpe.edu.br.common.ui.theme.White
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.model.dto.AuthUser
import com.ifpe.edu.br.view.MainActivity
import com.ifpe.edu.br.view.ui.theme.DefaultTransparentGradient
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.view.ui.theme.tb_secondary_light
import com.ifpe.edu.br.view.ui.theme.tb_tertiary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel
import com.ifpe.edu.br.viewmodel.util.AirPowerUtil

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AndroidViewModel
) {
    val scrollState = rememberScrollState()
    val airPowerViewModel = viewModel as AirPowerViewModel

    val isLoading by airPowerViewModel.uiStateManager.observeBoolean(
        id = Constants.STATE_AUTH_LOADING
    ).observeAsState(initial = false)
    val hasError by airPowerViewModel.uiStateManager.observeBoolean(
        id = Constants.STATE_AUTH_FAILURE
    ).observeAsState(initial = false)
    val context = LocalContext.current

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
            alignmentStrategy = CommonConstants.ALIGNMENT_CENTER,
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
                                viewModel.authenticate(
                                    AuthUser(
                                        username = login,
                                        password = password
                                    )
                                ) {
                                    val options = ActivityOptionsCompat.makeCustomAnimation(
                                        context,
                                        R.anim.enter_from_right,
                                        R.anim.exit_to_left
                                    )
                                    navController.popBackStack()
                                    AirPowerUtil.launchActivity(
                                        context,
                                        MainActivity::class.java,
                                        options.toBundle()
                                    )
                                    (context as? ComponentActivity)?.finish()
                                }
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

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            CustomProgressDialog(
                modifier = Modifier.align(Alignment.Center),
                indicatorColor = tb_secondary_light,
                textColor = tb_primary_light
            ) { DefaultTransparentGradient() }
        }
    }

    if (hasError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            NetworkFailureDialog(
                drawableResId = R.drawable.network_issue,
                iconSize = 150.dp,
                retryCallback = {

                }
            ) { DefaultTransparentGradient() }
        }
    }
}