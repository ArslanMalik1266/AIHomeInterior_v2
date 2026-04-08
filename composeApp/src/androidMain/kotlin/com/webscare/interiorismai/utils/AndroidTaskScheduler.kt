package com.webscare.interiorismai.utils

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class AndroidTaskScheduler(private val context: Context) : BackgroundTaskScheduler {
    override fun scheduleImageStatusCheck(taskId: String, delaySeconds: Long, fetchUrls: List<String>) {
        val data = Data.Builder()
            .putString("TASK_ID", taskId)
            .putStringArray("FETCH_URLS", fetchUrls.toTypedArray())
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ImageStatusWorker>()
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "image_check_$taskId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        println("✅ WorkManager scheduled for taskId: $taskId, delay: ${delaySeconds}s")
    }
}
