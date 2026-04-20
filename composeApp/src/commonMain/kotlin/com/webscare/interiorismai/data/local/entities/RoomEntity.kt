package com.webscare.interiorismai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey val id: Int,
    val imageUrl: String,
    val compressedImageUrl: String,
    val roomStyle: String,
    val roomType: String,
    val isTrending: Int,
    val paletteName: String,
    val createdAt: String,
    val updatedAt: String,
    val colorHexCodes: String // Color list ko comma-separated string (e.g., "#FF0000,#00FF00") mein save karenge
)