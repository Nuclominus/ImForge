@file:Suppress("UnstableApiUsage")

package core

import com.android.build.api.dsl.ApplicationExtension
import ext.Configurations
import ext.addBundle
import ext.addLibrary
import ext.addPlatform
import ext.versionCatalog
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Configure Compose-specific options
 */
internal fun Project.configureCompose() = extensions.getByType<ApplicationExtension>().apply {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14" // TODO: remove when integrate K2
    }

    dependencies {
        // Compose dependencies
//        addLibrary(versionCatalog(), "compose-compiler")
        addPlatform(versionCatalog(), "compose-bom")
        addBundle(versionCatalog(), "compose")

        // Test dependencies
        addPlatform(versionCatalog(), "compose-bom", Configurations.TestImplementation)
        addLibrary(versionCatalog(), "compose-ui-test-junit4", Configurations.TestImplementation)
        addLibrary(versionCatalog(), "compose-ui-tooling", Configurations.DebugImplementation)
        addLibrary(versionCatalog(), "compose-ui-test-manifest", Configurations.DebugImplementation)

    }
}