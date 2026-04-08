package com.webscare.interiorismai.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
actual suspend fun shareImage(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String
) {
    try {
        val uiImage: UIImage? = when {
            // ✅ ByteArray se
            imageBytes != null && imageBytes.isNotEmpty() -> {
                imageBytes.usePinned { pinned ->
                    val nsData = NSData.dataWithBytes(
                        pinned.addressOf(0),
                        imageBytes.size.toULong()
                    )
                    UIImage(data = nsData)
                }
            }
            // ✅ Local path se
            imageUrl != null && imageUrl.startsWith("/") -> {
                val url = NSURL.fileURLWithPath(imageUrl)
                val data = NSData.dataWithContentsOfURL(url) ?: return
                UIImage(data = data)
            }
            // ✅ Remote URL se
            imageUrl != null -> {
                val url = NSURL.URLWithString(imageUrl) ?: return
                val data = NSData.dataWithContentsOfURL(url) ?: return
                UIImage(data = data)
            }
            else -> return
        }

        uiImage ?: return

        val activityVC = UIActivityViewController(
            activityItems = listOf(uiImage),
            applicationActivities = null
        )

        UIApplication.sharedApplication.keyWindow
            ?.rootViewController
            ?.presentViewController(activityVC, animated = true, completion = null)

    } catch (e: Exception) {
        println("❌ iOS Share failed: ${e.message}")
    }
}