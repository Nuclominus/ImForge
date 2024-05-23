@file:Suppress("UnstableApiUsage")

package core

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.io.File

/**
 * Configure base Kotlin with Android options
 */
// TODO: Migration 2.0
//internal fun Project.configureKotlin() = pluginManager.withPlugin("org.jetbrains.kotlin.android") {
//    configure<KotlinAndroidProjectExtension> {
//        compilerOptions {
//            // Treat all Kotlin warnings as errors (disabled by default)
//            // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
//            val warningsAsErrors: String? by project
//            allWarningsAsErrors.set(warningsAsErrors.toBoolean())
//
//            freeCompilerArgs.set(
//                freeCompilerArgs.get() + listOf(
//                    "-opt-in=kotlin.RequiresOptIn",
//                    "-Xjvm-default=all-compatibility",
//                    "-Xcontext-receivers",
//                ) + buildComposeMetricsParameters()
//            )
//
//            jvmTarget.set(JvmTarget.JVM_19)
//        }
//    }
//}

internal fun Project.configureKotlin(
) = pluginManager.withPlugin("org.jetbrains.kotlin.android") {
    extensions.getByType(CommonExtension::class.java).kotlinOptions {
        // Treat all Kotlin warnings as errors (disabled by default)
        // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
        val warningsAsErrors: String? by project
        allWarningsAsErrors = warningsAsErrors.toBoolean()

        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all-compatibility",
            "-Xcontext-receivers",
        ) + buildComposeMetricsParameters()

        jvmTarget = JavaVersion.VERSION_19.toString()
    }
}

fun CommonExtension<*, *, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

/**
 * Configure Compose-specific options for Kotlin
 * Add metrics and reports parameters if enabled
 */
private fun Project.buildComposeMetricsParameters(): List<String> {
    val buildDirectory: File = layout.buildDirectory.get().asFile
    val metricParameters = mutableListOf<String>()
    val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
    val enableMetrics = (enableMetricsProvider.orNull == "true")
    if (enableMetrics) {
        val metricsFolder = File(buildDirectory, "compose-metrics")
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.absolutePath
        )
    }

    val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
    val enableReports = (enableReportsProvider.orNull == "true")
    if (enableReports) {
        val reportsFolder = File(buildDirectory, "compose-reports")
        metricParameters.add("-P")
        metricParameters.add(
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.absolutePath
        )
    }
    return metricParameters.toList()
}