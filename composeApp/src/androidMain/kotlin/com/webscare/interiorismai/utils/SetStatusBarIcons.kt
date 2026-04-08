package com.webscare.interiorismai.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat


private var currentActivity: Activity? = null
@Composable
actual fun SetStatusBarIcons(isLight: Boolean) {
    val context = LocalContext.current
    SideEffect {
        if (context is Activity) {
            currentActivity = context
            toggleStatusBarIcons(isLight)
        }
    }
}

actual fun toggleStatusBarIcons(isLight: Boolean) {
    currentActivity?.let { activity ->
        val window = activity.window
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isLight
        }
    }
}
