package com.webscare.interiorismai.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL

@OptIn(ExperimentalForeignApi::class)
actual fun uriToByteArray(context: Any, uriString: String): ByteArray {
    return try {
        // File path se "file://" prefix hata kar direct path lo
        val cleanPath = uriString
            .removePrefix("file://")
            .removePrefix("file:")

        println("DEBUG_IOS: Clean path = $cleanPath")

        // File URL banao
        val url = NSURL.fileURLWithPath(cleanPath)
        println("DEBUG_IOS: NSURL = $url")

        val data = NSData.dataWithContentsOfURL(url)
        println("DEBUG_IOS: Data size = ${data?.length}")

        if (data == null || data.length == 0UL) {
            println("DEBUG_IOS: Data is null or empty!")
            return byteArrayOf()
        }

        // NSData → ByteArray
        memScoped {
            val bytePtr = data.bytes!!
            bytePtr.readBytes(data.length.toInt())
        }
    } catch (e: Exception) {
        println("DEBUG_IOS: ERROR = ${e.message}")
        byteArrayOf()
    }
}