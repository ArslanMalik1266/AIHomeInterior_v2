package com.webscare.interiorismai.utils

import android.content.Context
import android.net.Uri

actual fun uriToByteArray(context: Any, uriString: String): ByteArray {
    val ctx = context as Context
    val uri = Uri.parse(uriString)
    return ctx.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
}