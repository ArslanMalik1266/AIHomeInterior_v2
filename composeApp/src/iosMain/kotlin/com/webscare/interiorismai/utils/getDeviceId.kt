package com.webscare.interiorismai.utils

import platform.UIKit.UIDevice

actual fun getDeviceId(): String {
    return UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown_ios"
}