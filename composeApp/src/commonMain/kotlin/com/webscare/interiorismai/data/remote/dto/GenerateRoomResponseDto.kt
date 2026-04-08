package com.webscare.interiorismai.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GenerateRoomResponseDto(
    @SerialName("status") val status: String = "",
    @SerialName("eta") val eta: Int? = null,
    @SerialName("fetch_result") val fetchResult: String? = null,
    @SerialName("id") val id: Long? = null,
    @SerialName("output") val output: JsonElement? = null,
    @SerialName("future_links") val futureLinks: List<String> = emptyList(),
    @SerialName("message") val message: String? = null,
    @SerialName("tip") val tip: String? = null,

    )