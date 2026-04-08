package com.webscare.interiorismai.di

import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import com.webscare.interiorismai.data.local.AppDatabase
import com.webscare.interiorismai.data.local.getDatabaseBuilder
import com.webscare.interiorismai.utils.BackgroundTaskScheduler
import platform.BackgroundTasks.BGProcessingTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSUserDefaults
import platform.Foundation.dateByAddingTimeInterval

@OptIn(ExperimentalForeignApi::class)
actual fun platformModule(): Module = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
    single<BackgroundTaskScheduler> {
        object : BackgroundTaskScheduler {
            override fun scheduleImageStatusCheck(
                taskId: String,
                delaySeconds: Long,
                fetchUrls: List<String>
            ) {
                // FetchUrls UserDefaults mein save karo
                NSUserDefaults.standardUserDefaults.setObject(
                    fetchUrls,
                    forKey = "PENDING_FETCH_URLS"
                )

                val identifier = "com.webscare.interiorismai.imageProcessing"
                val request = BGProcessingTaskRequest(identifier).apply {
                    earliestBeginDate = NSDate().dateByAddingTimeInterval(5.0)
                    requiresNetworkConnectivity = true
                    requiresExternalPower = false
                }
                try {
                    BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
                    println("✅ iOS BGProcessingTask Scheduled! URLs saved: $fetchUrls")
                } catch (e: Exception) {
                    println("❌ iOS Schedule Error: ${e.message}")
                }
            }
        }
    }
}
