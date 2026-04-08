package com.webscare.interiorismai.utils

actual fun getDeviceInfo(): String {
    return "Device: ${android.os.Build.MODEL}\nAndroid: ${android.os.Build.VERSION.RELEASE}"
}