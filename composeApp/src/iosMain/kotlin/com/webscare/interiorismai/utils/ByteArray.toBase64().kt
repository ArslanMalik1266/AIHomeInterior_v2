package com.webscare.interiorismai.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.toBase64(): String {
    val data = this.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = this.size.toULong()
        )
    }
    return data.base64EncodedStringWithOptions(0u)
}