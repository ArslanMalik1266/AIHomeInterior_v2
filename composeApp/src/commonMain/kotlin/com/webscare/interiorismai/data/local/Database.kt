package com.webscare.interiorismai.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import com.webscare.interiorismai.data.local.dao.DraftDao // Import add karein
import com.webscare.interiorismai.data.local.dao.RecentGeneratedDao
import com.webscare.interiorismai.data.local.dao.RoomDao
import com.webscare.interiorismai.data.local.entities.DraftEntity // Import add karein
import com.webscare.interiorismai.data.local.entities.RecentGeneratedEntity
import com.webscare.interiorismai.data.local.entities.RoomEntity

@Database(
    entities = [DraftEntity::class,
               RecentGeneratedEntity::class,
        RoomEntity::class],
    version = 1
)
@TypeConverters(StringListConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao
    abstract fun recentGeneratedDao(): RecentGeneratedDao
    abstract fun roomDao(): RoomDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.Default)
    .build()