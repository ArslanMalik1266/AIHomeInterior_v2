package com.webscare.interiorismai

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.webscare.interiorismai.ui.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        App()
    }
}