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

//    implementation(libs.core.ktx)
//    implementation(libs.lifecycle.runtime.ktx)
//    implementation(libs.activity.compose)

//    implementation(libs.bundles.hilt)
//    ksp(libs.hilt.compiler)
//    ksp(libs.hilt.android.compiler)
//
//    implementation(libs.androidx.room)
//    ksp(libs.androidx.room.compiler)
//    implementation(libs.androidx.room.ktx)

    implementation(libs.nuclominus.imforge)
}
