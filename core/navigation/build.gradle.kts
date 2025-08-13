plugins {
    id("kotlin-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.navigation)
        }
    }
}

android {
    namespace = "org.books.core.navigation"
}