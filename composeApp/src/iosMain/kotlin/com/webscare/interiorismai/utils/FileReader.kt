package com.webscare.interiorismai.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile

@OptIn(ExperimentalForeignApi::class)
actual suspend fun readLocalFile(path: String): ByteArray? {
    return try {
        val data = NSData.dataWithContentsOfFile(path) ?: return null
        val length = data.length.toInt()
        if (length == 0) return null
        data.bytes?.readBytes(length)
    } catch (e: Exception) {
        println("❌ iOS File read failed: ${e.message}")
        null
    }
}