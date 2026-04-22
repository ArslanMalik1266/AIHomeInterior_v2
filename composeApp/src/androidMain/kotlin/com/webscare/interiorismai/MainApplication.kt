package com.webscare.interiorismai

import android.app.Application
import com.webscare.interiorismai.di.appModule

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import com.webscare.interiorismai.di.platformModule
import com.webscare.interiorismai.utils.AppContext
import com.webscare.interiorismai.utils.NotificationManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. AppContext aur Notification initialize yahan kar dein
        // Taake Worker ko hamesha context mil sakay
        AppContext.set(this)
        NotificationManager.initialize()

        // 2. Koin ko yahan Start karein
        startKoin {
            androidContext(this@MainApplication)
            workManagerFactory() // Worker injection ke liye lazmi hai
            modules(appModule() + platformModule())
        }
    }
}