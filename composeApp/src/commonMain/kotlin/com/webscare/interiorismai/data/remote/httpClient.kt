package com.webscare.interiorismai.data.remote

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.webscare.interiorismai.utils.Constants


fun createHttpClientManual(
    settings: Settings
): HttpClient {
    return HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 300_000L  // ✅ 5 minutes
            connectTimeoutMillis = 120_000L  // ✅ 2 minutes
            socketTimeoutMillis = 300_000L   // ✅ 5 minutes
        }

        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    encodeDefaults = true
                }
            )
        }

        defaultRequest {
            val bt = settings.getString(Constants.BT, "")

            if (bt.isNotEmpty()) {
                header(HttpHeaders.Authorization, "Bearer $bt")
            }
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    co.touchlab.kermit.Logger.i { "KTOR_LOG: $message" }
                }
            }
            level = LogLevel.HEADERS
            sanitizeHeader { header ->
                header == HttpHeaders.Authorization
            }
        }
    }
}