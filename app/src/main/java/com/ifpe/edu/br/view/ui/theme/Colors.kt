package com.ifpe.edu.br.view.ui.theme
/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import androidx.compose.ui.graphics.Color
import com.ifpe.edu.br.common.contracts.AppColorScheme

// Light Colors
val primaryLightAppColor = Color(0xFF25669B)
val onPrimaryLightAppColor = Color(0xFAD8DADE)

val secondaryLightAppColor = Color(0xFFFF5722)
val onSecondaryLightAppColor = Color(0xFFEAE3E1)

val tertiaryLightAppColor = Color(0x8AE8E295)
val onTertiaryLightAppColor = Color(0xFF86A2E0)

val backgroundLightAppColor = Color(0xFFF3F1F1)
val onBackgroundLightAppColor = Color(0xFF636465)

val surfaceLightAppColor = Color(0xF3E8E7E7)
val onSurfaceLightAppColor = Color(0xFF42586B)

val primaryContainerLightAppColor = Color(0x59C0C0C2)
val onPrimaryContainerLightAppColor = Color(0xFF63747E)

val secondaryContainerLightAppColor = Color(0x12000000)
val onSecondaryContainerLightAppColor = Color(0xFF636D7E)

// Dark Colors
val primaryDarkAppColor = Color(0xFF3E608A)
val onPrimaryDarkAppColor = Color(0xFFC5C7CB)

val secondaryDarkAppColor = Color(0xFFFF5722)
val onSecondaryDarkAppColor = Color(0xFFDCD4D2)

val primaryContainerDarkAppColor = Color(0x2DB0B1B2)
val onPrimaryContainerDarkAppColor = Color(0xFFD3DEEC)

val secondaryContainerDarkAppColor = Color(0x0FFFFFFF)
val onSecondaryContainerDarkAppColor = Color(0xD3D3D6EC)

val tertiaryDarkAppColor = Color(0x9CF5DE52)
val onTertiaryDarkAppColor = Color(0xFF6F9CDC)

val backgroundDarkAppColor = Color(0xFF313136)
val onBackgroundDarkAppColor = Color(0xFFCBD0D5)

val surfaceDarkAppColor = Color(0xE91F1F1F)
val onSurfaceDarkAppColor = Color(0xFFD7DCEA)

val lightAppThemeSchema = AppColorScheme(
    primary = primaryLightAppColor,
    onPrimary = onPrimaryLightAppColor,

    secondary = secondaryLightAppColor,
    onSecondary = onSecondaryLightAppColor,

    tertiary = tertiaryLightAppColor,
    onTertiary = onTertiaryLightAppColor,

    primaryContainer = primaryContainerLightAppColor,
    onPrimaryContainer = onPrimaryContainerLightAppColor,

    secondaryContainer = secondaryContainerLightAppColor,
    onSecondaryContainer = onSecondaryContainerLightAppColor,

    background = backgroundLightAppColor,
    onBackground = onBackgroundLightAppColor,

    surface = surfaceLightAppColor,
    onSurface = onSurfaceLightAppColor
)

val darkAppThemeSchema = AppColorScheme(
    primary = primaryDarkAppColor,
    onPrimary = onPrimaryDarkAppColor,

    secondary = secondaryDarkAppColor,
    onSecondary = onSecondaryDarkAppColor,

    primaryContainer = primaryContainerDarkAppColor,
    onPrimaryContainer = onPrimaryContainerDarkAppColor,

    secondaryContainer = secondaryContainerDarkAppColor,
    onSecondaryContainer = onSecondaryContainerDarkAppColor,

    tertiary = tertiaryDarkAppColor,
    onTertiary = onTertiaryDarkAppColor,

    background = backgroundDarkAppColor,
    onBackground = onBackgroundDarkAppColor,

    surface = surfaceDarkAppColor,
    onSurface = onSurfaceDarkAppColor
)