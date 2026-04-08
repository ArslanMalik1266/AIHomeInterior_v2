package com.webscare.interiorismai.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.webscare.interiorismai.data.remote.util.ResultState
import com.webscare.interiorismai.domain.usecase.FetchGeneratedRoomUseCase

class ImageCheckHelper : KoinComponent {
    private val fetchGeneratedRoomUseCase: FetchGeneratedRoomUseCase by inject()
    private val scope = CoroutineScope(Dispatchers.Default)

    fun checkAndNotify(fetchUrls: List<String>, onComplete: () -> Unit) {
        scope.launch {
            var retries = 0
            val pendingUrls = fetchUrls.toMutableList()

            while (retries < 60 && pendingUrls.isNotEmpty()) {
                val doneUrls = mutableListOf<String>()
                pendingUrls.forEach { url ->
                    val result = fetchGeneratedRoomUseCase(url)
                    if (result is ResultState.Success) {
                        val data = result.data
                        if (!data.isProcessing && data.availableImages.isNotEmpty()) {
                            println("✅ iOS: Image ready from $url")
                            doneUrls.add(url)
                        }
                    }
                }
                pendingUrls.removeAll(doneUrls)

                if (pendingUrls.isEmpty()) {
                    // Sab images ready!
                    NotificationManager.initialize()
                    NotificationManager.notifyIfBackground()
                    println("✅ iOS: All images ready! Notification sent!")
                    break
                }

                retries++
                kotlinx.coroutines.delay(5000L)
            }
            onComplete()
        }
    }
}
