package com.webscare.interiorismai

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.android.ext.koin.androidContext
import com.webscare.interiorismai.ui.App
import com.webscare.interiorismai.utils.AppContext
import com.webscare.interiorismai.utils.NotificationManager

class MainActivity : ComponentActivity() {


    override fun onResume() {
        super.onResume()
        AppContext.setActivity(this)  // ✅
    }


    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            println("✅ Notification permission granted!")
        } else {
            println("❌ Notification permission denied!")
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // ✅ Notification setup
        AppContext.set(this)
        NotificationManager.initialize()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

// ✅ Yeh lagao — sab versions pe kaam karega
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(androidx.core.view.WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        window.statusBarColor = android.graphics.Color.TRANSPARENT


        setContent {
            App({
                androidContext(this@MainActivity.applicationContext)
            })
        }
    }

}