# Keep your data models (used by Ktor/Serialization/Room)
# Replace 'com.webscare.interiorismai' with your actual package name
-keep class com.webscare.interiorismai.data.** { *; }
-keep class com.webscare.interiorismai.domain.model.** { *; }

# Keep rules for Koin (essential for DI)
-keep class org.koin.** { *; }

# Keep rules for Firebase (if used)
-keep class com.google.firebase.** { *; }

# Keep rules for Coil (images)
-keep class coil3.** { *; }

# Keep Kotlin Serialization
-keepclassmembers class * implements kotlinx.serialization.Serializable {
    *;
}

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.koin.core.annotation.KoinReflectAPI
-dontwarn org.koin.core.instance.InstanceBuilderKt