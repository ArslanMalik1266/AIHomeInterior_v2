package com.webscare.interiorismai.utils


import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIImage
import kotlin.coroutines.resume

import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary

@OptIn(ExperimentalForeignApi::class)
actual suspend fun saveImageToGallery(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String
): Boolean {
    return try {
        val uiImage: UIImage? = when {
            imageBytes != null && imageBytes.isNotEmpty() -> {
                imageBytes.usePinned { pinned ->
                    val nsData = NSData.dataWithBytes(
                        pinned.addressOf(0),
                        imageBytes.size.toULong()
                    )
                    UIImage(data = nsData)
                }
            }
            imageUrl != null && imageUrl.startsWith("/") -> {
                val url = NSURL.fileURLWithPath(imageUrl)
                val data = NSData.dataWithContentsOfURL(url) ?: return false
                UIImage(data = data)
            }
            imageUrl != null -> {
                val url = NSURL.URLWithString(imageUrl) ?: return false
                val data = NSData.dataWithContentsOfURL(url) ?: return false
                UIImage(data = data)
            }
            else -> return false
        }

        uiImage ?: return false

        // ✅ PHPhotoLibrary — proper async with real result
        kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            PHPhotoLibrary.sharedPhotoLibrary().performChanges(
                changeBlock = {
                    PHAssetChangeRequest.creationRequestForAssetFromImage(uiImage)
                },
                completionHandler = { success, error ->
                    if (success) {
                        println("✅ iOS Save Success")
                        continuation.resume(true)
                    } else {
                        println("❌ iOS Save Error: ${error?.localizedDescription}")
                        continuation.resume(false)
                    }
                }
            )
        }

    } catch (e: Exception) {
        println("❌ iOS Save failed: ${e.message}")
        false
    }
}