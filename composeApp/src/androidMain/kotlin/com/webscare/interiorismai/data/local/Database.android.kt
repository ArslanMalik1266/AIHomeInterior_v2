package com.webscare.interiorismai.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.webscare.interiorismai.utils.Constants.DB_NAME


fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(DB_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).fallbackToDestructiveMigration(true)
}