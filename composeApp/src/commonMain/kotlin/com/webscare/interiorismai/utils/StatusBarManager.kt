package com.webscare.interiorismai.utils

import androidx.compose.runtime.Composable

@Composable
expect fun SetStatusBarIcons(isLight: Boolean)

// Ye normal function hai jo onDispose mein chalega
expect fun toggleStatusBarIcons(isLight: Boolean)