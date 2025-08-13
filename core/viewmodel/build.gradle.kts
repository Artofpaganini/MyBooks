plugins {
    id("kotlin-multiplatform-setup")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime)
            implementation(libs.napier)
        }
    }
}

android {
    namespace = "org.books.core.viewmodel"

}