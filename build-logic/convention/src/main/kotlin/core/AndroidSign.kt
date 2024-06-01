package core

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

/**
 * Configure project signing
 */
internal fun Project.configureSigningAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {

        signingConfigs {
            create("release") {
                keyAlias = System.getenv("ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                storeFile = file(System.getenv("SIGNING_KEY"))
                storePassword = System.getenv("KEY_STORE_PASSWORD")
            }
        }
    }
}