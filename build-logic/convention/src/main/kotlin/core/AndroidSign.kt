package core

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * Configure project signing
 */
internal fun Project.configureSigningAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {

        signingConfigs {
            val keystoreProperties = projectDir.getProps("/keystore.properties")

            create("release") {
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                storeFile = file(keystoreProperties.getProperty("keyStoreFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
            }
        }
    }
}

private fun File.getProps(filePath: String) = Properties().apply {
    load(FileInputStream(File(parent + filePath)))
}