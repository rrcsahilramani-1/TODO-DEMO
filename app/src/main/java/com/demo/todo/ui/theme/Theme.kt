package com.demo.todo.ui.theme

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
import com.demo.todo.settings.ThemeMode

private val OnIndigoContainerLight = Color(0xFF0F1055)
private val OnTealContainerLight = Color(0xFF002022)
private val OnIndigoDark = Color(0xFF1B2178)
private val OnTealDark = Color(0xFF003739)

private val LightColors = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = IndigoContainerLight,
    onPrimaryContainer = OnIndigoContainerLight,
    secondary = Teal40,
    onSecondary = Color.White,
    secondaryContainer = TealContainerLight,
    onSecondaryContainer = OnTealContainerLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    error = ErrorLight,
)

private val DarkColors = darkColorScheme(
    primary = Indigo80,
    onPrimary = OnIndigoDark,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoContainerLight,
    secondary = Teal80,
    onSecondary = OnTealDark,
    secondaryContainer = TealContainerDark,
    onSecondaryContainer = TealContainerLight,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    error = ErrorDark,
)

@Composable
fun TodoTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
