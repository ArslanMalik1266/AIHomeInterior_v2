package com.webscare.interiorismai.domain.model

import androidx.compose.ui.graphics.Color

data class RoomUi(
    val id: Int,
    val imageUrl: String,
    val compressedImageUrl: String,
    val roomStyle: String,
    val roomType: String,
    val isTrending: Int,
    val paletteName: String,
    val createdAt: String,
    val updatedAt: String,
    val colors: List<Color>,
    val cardHeight: Int
)