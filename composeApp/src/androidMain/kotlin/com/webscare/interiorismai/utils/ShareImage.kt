package com.webscare.interiorismai.utils

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import org.koin.core.context.GlobalContext
import java.io.File
import java.net.URL

actual suspend fun shareImage(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String
) {
    try {
        val context = GlobalContext.get().get<android.app.Application>()

        val bytes: ByteArray = when {
            // ✅ ByteArray se
            imageBytes != null && imageBytes.isNotEmpty() -> imageBytes
            // ✅ Local path se
            imageUrl != null && imageUrl.startsWith("/") -> File(imageUrl).readBytes()
            // ✅ Remote URL se
            imageUrl != null -> URL(imageUrl).readBytes()
            else -> return
        }

        val file = File(context.cacheDir, fileName)
        file.writeBytes(bytes)

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(intent, "Share Image").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })

    } catch (e: Exception) {
        println("❌ Share failed: ${e.message}")
    }
}