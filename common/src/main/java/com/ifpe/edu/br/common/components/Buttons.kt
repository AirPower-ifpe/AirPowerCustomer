package com.ifpe.edu.br.common.components

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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