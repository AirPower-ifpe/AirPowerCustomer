package com.ifpe.edu.br.common.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.R
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme
import com.ifpe.edu.br.common.ui.theme.White


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


@Composable
fun CustomProgressDialog(
    modifier: Modifier = Modifier.wrapContentWidth(),
    message: String = "Aguarde",
    textColor: Color = AirPowerTheme.color.onBackground,
    fontStyle: TextStyle = LocalTextStyle.current,
    indicatorColor: Color = AirPowerTheme.color.secondary,
    customBackground: @Composable (modifier: Modifier) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        customBackground(modifier)

        CustomColumn(
            alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
            modifier = modifier,
            layouts = listOf {
            CircularProgressIndicator(
                color = White,
                trackColor = indicatorColor,
                modifier = Modifier
                    .size(70.dp)
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            CustomText(
                text = message,
                color = textColor,
                fontStyle = fontStyle
            )
        })
    }
}

@Composable
fun FailureDialog(
    modifier: Modifier = Modifier.wrapContentWidth(),
    iconSize: Dp = 110.dp,
    text: String = "Um erro ocorreu",
    textColor: Color = AirPowerTheme.color.onBackground,
    textStyle: TextStyle = LocalTextStyle.current,
    @DrawableRes drawableResId: Int = R.drawable.generic_error,
    retryCallback: () -> Unit,
    customBackground: @Composable (modifier: Modifier) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable { retryCallback.invoke() }
    ) {

        customBackground(modifier)

        CustomColumn(
            alignmentStrategy = CommonConstants.Ui.ALIGNMENT_CENTER,
            modifier = modifier,
            layouts = listOf {
                Image(
                    painter = painterResource(id = drawableResId),
                    contentDescription = "Ícone de erro de conexão",
                    modifier = Modifier
                        .size(iconSize)
                )

                Spacer(modifier = Modifier.padding(vertical = 15.dp))

                CustomText(
                    text = text,
                    alignment = TextAlign.Center,
                    color = textColor,
                    fontStyle = textStyle
                )

                Spacer(modifier = Modifier.padding(vertical = 40.dp))

                CustomText(
                    text = stringResource(id = R.string.connection_failure_retry),
                    alignment = TextAlign.Center,
                    fontStyle = AirPowerTheme.typography.displayMedium,
                    color = textColor
                )
            })
    }
}