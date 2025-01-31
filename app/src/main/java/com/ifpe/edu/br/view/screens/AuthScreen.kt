package com.ifpe.edu.br.view.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.R
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomCard
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.CustomInputText
import com.ifpe.edu.br.common.components.GradientBackground
import com.ifpe.edu.br.common.components.RoundedButton
import com.ifpe.edu.br.common.components.RoundedImageIcon
import com.ifpe.edu.br.common.components.TextTitle
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientDark
import com.ifpe.edu.br.common.ui.theme.defaultBackgroundGradientLight
import com.ifpe.edu.br.model.Constants
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

@Composable
fun AuthScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {

    GradientBackground(if (isSystemInDarkTheme()) defaultBackgroundGradientDark else defaultBackgroundGradientLight)
    val scrollState = rememberScrollState()
    CustomColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        alignmentStrategy = CommonConstants.ALIGNMENT_CENTER,
        layouts = listOf {
            CustomCard(
                layouts = listOf {
                    var login by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }

                    Spacer(modifier = Modifier.padding(vertical = 8.dp))

                    TextTitle(
                        textAlign = TextAlign.Center,
                        message = "AirPower App",
                        fontSize = 30.sp
                    )

                    Spacer(modifier = Modifier.padding(vertical = 20.dp))

                    RoundedImageIcon(
                        description = "",
                        iconResId = R.drawable.airpower_icon,
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.padding(vertical = 30.dp))

                    CustomInputText(
                        value = login,
                        onValueChange = { login = it },
                        label = "Email",
                        placeholder = "Digite seu email",
                    )

                    CustomInputText(
                        value = password,
                        onValueChange = { password = it },
                        label = "Senha",
                        placeholder = "Digite sua senha",
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.padding(vertical = 20.dp))

                    RoundedButton(
                        text = "Login",
                        {
                            navController.navigate(Constants.NAVIGATION_MAIN) {
                                popUpTo(Constants.NAVIGATION_AUTH) { inclusive = true }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                })
        })
}