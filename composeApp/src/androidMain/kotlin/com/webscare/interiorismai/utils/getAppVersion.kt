package com.webscare.interiorismai.utils

actual fun getAppVersion(): String {
    return try {
        val context = AppContext.get()
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        "Version ${packageInfo.versionName}"
    } catch (e: Exception) {
        "Version —"
    }
}