/*
* Trabalho de conclusão de curso - IFPE 2025
* Author: Willian Santos
* Project: AirPower Costumer
*/

package com.ifpe.edu.br.common.contracts

import androidx.compose.ui.graphics.Color
import com.ifpe.edu.br.common.ui.theme.ColorPrimaryLight
import com.ifpe.edu.br.common.ui.theme.ColorSecondaryLight
import com.ifpe.edu.br.common.ui.theme.ColorTertiaryLight
import com.ifpe.edu.br.common.ui.theme.OnPrimaryLight
import com.ifpe.edu.br.common.ui.theme.OnSecondaryContainerLight
import com.ifpe.edu.br.common.ui.theme.SecondaryContainerLight

/**
 * Semantic color scheme for the application.
 *
 * Each color represents a UI role instead of a specific visual value.
 * This allows changing the visual identity without impacting components.
 *
 * General rule:
 * - X    -> background color
 * - onX -> content color (text/icon) displayed on top of X
 */
data class AppColorScheme(
    /**
     * Main brand color.
     *
     * Used for:
     * - Primary buttons (Save, Confirm, Apply)
     * - Highlighted icons
     * - Selection indicators
     */
    val primary: Color = ColorPrimaryLight,

    /**
     * Text/icon displayed on top of [primary].
     */
    val onPrimary: Color = OnPrimaryLight,

    /**
     * Soft version of the [primary] color.
     *
     * Used for:
     * - Chips
     * - Badges
     * - Lightweight buttons
     * - Highlighted backgrounds
     */
    val primaryContainer: Color = ColorPrimaryLight,

    /**
     * Text/icon displayed on top of [primaryContainer].
     */
    val onPrimaryContainer: Color = OnPrimaryLight,

    /**
     * Color for secondary actions.
     *
     * Used for:
     * - Cancel
     * - Back
     * - Edit
     */
    val secondary: Color = ColorSecondaryLight,

    /**
     * Text/icon displayed on top of [secondary].
     */
    val onSecondary: Color = OnPrimaryLight,

    /**
     * Soft version of the [secondary] color.
     *
     * Used for:
     * - Passive states
     * - Alternative actions
     */
    val secondaryContainer: Color = SecondaryContainerLight,

    /**
     * Text/icon displayed on top of [secondaryContainer].
     */
    val onSecondaryContainer: Color = OnSecondaryContainerLight,

    /**
     * Support color for dividers, separators and auxiliary surfaces.
     */
    val tertiary: Color = ColorTertiaryLight,

    /**
     * Text/icon displayed on top of [tertiary].
     */
    val onTertiary: Color = OnPrimaryLight,

    /**
     * Main screen background color.
     */
    val background: Color = SecondaryContainerLight,

    /**
     * Default text color displayed on top of [background].
     */
    val onBackground: Color = OnSecondaryContainerLight,

    // ----------------------------------------------------------------------
    // Elevated Surfaces
    // ----------------------------------------------------------------------

    /**
     * Background color for cards, dialogs, bottom sheets and elevated components.
     */
    val surface: Color = SecondaryContainerLight,

    /**
     * Text/icon displayed on top of [surface].
     */
    val onSurface: Color = OnSecondaryContainerLight,
)
