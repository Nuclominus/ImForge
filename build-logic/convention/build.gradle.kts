plugins {
    `kotlin-dsl`
}

group = "io.nuclominus.buildlogic.convention"

java {
    sourceSets {
        main {
            kotlin {
                srcDir("src/main/kotlin")
            }
        }
    }
}

dependencies {
    compileOnly(libs.detekt.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("application") {
            id = "io.nuclominus.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("library") {
            id = "io.nuclominus.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}