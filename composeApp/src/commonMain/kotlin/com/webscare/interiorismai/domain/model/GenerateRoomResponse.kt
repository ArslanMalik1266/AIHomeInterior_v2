package com.webscare.interiorismai.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateRoomResponse(
    @SerialName("status") val status: String = "",
    @SerialName("id") val id: Long? = null,
    @SerialName("output") val output: List<String> = emptyList(),
    @SerialName("generationTime") val generationTime: Double? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("fetch_result") val fetchResult: String? = null, // ✅ Add karo
    @SerialName("eta") val eta: Int? = null,
) {
    val success: Boolean get() = status == "success" || status == "processing"
    val isSuccess: Boolean get() = status == "success"
    val images: List<String> get() = output
    val isProcessing: Boolean get() = status == "processing"
    val job_id: String? get() = id?.toString()
    val count: Int get() = output.size
    val availableImages: List<String> get() = output
    val fetchUrl: String? get() = fetchResult
}