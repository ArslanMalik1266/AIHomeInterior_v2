package com.webscare.interiorismai.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InteriorType(
    val id: Int,
    val name: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class InteriorTypes(
    val success: Boolean,
    val types: List<InteriorType>
)