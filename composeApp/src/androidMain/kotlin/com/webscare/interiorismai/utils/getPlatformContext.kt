package com.webscare.interiorismai.utils

import androidx.compose.runtime.Composable

@Composable
actual fun getPlatformContext(): Any = androidx.compose.ui.platform.LocalContext.current