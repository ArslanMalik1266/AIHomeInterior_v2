plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidx.room) apply false
}

subprojects {
    configurations.configureEach {
        resolutionStrategy {
            force("androidx.emoji2:emoji2-views-helper:1.3.0")
            force("androidx.emoji2:emoji2:1.3.0")
        }
    }
}