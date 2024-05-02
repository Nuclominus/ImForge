@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationExtension
import core.configureAndroidCompose
import core.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {

            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)

                defaultConfig.apply {
                    targetSdk = 34
                    multiDexEnabled = true

                    buildFeatures {
                        aidl = false
                        buildConfig = false
                        dataBinding = false
                        viewBinding = false
                        prefab = false
                    }
                }
            }
        }
    }
}