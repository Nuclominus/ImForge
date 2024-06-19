package core

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import data.AndroidAppConf
import ext.Configurations
import ext.addBundle
import ext.addLibrary
import ext.versionCatalog
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureAndroidApplication() =
    extensions.getByType<ApplicationExtension>().apply {


        defaultConfig {
            targetSdk = AndroidAppConf.COMPILE_SDK

            versionName = AndroidAppConf.APP_VERSION
            versionCode = AndroidAppConf.VERSION_CODE
            namespace = AndroidAppConf.NAMESPACE

            multiDexEnabled = true
        }

        configureDefaults()

        lint {
            baseline = file("lint-baseline.xml")
            abortOnError = true
        }

//        configureSigningAndroid(this)

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        dependencies {
            // AndroidX
            addBundle(versionCatalog(), "androidx")
            addLibrary(versionCatalog(), "google-gson")

            // Hilt
            addBundle(versionCatalog(), "hilt")
            addLibrary(versionCatalog(), "hilt-compiler", Configurations.Ksp)
            addLibrary(versionCatalog(), "hilt-android-compiler", Configurations.Ksp)

            // Room
            addLibrary(versionCatalog(), "androidx-room")
            addLibrary(versionCatalog(), "androidx-room-compiler", Configurations.Ksp)
        }
    }


internal fun Project.configureDefaults() =
    extensions.getByType(CommonExtension::class.java).apply {
        compileSdk = AndroidAppConf.COMPILE_SDK

        defaultConfig.apply {
            minSdk = AndroidAppConf.MIN_SDK

            buildFeatures {
                // Disable unused features to reduce build time.
                // Enable features as needed in other configurations.
                aidl = false
                buildConfig = false
                dataBinding {
                    enable = false
                }
                viewBinding = false
                prefab = false
                compose = false
            }
        }

        compileOptions {
            sourceCompatibility = AndroidAppConf.javaVersion
            targetCompatibility = AndroidAppConf.javaVersion
        }
    }