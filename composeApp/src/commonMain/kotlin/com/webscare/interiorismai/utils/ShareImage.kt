package com.webscare.interiorismai.utils

expect suspend fun shareImage(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String = "interior_design.jpg"
)