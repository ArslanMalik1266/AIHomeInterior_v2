package com.webscare.interiorismai.utils

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

actual fun openEmail(to: String, subject: String, body: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // ✅ yeh add karo
        }
        AppContext.get().startActivity(
            Intent.createChooser(intent, "Send Email").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // ✅ chooser pe bhi
            }
        )
    } catch (e: Exception) {
        e.printStackTrace()  // ✅ error dekho logcat mein
    }
}

actual fun openUrl(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        AppContext.get().startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}