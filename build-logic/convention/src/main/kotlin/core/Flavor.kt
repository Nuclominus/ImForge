package core

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class FlavorDimension {
    environment,
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
@Suppress("EnumEntryName")
enum class Flavors(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
    val versionNameSuffix: String? = null
) {
    development(FlavorDimension.environment, ".dev", "-development"),
    production(FlavorDimension.environment)
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: Flavors) -> Unit = {}
) {
    commonExtension.apply {

        flavorDimensions += FlavorDimension.environment.name

        productFlavors {
            Flavors.values().forEach {
                create(it.name) {
                    // main block configuration
                    dimension = it.dimension.name

                    // custom configuration
                    flavorConfigurationBlock(this, it)

                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            applicationIdSuffix = it.applicationIdSuffix
                            versionNameSuffix = it.versionNameSuffix
                        }
                    }
                }
            }
        }
    }
}

fun ProductFlavor.configureDevFlavor() {
    // custom configuration for dev flavor
}

fun ProductFlavor.configureProdFlavor() {
    // custom configuration for prod flavor
}