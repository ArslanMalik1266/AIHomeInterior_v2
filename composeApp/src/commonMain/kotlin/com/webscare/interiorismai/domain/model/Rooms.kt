package com.webscare.interiorismai.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rooms(
    @SerialName("rooms") val rooms: List<RoomDetail> = emptyList(),
    @SerialName("success") val success: Boolean = false,
    val message: String? = null
)

@Serializable
data class RoomDetail(
    val id: Int,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("is_trending") val isTrending: Int = 0,
    @SerialName("interior_style_id") val interiorStyleId: Int? = null,
    @SerialName("interior_type_id") val interiorTypeId: Int? = null,
    @SerialName("interior_color_id") val interiorColorId: Int? = null,
    @SerialName("number_of_credits") val numberOfCredits: Int = 1,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val style: InteriorStyle? = null,
    val type: InteriorType? = null,
    val color: InteriorColor? = null
)
