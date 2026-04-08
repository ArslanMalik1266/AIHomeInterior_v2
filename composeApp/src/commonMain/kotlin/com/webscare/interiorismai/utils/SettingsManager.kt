package com.webscare.interiorismai.utils

import com.russhwolf.settings.Settings

object SettingsManager {
    private val settings: Settings = Settings()

    fun isNotificationsEnabled(): Boolean {
        return settings.getBoolean("notifications_enabled", true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        settings.putBoolean("notifications_enabled", enabled)
    }
}
