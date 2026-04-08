package com.webscare.interiorismai.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openEmail(to: String, subject: String, body: String) {
    val urlString = "mailto:$to?subject=${subject.encodeURL()}&body=${body.encodeURL()}"
    val url = NSURL.URLWithString(urlString) ?: return

    UIApplication.sharedApplication.openURL(
        url = url,
        options = emptyMap<Any?, Any>(),
        completionHandler = null
    )
}

private fun String.encodeURL(): String =
    this.replace(" ", "%20")
        .replace("@", "%40")
        .replace("&", "%26")
        .replace("?", "%3F")
        .replace("=", "%3D")

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(
        url = nsUrl,
        options = emptyMap<Any?, Any>(),
        completionHandler = null
    )
}