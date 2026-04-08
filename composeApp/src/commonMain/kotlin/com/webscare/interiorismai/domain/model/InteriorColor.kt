package com.webscare.interiorismai.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class InteriorColor(
    val id: Int,
    val name: String,
    val colors: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class InteriorColors(
    val success: Boolean,
    val colors: List<InteriorColor>
)