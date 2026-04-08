package com.webscare.interiorismai.utils

import androidx.compose.ui.graphics.Color

object HelperFunction {
    private fun parseHexColor(hex: String): Color? {
        return try {
            val cleanHex = hex
                .replace("0x", "", ignoreCase = true)
                .replace("#", "")
                .trim()
                .uppercase()

            val fullHex = if (cleanHex.length == 6) "FF$cleanHex" else cleanHex

            val colorValue = fullHex.toLongOrNull(16) ?: return null
            Color(colorValue.toULong())
        } catch (e: Exception) {
            null
        }
    }
}