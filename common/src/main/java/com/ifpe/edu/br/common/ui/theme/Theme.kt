package com.ifpe.edu.br.common.ui.theme

/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ifpe.edu.br.common.contracts.AppColorScheme
import com.ifpe.edu.br.common.contracts.WindowInfo
import com.ifpe.edu.br.common.contracts.rememberWindowInfo

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AirPowerCostumerTheme(
    darkAppColorScheme : AppColorScheme = AppColorScheme(),
    lightAppScheme : AppColorScheme = AppColorScheme(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = darkAppColorScheme.primary,
            onPrimary = darkAppColorScheme.onPrimary,
            primaryContainer = darkAppColorScheme.primaryContainer,
            onPrimaryContainer = darkAppColorScheme.onPrimaryContainer,
            secondary = darkAppColorScheme.secondary,
            onSecondary = darkAppColorScheme.onSecondary,
            secondaryContainer = darkAppColorScheme.secondaryContainer,
            onSecondaryContainer = darkAppColorScheme.onSecondaryContainer,
            tertiary = darkAppColorScheme.tertiary,
            onTertiary = darkAppColorScheme.onTertiary,
            background = darkAppColorScheme.background,
            onBackground = darkAppColorScheme.onBackground,
            surface = darkAppColorScheme.surface,
            onSurface = darkAppColorScheme.onSurface,
        )
        else -> lightColorScheme(
            primary = lightAppScheme.primary,
            onPrimary = lightAppScheme.onPrimary,
            primaryContainer = lightAppScheme.primaryContainer,
            onPrimaryContainer = lightAppScheme.onPrimaryContainer,
            secondary = lightAppScheme.secondary,
            onSecondary = lightAppScheme.onSecondary,
            secondaryContainer = lightAppScheme.secondaryContainer,
            onSecondaryContainer = lightAppScheme.onSecondaryContainer,
            tertiary = lightAppScheme.tertiary,
            onTertiary = lightAppScheme.onTertiary,
            background = lightAppScheme.background,
            onBackground = lightAppScheme.onBackground,
            surface = lightAppScheme.surface,
            onSurface = lightAppScheme.onSurface,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    val windowInfo = rememberWindowInfo()
    val dimensions = if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {
        compactDimens
    } else {
        expandedDimens
    }

    val typography = if (windowInfo.screenWidthInfo is WindowInfo.WindowType.Compact) {
        compactTypography
    } else {
        expandedTypography
    }

    val densityScaleFactor = when(windowInfo.screenDensity) {
        WindowInfo.DensityType.High -> 1.10f
        WindowInfo.DensityType.Medium -> 1.2f
        WindowInfo.DensityType.Low -> 0.9f
    }

    val dimensionScaleFactor = when(windowInfo.screenDensity) {
        WindowInfo.DensityType.High -> 1.1f
        else -> 1.0f
    }

    val finalTypography = typography.withScale(densityScaleFactor)
    val finalDimensions = dimensions.withScale(dimensionScaleFactor)

    CompositionLocalProvider(
        LocalAppDimens provides finalDimensions,
        LocalAppTypography provides finalTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = androidx.compose.material3.Typography(
                bodyLarge = finalTypography.bodyLarge,
                titleLarge = finalTypography.displayLarge,
                labelSmall = finalTypography.bodySmall,
                labelMedium = finalTypography.bodySmall,
                labelLarge = finalTypography.bodySmall
            ),
            content = content
        )
    }
}

object AirPowerTheme {
    val color: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme

    val dimens: AppDimens
        @Composable
        get() = LocalAppDimens.current

    val typography: AppTypography
        @Composable
        get() = LocalAppTypography.current
}