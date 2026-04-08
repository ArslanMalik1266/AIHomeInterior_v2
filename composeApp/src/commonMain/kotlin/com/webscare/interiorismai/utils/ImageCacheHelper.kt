package com.webscare.interiorismai.utils

expect suspend fun downloadAndCacheImage(
    url: String,
    fileName: String
): String?

expect fun getImageModel(path: String?): Any?

expect suspend fun saveImageBytes(
    bytes: ByteArray,
    fileName: String
): String?