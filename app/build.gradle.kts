import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id("compose-multiplatform-setup")
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.androidx.navigation)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.viewmodel)
            implementation(libs.koin.navigation)
            api(libs.koin.annotations)
        }

        sourceSets.named("commonMain").configure {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }

        commonTest.dependencies {
            implementation(libs.lifecycle.viewmodel)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK","true")
    arg("USE_COMPOSE_VIEWMODEL","true")
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp)
    add("kspAndroid", libs.koin.ksp)
    add("kspJvm", libs.koin.ksp)
    ksp(libs.koin.ksp)
}

android {
    namespace = "org.books"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.books"
        minSdk = libs.versions.android.minSdk.get().toInt()
        //noinspection OldTargetApi
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.books.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.books"
            packageVersion = "1.0.0"
        }
    }
}
