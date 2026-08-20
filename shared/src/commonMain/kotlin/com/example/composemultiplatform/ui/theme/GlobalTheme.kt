package com.example.composemultiplatform.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LightColorTheme = lightColorScheme(
    primary = Primary,
    surface = Surface,
    surfaceContainerLowest = SurfaceLowest,
    background = Background,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant
)

val DarkColorTheme = darkColorScheme(
    primary = Primary,
    surface = Surface,
    surfaceContainerLowest = SurfaceLowest,
    background = Background,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant
)

@Composable
fun GlobalTheme(
    content: @Composable () -> Unit
) {

    val theme = if(isSystemInDarkTheme()){
        DarkColorTheme
    }else{
        LightColorTheme
    }
    MaterialTheme(
        typography = Typography,
        colorScheme = theme,
        content = content
    )

}