plugins {
    id("kotlin-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.json)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.viewmodel)
            implementation(libs.koin.navigation)
            implementation(libs.koin.ktor)
            implementation(libs.koin.ktor.logger)

        }

        androidMain.dependencies {
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.androidx.navigation)
            implementation(libs.ktor.client.android)
        }
    }
}

android {
    namespace = "org.books.core.network"
}