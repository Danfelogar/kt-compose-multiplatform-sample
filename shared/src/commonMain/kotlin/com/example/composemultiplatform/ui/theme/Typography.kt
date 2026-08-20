package com.example.composemultiplatform.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import composemultiplatform.shared.generated.resources.Res
import composemultiplatform.shared.generated.resources.inter_variable
import composemultiplatform.shared.generated.resources.space_grotesk_variable
import org.jetbrains.compose.resources.Font

val SpaceGrotesk @Composable get() = FontFamily(
    Font(
        resource = Res.font.space_grotesk_variable,
        weight = FontWeight.Bold
    )
)

val Inter @Composable get() = FontFamily(
    Font(
        resource = Res.font.inter_variable,
        weight = FontWeight.Normal
    ),
    Font(
        resource = Res.font.inter_variable,
        weight = FontWeight.Medium
    ),
)

    val Typography @Composable get() = Typography(
        bodyLarge = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 24.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            lineHeight = 20.sp
        ),
        bodySmall = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 20.sp
        ),
        titleLarge = TextStyle(
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 36.sp
        ),
        titleSmall = TextStyle(
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            lineHeight = 24.sp
        ),
    )