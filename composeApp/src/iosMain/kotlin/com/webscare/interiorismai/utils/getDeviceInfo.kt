package com.webscare.interiorismai.utils

actual fun getDeviceInfo(): String {
    return "Device: ${platform.UIKit.UIDevice.currentDevice.model}\niOS: ${platform.UIKit.UIDevice.currentDevice.systemVersion}"
}