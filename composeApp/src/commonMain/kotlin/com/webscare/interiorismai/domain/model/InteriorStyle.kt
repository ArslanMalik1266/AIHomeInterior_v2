package com.webscare.interiorismai.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InteriorStyle(
    val id: Int,
    val name: String,
    val image: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class InteriorStyles(
    val success: Boolean,
    val styles: List<InteriorStyle>
)