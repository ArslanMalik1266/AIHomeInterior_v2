package com.webscare.interiorismai.data.mapper

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import com.webscare.interiorismai.data.remote.dto.GenerateRoomRequestDto
import com.webscare.interiorismai.data.remote.dto.GenerateRoomResponseDto
import com.webscare.interiorismai.domain.model.GenerateRoomRequest
import com.webscare.interiorismai.domain.model.GenerateRoomResult

fun GenerateRoomResponseDto.toDomain(): GenerateRoomResult {
    // 1. Output ko safely handle karein
    val imagesList = when (val outputElement = output) {
        is JsonArray -> {
            // Agar array hai (e.g. ["url1", "url2"]), to extract karein
            outputElement.map { it.jsonPrimitive.content }
        }
        is JsonPrimitive -> {
            // Agar string hai (e.g. ""), to khali list bhejien
            emptyList<String>()
        }
        else -> {
            // Agar null hai, to bhi khali list
            emptyList<String>()
        }
    }

    return GenerateRoomResult(
        status = status,
        eta = eta,
        fetchUrl = fetchResult,
        id = id,
        images = imagesList, // Ab types match ho jayengi
        futureLinks = futureLinks
    )
}

fun GenerateRoomRequest.toDto(): GenerateRoomRequestDto {
    return GenerateRoomRequestDto(
        initImage = initImage,
        prompt = prompt,
        strength = strength.toString(),
        negativePrompt = negativePrompt
    )
}