import com.android.aaptcompiler.shouldIgnoreElement
import com.android.build.gradle.internal.packaging.defaultExcludes
import javax.xml.namespace.QName

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("io.nuclominus.android.application")
    alias(libs.plugins.detekt.analyzer)
}

android {

    defaultConfig {
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "dexguard-release.txt",
            )
            setProperty("archivesBaseName", "ImForge")
        }

        val debug by getting {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "dexguard-release.txt",
            )
            setProperty("archivesBaseName", "ImForge")
        }
    }

    detekt {
        source.setFrom("src/main/java")
        config.setFrom("../config/detekt/detekt.yaml")
        buildUponDefaultConfig = true
        tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
            exclude("**/theme/**/*.kt")
        }
    }
}

dependencies {
    implementation(libs.nuclominus.imforge)
}
