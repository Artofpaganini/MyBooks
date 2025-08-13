
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.uiTooling)
                implementation(compose.preview)
                implementation(compose.uiUtil)
                implementation(compose.animation)
                implementation(compose.animationGraphics)
                implementation(compose.material3)
                implementation(compose.runtimeSaveable)
            }
        }

        androidTarget {
            dependencies {
//                debugImplementation(compose.uiTooling)
//                debugImplementation(compose.preview)
            }
        }
    }
}