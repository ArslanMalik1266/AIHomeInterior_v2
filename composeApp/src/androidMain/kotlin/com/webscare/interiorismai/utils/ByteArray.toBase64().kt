package com.webscare.interiorismai.utils

import android.util.Base64

actual fun ByteArray.toBase64(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}