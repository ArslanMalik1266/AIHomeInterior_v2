package com.webscare.interiorismai.data.mapper

import com.webscare.interiorismai.data.local.entities.RoomEntity
import com.webscare.interiorismai.domain.model.Room
import com.webscare.interiorismai.domain.model.RoomDetail
import com.webscare.interiorismai.domain.model.RoomUi


fun RoomDetail.toEntity(): RoomEntity {
    return RoomEntity(
        id = id,
        imageUrl = imageUrl,
        compressedImageUrl = compressedImageUrl,
        roomStyle = style?.name ?: "",
        roomType = type?.name ?: "",
        isTrending = isTrending,
        paletteName = color?.name ?: "Default",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        // Colors ko String mein convert karke save karein
        colorHexCodes = color?.colors?.joinToString(",") ?: ""
    )
}

fun RoomEntity.toUi(): RoomUi {
    // 1. Check karein string blank toh nahi
    val colorList = if (colorHexCodes.isBlank()) {
        emptyList()
    } else {
        // 2. Sirf valid strings ko process karein
        colorHexCodes.split(",").filter { it.isNotBlank() }.mapNotNull { it.toColorOrNull() }
    }

    return RoomUi(
        id = id,
        imageUrl = imageUrl,
        compressedImageUrl = compressedImageUrl,
        roomStyle = roomStyle,
        roomType = roomType,
        isTrending = isTrending,
        paletteName = paletteName,
        createdAt = createdAt,
        updatedAt = updatedAt,
        colors = colorList, // Yahan processed list pass karein
        cardHeight = listOf(150, 180, 210, 240, 280).random()
    )
}

