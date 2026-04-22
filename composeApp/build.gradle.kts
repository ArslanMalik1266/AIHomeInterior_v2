import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.google.services)


}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    sourceSets.all {
        languageSettings {
            languageVersion = "2.1"
            apiVersion = "2.1"
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

//    js {
//        browser()
//        binaries.executable()
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }
    ksp {
        arg("room.generateKotlin", "true")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.ktor.client.okhttp)
            implementation("androidx.work:work-runtime-ktx:2.9.0")
            implementation("io.insert-koin:koin-androidx-workmanager:3.5.0")
            implementation(libs.billing)
            implementation(libs.billing.ktx)
            implementation(libs.credentials)
            implementation(libs.credentials.play.services.auth)
            implementation(libs.google.identity.googleid)


        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.multiplatform.settings)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.coil.network.ktor)
            implementation("com.squareup.okio:okio:3.6.0")
            implementation(libs.coil.compose)
            implementation(libs.coil.svg)
            implementation(compose.components.resources)
            implementation(libs.compottie)
            implementation(libs.compottie.lite)
            implementation(libs.compottie.resources)
            implementation(libs.compottie.dot)
            implementation(libs.compottie.network)
            implementation(libs.navigation.compose)
            implementation(libs.kermit)
            implementation(libs.ktor.client.core)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.encoding)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            //Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)
            //room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)




            implementation(libs.imagepickerkmp)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "com.webscare.interiorismai"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    signingConfigs {
        create("release") {
            storeFile = file("D:\\Android studio projects\\AIHomeInteriorr-main2\\upload-keystore-homeinterior.jks")  // ← apna path
            storePassword = project.findProperty("KEYSTORE_PASSWORD") as String
            keyAlias = project.findProperty("KEY_ALIAS") as String
            keyPassword = project.findProperty("KEY_PASSWORD") as String
        }
    }
    defaultConfig {
        applicationId = "com.webscare.interiorismai"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 4
        versionName = "1.0.4 "

    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true

        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    lint {
        abortOnError = false  // ✅ Yeh add karo
        checkReleaseBuilds = false  // ✅ Yeh bhi
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    debugImplementation(compose.uiTooling)

    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler) // Android
    add("kspIosSimulatorArm64", libs.androidx.room.compiler) // Apple Silicon iOS Simulators
    add("kspIosArm64", libs.androidx.room.compiler) // Real iOS Devices
    add("kspJvm", libs.androidx.room.compiler) // jvm
}
project.configurations.configureEach {
    resolutionStrategy {
        force("androidx.emoji2:emoji2-views-helper:1.3.0")
        force("androidx.emoji2:emoji2:1.3.0")
    }
}




compose.desktop {
    application {
        mainClass = "com.webscare.interiorismai.MainKt"

        nativeDistributions { targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.webscare.interiorismai"
            packageVersion = "1.0.0"
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}