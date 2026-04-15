package com.webscare.interiorismai.utils

import androidx.compose.runtime.Composable

@Composable
actual fun CommonBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Usually empty on iOS as back is handled by gestures/UINavigationController
}