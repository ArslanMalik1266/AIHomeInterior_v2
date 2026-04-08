package com.webscare.interiorismai.utils

expect suspend fun saveImageToGallery(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String = "home_interior.jpg"
): Boolean