package com.webscare.interiorismai.domain.model

data class GenerateRoomResult(
    val status: String,
    val eta: Int?,
    val fetchUrl: String?,
    val id: Long?,
    val images: List<String>,
    val futureLinks: List<String>
) {
    val isProcessing: Boolean get() = status == "processing"
    val isSuccess: Boolean get() = status == "success"
    val availableImages: List<String> get() =
        if (images.isNotEmpty()) images else futureLinks
}