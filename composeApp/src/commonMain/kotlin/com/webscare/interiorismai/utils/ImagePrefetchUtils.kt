package com.webscare.interiorismai.utils

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun fetchImageBytesFromUrl(
    httpClient: HttpClient,
    url: String
): ByteArray? {
    return try {
        httpClient.get(url).readBytes()
    } catch (e: Exception) {
        println("Image prefetch failed: ${e.message}")
        null
    }
}