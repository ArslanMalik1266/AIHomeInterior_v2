package com.webscare.interiorismai.utils

import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationState
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

actual object NotificationManager {
    actual fun initialize() {
        // iOS par agar koi special setup chahiye ho (e.g. badge reset)
    }

    actual fun notifyIfBackground() {
        if (isAppInBackground()) {
            val content = UNMutableNotificationContent().apply {
                setTitle("Your Design is Ready!")
                setBody("Your AI interior design is ready to view.")
                setSound(UNNotificationSound.defaultSound)
            }

            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = "design_status",
                content = content,
                trigger = null // null matlab foran dikhao
            )

            UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
                error?.let { println("Notification Error: ${it.localizedDescription}") }
            }
        }
    }

    actual fun isNotificationsEnabled(): Boolean {
        return SettingsManager.isNotificationsEnabled()
    }

    actual fun setNotificationsEnabled(enabled: Boolean) {
        SettingsManager.setNotificationsEnabled(enabled)
    }

    actual fun isAppInBackground(): Boolean {
        val state = UIApplication.sharedApplication.applicationState
        // iOS States: Active (0), Inactive (1), Background (2)
        return state == UIApplicationState.UIApplicationStateBackground ||
                state == UIApplicationState.UIApplicationStateInactive
    }
}