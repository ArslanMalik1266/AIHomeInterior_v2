package com.webscare.interiorismai.data.mapper

import com.webscare.interiorismai.domain.model.Room
import com.webscare.interiorismai.domain.model.RoomDetail
import com.webscare.interiorismai.domain.model.RoomUi


fun RoomDetail.toUi(): RoomUi {
    return RoomUi(
        id = id,
        imageUrl = imageUrl,
        roomStyle = style?.name ?: "",
        roomType = type?.name ?: "",
        isTrending = isTrending,
        paletteName = color?.name ?: "Default Palette",
        createdAt = createdAt ?: "",
        updatedAt = updatedAt ?: "",
        colors = color?.colors?.mapNotNull { it.toColorOrNull() } ?: emptyList(),
        cardHeight = listOf(150, 180, 210, 240, 280).random()
    )
}