/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/
package com.ifpe.edu.br.view.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.ifpe.edu.br.common.components.CustomText
import com.ifpe.edu.br.common.ui.theme.AirPowerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericDropdownSelector(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    itemLabelMapper: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.padding(AirPowerTheme.dimens.paddingSmall)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = itemLabelMapper(selectedItem),
                onValueChange = {},
                readOnly = true,
                label = {
                    CustomText(
                        text = label,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontStyle = AirPowerTheme.typography.bodySmall
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = filterSelectorColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            CustomText(
                                text = itemLabelMapper(item),
                                color = AirPowerTheme.color.onSecondaryContainer,
                                fontStyle = AirPowerTheme.typography.button
                            )
                        },
                        colors = MenuItemColors(
                            textColor = AirPowerTheme.color.onSecondaryContainer,
                            leadingIconColor = AirPowerTheme.color.onSecondaryContainer,
                            trailingIconColor = AirPowerTheme.color.onSecondaryContainer,
                            disabledTextColor = AirPowerTheme.color.onSecondaryContainer,
                            disabledLeadingIconColor = AirPowerTheme.color.onSecondaryContainer,
                            disabledTrailingIconColor = AirPowerTheme.color.onSecondaryContainer,
                        ),
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Composable
fun CustomFullScreenGradientBackground(
    modifier: Modifier = Modifier.fillMaxSize(),
    listColor: List<Color>,
) {
    Canvas(
        modifier = modifier
    ) {
        scale(scaleX = 1f, scaleY = 1f) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listColor,
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.width
                )
            )
        }
    }
}

@Composable
private fun filterSelectorColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedTextColor = AirPowerTheme.color.onSurface,
    unfocusedTextColor = AirPowerTheme.color.onSurface,
    focusedLabelColor = AirPowerTheme.color.onSurface,
    unfocusedLabelColor = AirPowerTheme.color.onSurface,
    focusedContainerColor = AirPowerTheme.color.secondaryContainer,
    unfocusedContainerColor = AirPowerTheme.color.surface.copy(alpha = 0.7f),
    focusedIndicatorColor = AirPowerTheme.color.onSurface.copy(alpha = 0.9f),
    unfocusedIndicatorColor = AirPowerTheme.color.surface,
    cursorColor = AirPowerTheme.color.secondary,
    focusedPlaceholderColor = AirPowerTheme.color.primary.copy(alpha = 0.6f),
    unfocusedPlaceholderColor = AirPowerTheme.color.onSurface.copy(alpha = 0.3f),
)

@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
) {
    val colors = listOf(
        AirPowerTheme.color.primary,
        AirPowerTheme.color.primary.copy(alpha = 0.1f)
    )
    Box(
        modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(
                brush = Brush.horizontalGradient(colors)
            )
    )
}

@Composable
fun SolidDivider(
    modifier: Modifier = Modifier,
) {
    val colors = listOf(
        AirPowerTheme.color.onPrimaryContainer,
        AirPowerTheme.color.primary.copy(alpha = 0.1f)
    )
    Box(
        modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(
                brush = Brush.radialGradient(colors)
            )
    )
}
