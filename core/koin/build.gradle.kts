plugins {
    id("kotlin-multiplatform-setup")
}

kotlin {
    sourceSets {

        androidMain.dependencies {
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.androidx.navigation)
        }

        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.viewmodel)
            implementation(libs.koin.navigation)
        }
    }
}

android {
    namespace = "org.books.core.koin"
}