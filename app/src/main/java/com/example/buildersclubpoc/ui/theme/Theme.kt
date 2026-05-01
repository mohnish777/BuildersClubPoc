package com.example.buildersclubpoc.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PlannerGreenDark,
    onPrimary = PlannerNight,
    primaryContainer = PlannerGreen,
    onPrimaryContainer = PlannerInkDark,
    secondary = PlannerLeafDark,
    onSecondary = PlannerNight,
    secondaryContainer = PlannerLeaf,
    onSecondaryContainer = PlannerInkDark,
    tertiary = PlannerPeachDark,
    onTertiary = PlannerNight,
    tertiaryContainer = PlannerCoral,
    onTertiaryContainer = PlannerNight,
    background = PlannerNight,
    onBackground = PlannerInkDark,
    surface = Color(0xFF18201C),
    onSurface = PlannerInkDark,
    surfaceContainerHigh = Color(0xFF22302A),
    onSurfaceVariant = PlannerStoneDark,
)

private val LightColorScheme = lightColorScheme(
    primary = PlannerGreen,
    onPrimary = PlannerCream,
    primaryContainer = PlannerGreenSoft,
    onPrimaryContainer = PlannerInk,
    secondary = PlannerLeaf,
    onSecondary = PlannerCream,
    secondaryContainer = PlannerPeach,
    onSecondaryContainer = PlannerInk,
    tertiary = PlannerCoral,
    onTertiary = PlannerInk,
    tertiaryContainer = Color(0xFFFCE7C9),
    onTertiaryContainer = PlannerInk,
    background = PlannerSand,
    onBackground = PlannerInk,
    surface = PlannerCream,
    onSurface = PlannerInk,
    surfaceContainerHigh = Color(0xFFF7F0E6),
    onSurfaceVariant = PlannerStone,
)

@Composable
fun BuildersClubPocTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
