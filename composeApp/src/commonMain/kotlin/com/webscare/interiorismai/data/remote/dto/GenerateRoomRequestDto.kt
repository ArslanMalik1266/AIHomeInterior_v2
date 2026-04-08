package com.webscare.interiorismai.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateRoomRequestDto(
    @SerialName("key") val key: String = "mW6KkOydCfGiexA6cvbl3OkoU2a4ZNVodVmrsmImjlTRc0uZz6oMCGbYoFvh",
    @SerialName("init_image") val initImage: String,
    @SerialName("prompt") val prompt: String,
    @SerialName("strength") val strength: String,
    @SerialName("negative_prompt") val negativePrompt: String,
    @SerialName("guidance_scale") val guidanceScale: String = "7.5",
    @SerialName("base64") val base64: String = "true",
    @SerialName("seed") val seed: String = "0",
    @SerialName("num_inference_steps") val numInferenceSteps: String = "51",
    @SerialName("scale_down") val scaleDown: String = "6",
    @SerialName("webhook") val webhook: String? = null,
    @SerialName("track_id") val trackId: String? = null,
)