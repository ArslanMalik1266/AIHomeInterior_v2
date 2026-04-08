package com.webscare.interiorismai.utils

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import com.webscare.interiorismai.domain.usecase.FetchGeneratedRoomUseCase
import com.webscare.interiorismai.data.remote.util.ResultState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImageStatusWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val fetchGeneratedRoomUseCase: FetchGeneratedRoomUseCase by inject()

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val taskId = inputData.getString("TASK_ID") ?: return Result.failure()
        val fetchUrls = inputData.getStringArray("FETCH_URLS")?.toList() ?: return Result.failure()

        println("🚀 WORKER: Started taskId=$taskId urls=$fetchUrls")

        var allDone = false
        var retries = 0

        while (retries < 30 && !allDone) {
            var readyCount = 0
            fetchUrls.forEach { url ->
                val result = fetchGeneratedRoomUseCase(url)
                if (result is ResultState.Success) {
                    val data = result.data
                    if (!data.isProcessing && data.availableImages.isNotEmpty()) {
                        readyCount++
                        println("✅ WORKER: Image ready from $url")
                    }
                }
            }
            if (readyCount >= fetchUrls.size) {
                allDone = true
            } else {
                retries++
                delay(5000L)
            }
        }

        // Notification show karo
        NotificationManager.initialize()
        if (NotificationManager.isAppInBackground()) {
            NotificationManager.notifyIfBackground()
        }

        println("✅ WORKER: Done! Notification sent.")
        return Result.success()
    }
}
