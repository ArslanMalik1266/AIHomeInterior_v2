package com.webscare.interiorismai.data.mapper

import androidx.compose.ui.graphics.Color

fun String.toColorOrNull(): Color? {
    return try {
        val cleanHex = this
            .replace("0x", "", ignoreCase = true)
            .replace("0X", "", ignoreCase = true)
            .replace("#", "")
            .trim()
            .uppercase()

        if (cleanHex.isEmpty()) return null

        // Handle different hex lengths
        val fullHex = when (cleanHex.length) {
            3 -> {
                // RGB shorthand: #fff -> #ffffff
                val r = cleanHex[0].toString().repeat(2)
                val g = cleanHex[1].toString().repeat(2)
                val b = cleanHex[2].toString().repeat(2)
                "FF$r$g$b"
            }
            4 -> {
                // ARGB shorthand: #ffff -> #ffffffff
                val a = cleanHex[0].toString().repeat(2)
                val r = cleanHex[1].toString().repeat(2)
                val g = cleanHex[2].toString().repeat(2)
                val b = cleanHex[3].toString().repeat(2)
                "$a$r$g$b"
            }
            6 -> "FF$cleanHex"  // RGB -> ARGB
            8 -> cleanHex        // Already ARGB
            else -> return null  // Invalid length
        }

        // Parse as ULong and create Color
        val colorLong = fullHex.toULongOrNull(16) ?: return null

        // Extract ARGB components
        val alpha = ((colorLong shr 24) and 0xFFu).toInt()
        val red = ((colorLong shr 16) and 0xFFu).toInt()
        val green = ((colorLong shr 8) and 0xFFu).toInt()
        val blue = (colorLong and 0xFFu).toInt()

        // Create Color using ARGB constructor
        Color(
            red = red / 255f,
            green = green / 255f,
            blue = blue / 255f,
            alpha = alpha / 255f
        )
    } catch (e: Exception) {
        null
    }
}