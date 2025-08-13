plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId).apply(false)
    id(libs.plugins.kotlin.android.get().pluginId).apply(false)
    id(libs.plugins.android.application.get().pluginId).apply(false)
    id(libs.plugins.android.library.get().pluginId).apply(false)
    id(libs.plugins.compose.asProvider().get().pluginId).apply(false)
    id(libs.plugins.compose.compiler.get().pluginId).apply(false)
    id(libs.plugins.kotlinx.serialization.get().pluginId).apply(false)
    alias(libs.plugins.ksp) apply false
}