package com.webscare.interiorismai.utils

import android.provider.Settings
import android.content.Context
import org.koin.java.KoinJavaComponent.get

actual fun getDeviceId(): String {
    val context: Context = get(Context::class.java)

    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    ) ?: "unknown_android"
}