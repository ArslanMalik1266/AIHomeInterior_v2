package com.webscare.interiorismai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.webscare.interiorismai.data.local.StringListConverter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "recent_generated")
@TypeConverters(StringListConverter::class)
data class RecentGeneratedEntity @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val localPaths: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val bundleId: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val originalImagePath: String? = null,
    val prompt: String? = null,
    val roomType: String? = null,
    val style: String? = null,
    val paletteId: Int? = null
)