package com.webscare.interiorismai.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openPlayStore(packageName: String) {
    val url = platform.Foundation.NSURL.URLWithString(
        "https://apps.apple.com/app/id$packageName"
    )
    if (url != null) {
        platform.UIKit.UIApplication.sharedApplication.openURL(url)
    }
}