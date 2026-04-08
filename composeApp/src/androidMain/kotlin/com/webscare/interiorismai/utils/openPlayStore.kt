package com.webscare.interiorismai.utils

import android.app.Application


actual fun openPlayStore(packageName: String) {
    try {
        val intent = android.content.Intent(
            android.content.Intent.ACTION_VIEW,
            android.net.Uri.parse("market://details?id=$packageName")
        ).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        AppContext.get().startActivity(intent)
    } catch (e: Exception) {
        // Play Store app nahi hai to browser mein kholo
        try {
            val intent = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            ).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            AppContext.get().startActivity(intent)
        } catch (e2: Exception) {
            e2.printStackTrace()
        }
    }
}