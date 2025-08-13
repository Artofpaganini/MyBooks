plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(libs.classpath.kotlin)
    implementation(libs.classpath.kotlin.serialization)
    implementation(libs.classpath.android)
    implementation(libs.classpath.compose)
    implementation(libs.classpath.compose.compiler)

}