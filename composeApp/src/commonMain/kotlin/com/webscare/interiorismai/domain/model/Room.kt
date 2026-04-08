package com.webscare.interiorismai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val color_palette: List<String>,
    val created_at: String,
    val id: Int,
    val image_url: String,
    val is_trending: Int,
    val room_style: String,
    val room_type: String,
    val updated_at: String
)