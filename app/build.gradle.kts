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
}

dependencies {
    implementation(libs.nuclominus.imforge)
}
