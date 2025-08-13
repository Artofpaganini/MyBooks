plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

kotlin {
    androidTarget()
    jvm("desktop")
    jvmToolchain(19)
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 29
        lint.targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
}
