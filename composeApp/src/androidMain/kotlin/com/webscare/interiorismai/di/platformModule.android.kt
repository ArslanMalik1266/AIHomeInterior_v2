package com.webscare.interiorismai.di

import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.Module
import org.koin.dsl.module
import com.webscare.interiorismai.data.local.AppDatabase
import com.webscare.interiorismai.data.local.getDatabaseBuilder
import com.webscare.interiorismai.utils.AndroidTaskScheduler
import com.webscare.interiorismai.utils.BackgroundTaskScheduler
import com.webscare.interiorismai.utils.GoogleSignInHelper
import com.webscare.interiorismai.utils.ImageStatusWorker

actual fun platformModule(): Module = module{
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder(androidContext())
    }
    single<BackgroundTaskScheduler> { AndroidTaskScheduler(get()) }

    // Worker register karein
    worker { ImageStatusWorker(get(), get()) }
    single { GoogleSignInHelper(androidContext()) }

}
