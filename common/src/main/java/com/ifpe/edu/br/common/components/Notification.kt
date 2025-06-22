package com.ifpe.edu.br.common.components

import android.provider.SyncStateContract.Constants
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.CommonConstants
import com.ifpe.edu.br.common.R
import com.ifpe.edu.br.common.ui.theme.White


// Trabalho de conclusão de curso - IFPE 2025
// Author: Willian Santos
// Project: AirPower Costumer

// Copyright (c) 2025 IFPE. All rights reserved.


@Composable
fun CustomProgressDialog(
    modifier: Modifier = Modifier.wrapContentWidth(),
    message: String = "Aguarde",
    textColor: Color = MaterialTheme.colorScheme.primary,
    indicatorColor: Color = MaterialTheme.colorScheme.secondary,
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
                color = textColor
            )
        })
    }
}

@Composable
fun FailureDialog(
    modifier: Modifier = Modifier.wrapContentWidth(),
    iconSize: Dp = 110.dp,
    text: String = "Um erro ocorreu",
    textColor: Color = MaterialTheme.colorScheme.primary,
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.padding(vertical = 40.dp))

                CustomText(
                    text = stringResource(id = R.string.connection_failure_retry),
                    alignment = TextAlign.Center,
                    fontSize = 18.sp,
                    color = textColor
                )
            })
    }
}