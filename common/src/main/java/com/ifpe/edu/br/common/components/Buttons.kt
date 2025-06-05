package com.ifpe.edu.br.common.components

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifpe.edu.br.common.ui.theme.White
import com.ifpe.edu.br.common.ui.theme.cardCornerRadius

@Composable
fun RoundedButton(
    text: String,
    onClick: () -> Unit,
    fontSize: TextUnit = 20.sp,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(cardCornerRadius),
        colors = ButtonColors(
            contentColor = White,
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun RectButton(
    text: String,
    onClick: () -> Unit,
    fontSize: TextUnit = 20.sp,
    colors: ButtonColors = ButtonColors(
        contentColor = White,
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContentColor = Color.Gray,
        disabledContainerColor = Color.Gray
    ),
    modifier: Modifier = Modifier
        .fillMaxWidth()
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        shape = RectangleShape
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun CustomIconButton(
    @DrawableRes iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(50.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    iconTint: Color = Color.White,
    shape: Shape = RectangleShape
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = shape)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(iconTint),
            modifier = Modifier.fillMaxSize()
        )
    }
}