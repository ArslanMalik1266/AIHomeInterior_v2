package com.webscare.interiorismai.data.remote.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import com.webscare.interiorismai.data.remote.dto.GenerateRoomRequestDto
import com.webscare.interiorismai.data.remote.dto.GenerateRoomResponseDto

class RoomService(val client: HttpClient,
                  private val baseUrl: String) {

    private fun fullUrl(path: String) = "$baseUrl/$path"

    suspend fun getRooms(): HttpResponse {
        val url = fullUrl("rooms/")
        println("DEBUG_SERVICE: Calling Rooms with Token...")

        return client.get(url) {
            header("Authorization", "Bearer 13|gd4J0SpTwI53hTzn9z6nmOtYSVhu6AdBfB2CY7qw2e1c3419")
        }
    }
    suspend fun getStyles(): HttpResponse {
        println("DEBUG_SERVICE: Calling Styles...")
        return client.get(fullUrl("interiors/styles")) {
        }
    }

    suspend fun getTypes(): HttpResponse {
        println("DEBUG_SERVICE: Calling Types...")
        return client.get(fullUrl("interiors/types")) {
        }
    }
    suspend fun getColors(): HttpResponse {
        println("DEBUG_SERVICE: Calling Colors...")
        return client.get(fullUrl("interiors/colors")) {
        }
    }
    suspend fun generateRoom(
        initImage: String,   // ✅ bytes ki jagah string
        prompt: String,
        strength: Float
    ): GenerateRoomResponseDto {
        println("DEBUG_SERVICE: initImage length -> ${initImage.length}")

        return try {
            val response = client.post("https://modelslab.com/api/v6/interior/make") {
                contentType(ContentType.Application.Json)
                setBody(
                    GenerateRoomRequestDto(
                        initImage = initImage,  // ✅ Direct string
                        prompt = prompt,
                        strength = strength.toString(),
                        negativePrompt = "Blurry, low resolution, distorted proportions, perspective error, unrealistic lighting, messy, cluttered, poorly drawn furniture, floating objects, non-functional layout, extra legs on furniture, missing furniture pieces, low detail, pixelated, grainy, compression artifacts, oversaturated, chromatic aberration, asymmetry, cartoon, illustration, anime, CGI-look, out of frame, duplicate objects, watermark, text, signature, bad anatomy, warped textures, noisy, low-quality render."
                    )
                )
            }
            val rawText = response.bodyAsText()
            println("DEBUG_SERVICE: Raw -> $rawText")
            Json { ignoreUnknownKeys = true }.decodeFromString(rawText)
        } catch (e: Exception) {
            println("DEBUG_SERVICE: ERROR -> ${e.message}")
            throw e
        }
    }

    suspend fun fetchGeneratedRoom(fetchUrl: String): GenerateRoomResponseDto {
        return try {
            val response = client.post(fetchUrl) {
                contentType(ContentType.Application.Json)
                setBody(buildJsonObject {
                    put("key", "mW6KkOydCfGiexA6cvbl3OkoU2a4ZNVodVmrsmImjlTRc0uZz6oMCGbYoFvh")
                })
            }
            val rawText = response.bodyAsText()
            println("DEBUG_FETCH: Raw -> $rawText")
            Json { ignoreUnknownKeys = true }.decodeFromString(rawText)
        } catch (e: Exception) {
            println("DEBUG_FETCH: ERROR -> ${e.message}")
            throw e
        }
    }


}