package com.webscare.interiorismai.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
actual suspend fun downloadAndCacheImage(
    url: String,
    fileName: String
): String? {
    return withContext(Dispatchers.Default) {
        try {
            val nsUrl = NSURL.URLWithString(url) ?: return@withContext null
            val documentsDir = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory, NSUserDomainMask, true
            ).first() as String
            val dir = "$documentsDir/generated_images"
            NSFileManager.defaultManager.createDirectoryAtPath(
                dir, withIntermediateDirectories = true,
                attributes = null, error = null
            )
            val filePath = "$dir/$fileName"
            if (NSFileManager.defaultManager.fileExistsAtPath(filePath)) {
                return@withContext filePath
            }

            val data = NSData.dataWithContentsOfURL(nsUrl) ?: return@withContext null

            // ✅ Base64 URL hai to decode karo
            val imageData = if (url.endsWith(".base64")) {
                val base64String = NSString.create(data, NSUTF8StringEncoding) as String
                NSData.create(
                    base64EncodedString = base64String.trim(),
                    options = 0u
                ) ?: return@withContext null
            } else {
                data
            }

            imageData.writeToFile(filePath, atomically = true)
            filePath
        } catch (e: Exception) {
            println("❌ iOS Cache failed: ${e.message}")
            null
        }
    }
}
actual fun getImageModel(path: String?): Any? {
    return path
}


@OptIn(ExperimentalForeignApi::class)
actual suspend fun saveImageBytes(
    bytes: ByteArray,
    fileName: String
): String? {
    return withContext(Dispatchers.Default) {
        try {
            val documentsDir = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory, NSUserDomainMask, true
            ).first() as String
            val dir = "$documentsDir/generated_images"
            NSFileManager.defaultManager.createDirectoryAtPath(
                dir, withIntermediateDirectories = true,
                attributes = null, error = null
            )
            val filePath = "$dir/$fileName"

            // ✅ ByteArray to NSData
            val nsData = bytes.usePinned { pinned ->
                NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
            }
            nsData.writeToFile(filePath, atomically = true)
            filePath
        } catch (e: Exception) {
            println("❌ iOS Save failed: ${e.message}")
            null
        }
    }
}