package com.webscare.interiorismai.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import com.webscare.interiorismai.utils.Constants
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = documentDirectory() + "/${Constants.DB_NAME}"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
    ) .fallbackToDestructiveMigration(true)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
