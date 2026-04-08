package com.webscare.interiorismai.utils

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val CHANNEL_ID   = "image_gen_channel"
private const val CHANNEL_NAME = "Image Generation"
private const val NOTIF_ID     = 1001

object AppContext {
    private lateinit var _context: Context

    private var _activity: android.app.Activity? = null
    fun set(context: Context) { _context = context.applicationContext }
    fun get(): Context = _context
    fun setActivity(activity: android.app.Activity) { _activity = activity }
    fun clearActivity() { _activity = null }
    fun getActivity(): android.app.Activity? = _activity
}

actual object NotificationManager {

    actual fun initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Image generation complete notification"
                enableVibration(true)
                enableLights(true)
            }
            val manager = AppContext.get()
                .getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    actual fun isNotificationsEnabled(): Boolean {
        return SettingsManager.isNotificationsEnabled()
    }

    actual fun setNotificationsEnabled(enabled: Boolean) {
        SettingsManager.setNotificationsEnabled(enabled)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    actual fun notifyIfBackground() {
        if (!isNotificationsEnabled()) {
            println("🔕 Notifications disabled by user — skipping")
            return
        }
        showNotification()
    }

    actual fun isAppInBackground(): Boolean {
        return try {
            val appProcesses = (AppContext.get()
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .runningAppProcesses ?: return true
            val packageName = AppContext.get().packageName
            appProcesses.none {
                it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && it.processName == packageName
            }
        } catch (e: Exception) {
            true
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification() {
        println("🚀 Step 1: showNotification() called")
        val context = AppContext.get()
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            println("❌ Error: App notifications are DISABLED in System Settings!")
            return
        }
        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Interior Design Ready")
            .setContentText("Your design is ready. Tap to view.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Your AI-generated interior design is complete. Tap to explore and download your new space.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        try {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.notify(NOTIF_ID, notification)
            println("✅ Step 3: Notification successfully sent to the system!")
        } catch (e: Exception) {
            println("⚠️ Error during notify: ${e.message}")
        }
    }
}
