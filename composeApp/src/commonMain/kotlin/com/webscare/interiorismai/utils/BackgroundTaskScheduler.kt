package com.webscare.interiorismai.utils

interface BackgroundTaskScheduler {
    fun scheduleImageStatusCheck(taskId: String, delaySeconds: Long, fetchUrls: List<String> = emptyList())
}
