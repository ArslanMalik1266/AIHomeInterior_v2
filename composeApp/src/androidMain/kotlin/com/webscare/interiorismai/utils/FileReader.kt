package com.webscare.interiorismai.utils

import java.io.File

actual suspend fun readLocalFile(path: String): ByteArray? {
    return try {
        File(path).readBytes()
    } catch (e: Exception) {
        println("❌ File read failed: ${e.message}")
        null
    }
}