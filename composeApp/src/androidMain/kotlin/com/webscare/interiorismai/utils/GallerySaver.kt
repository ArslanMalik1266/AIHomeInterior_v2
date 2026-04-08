package com.webscare.interiorismai.utils

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import org.koin.core.context.GlobalContext
import java.net.URL

actual suspend fun saveImageToGallery(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String
): Boolean {
    return try {
        val context = GlobalContext.get().get<android.app.Application>()

        val bitmap: Bitmap = when {
            // ✅ ByteArray se
            imageBytes != null && imageBytes.isNotEmpty() -> {
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }
            // ✅ Local path se — shuru hota hai /
            imageUrl != null && imageUrl.startsWith("/") -> {
                BitmapFactory.decodeFile(imageUrl)
            }
            // ✅ URL se
            imageUrl != null -> {
                val bytes = URL(imageUrl).readBytes()
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
            else -> return false
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return false

        context.contentResolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        }
        true
    } catch (e: Exception) {
        println("❌ Save failed: ${e.message}")
        false
    }
}