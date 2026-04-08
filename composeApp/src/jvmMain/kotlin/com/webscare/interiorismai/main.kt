package com.webscare.interiorismai

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.webscare.interiorismai.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "homeinterior",
    ) {
        App()
    }
}