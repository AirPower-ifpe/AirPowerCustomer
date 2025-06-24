package com.ifpe.edu.br.view.ui.screens

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.components.CustomColumn
import com.ifpe.edu.br.common.components.RectButton
import com.ifpe.edu.br.common.components.TextTitle
import com.ifpe.edu.br.model.util.AirPowerUtil
import com.ifpe.edu.br.view.AuthActivity
import com.ifpe.edu.br.view.ui.theme.tb_primary_light
import com.ifpe.edu.br.viewmodel.AirPowerViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    mainViewModel: AirPowerViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    CustomColumn(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
        alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
        layouts = listOf {
            Spacer(modifier = Modifier.padding(vertical = 100.dp))
            TextTitle(
                textColor = tb_primary_light,
                textAlign = TextAlign.Center,
                message = "ProfileScreen"
            )
            RectButton(
                text = "Logout",
                onClick = {
                    mainViewModel.logout()
                    AirPowerUtil.launchActivity(
                        navController.context,
                        AuthActivity::class.java,
                    )
                    navController.popBackStack()
                    (context as? ComponentActivity)?.finish()
                },
                fontSize = 20.sp
            )
        }
    )
}