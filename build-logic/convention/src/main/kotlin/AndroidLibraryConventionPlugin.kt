import com.android.build.gradle.LibraryExtension
import core.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("io.gitlab.arturbosch.detekt")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                defaultConfig.apply {
                    targetSdk = 34

                    buildFeatures {
                        buildConfig = false
                        dataBinding = false
                        viewBinding = false
                    }
                }
            }
        }
    }
}