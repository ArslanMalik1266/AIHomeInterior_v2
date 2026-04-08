package com.webscare.interiorismai.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext
import java.io.File
import java.net.URL

actual suspend fun downloadAndCacheImage(
    url: String,
    fileName: String
): String? {
    return withContext(Dispatchers.IO) {
        try {
            val context = GlobalContext.get().get<android.app.Application>()
            val dir = File(context.filesDir, "generated_images")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            if (file.exists()) return@withContext file.absolutePath

            val responseText = URL(url).readText() // ✅ readText — bytes nahi

            // ✅ Base64 URL hai to decode karo
            val imageBytes = if (url.endsWith(".base64")) {
                android.util.Base64.decode(
                    responseText.trim(),
                    android.util.Base64.DEFAULT
                )
            } else {
                URL(url).readBytes()
            }

            file.writeBytes(imageBytes)
            file.absolutePath
        } catch (e: Exception) {
            println("❌ Cache failed: ${e.message}")
            null
        }
    }
}
actual fun getImageModel(path: String?): Any? {
    if (path == null) return null
    return if (path.startsWith("/")) {
        File(path)  // ✅ Local path — File object
    } else {
        path  // ✅ URL — string as is, File mat banao
    }
}

actual suspend fun saveImageBytes(
    bytes: ByteArray,
    fileName: String
): String? {
    return withContext(Dispatchers.IO) {
        try {
            val context = GlobalContext.get().get<android.app.Application>()
            val dir = File(context.filesDir, "generated_images")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            file.writeBytes(bytes)
            file.absolutePath
        } catch (e: Exception) {
            println("❌ Save failed: ${e.message}")
            null
        }
    }
}