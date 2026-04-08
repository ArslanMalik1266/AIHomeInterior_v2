package com.webscare.interiorismai.domain.usecase

import com.webscare.interiorismai.utils.BackgroundTaskScheduler

class StartImageTrackingUseCase(private val scheduler: BackgroundTaskScheduler) {
    operator fun invoke(taskId: String, etaSeconds: Long, fetchUrls: List<String> = emptyList()) {
        scheduler.scheduleImageStatusCheck(taskId, etaSeconds, fetchUrls)
    }
}
