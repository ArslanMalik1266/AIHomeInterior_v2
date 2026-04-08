package com.webscare.interiorismai.di

import androidx.room.RoomDatabase
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import com.webscare.interiorismai.data.local.AppDatabase
import com.webscare.interiorismai.data.local.getRoomDatabase


val databaseModule = module {
    single { getRoomDatabase(get<RoomDatabase.Builder<AppDatabase>>()) }
    single { get<AppDatabase>().draftDao() }
    single { get<AppDatabase>().recentGeneratedDao() }
    single<Settings> { Settings() }

}