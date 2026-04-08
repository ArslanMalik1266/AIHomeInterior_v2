package com.webscare.interiorismai.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.setStatusBarStyle

@Composable
actual fun SetStatusBarIcons(isLight: Boolean) {
    SideEffect {
        toggleStatusBarIcons(isLight)
    }
}

actual fun toggleStatusBarIcons(isLight: Boolean) {
    // 1L = Light (White), 0L = Default (Black)
    val style = if (isLight) 1L else 0L
    UIApplication.sharedApplication.setStatusBarStyle(style)
}