package com.webscare.interiorismai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.urbanistbold
import homeinterior.composeapp.generated.resources.urbanistlight
import homeinterior.composeapp.generated.resources.urbanistmedium
import homeinterior.composeapp.generated.resources.urbanistregular
import homeinterior.composeapp.generated.resources.urbanistsemibold
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
fun urbanistFontFamily() = FontFamily(
    Font(Res.font.urbanistlight, weight = FontWeight.Light),
    Font(Res.font.urbanistregular, weight = FontWeight.Normal),
    Font(Res.font.urbanistmedium, weight = FontWeight.Medium),
    Font(Res.font.urbanistsemibold, weight = FontWeight.SemiBold),
    Font(Res.font.urbanistbold, weight = FontWeight.Bold)
)

@Composable
fun AppTypography() = Typography().run {
    val fontFamily = urbanistFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily)
    )
}