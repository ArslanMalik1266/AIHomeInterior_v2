package com.webscare.interiorismai.utils

actual fun getAppVersion(): String {
    val version = platform.Foundation.NSBundle.mainBundle
        .infoDictionary?.get("CFBundleShortVersionString") as? String ?: "—"
    return "Version $version"
}