# [Add these for Google One Tap / Credential Manager]
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class androidx.credentials.** { *; }
-keep class com.google.android.gms.auth.api.signin.** { *; }

# [Keep your data models]
-keep class com.webscare.interiorismai.data.** { *; }
-keep class com.webscare.interiorismai.domain.model.** { *; }

# [Koin Rules - already in your file]
-keep class org.koin.** { *; }
-dontwarn org.koin.core.annotation.KoinReflectAPI
-dontwarn org.koin.core.instance.InstanceBuilderKt

# [Serialization - already in your file]
-keepclassmembers class * implements kotlinx.serialization.Serializable {
    *;
}

# [Firebase & Coil - already in your file]
-keep class com.google.firebase.** { *; }
-keep class coil3.** { *; }