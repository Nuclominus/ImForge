package core

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Configure project signing
 */
@OptIn(ExperimentalEncodingApi::class)
internal fun Project.configureSigningAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {

        signingConfigs {
            create("release") {
                val keyStore = System.getenv("APP_KEY_STORE")
                    ?: throw IllegalArgumentException("APP_KEY_STORE is not set")
                val bytes = Base64.Default.decode(keyStore)
                storeFile = file(File.createTempFile("keystore", ".jks").apply {
                    writeBytes(bytes)
                })
                storePassword = System.getenv("APP_KEY_STORE_PASSWORD")
                keyAlias = System.getenv("APP_ALIAS")
                keyPassword = System.getenv("APP_KEY_PASSWORD")
            }
        }
    }
}