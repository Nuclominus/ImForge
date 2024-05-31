@file:Suppress("UnstableApiUsage")

rootProject.name = "ImageCompressor"

pluginManagement {
    includeBuild("build-logic")
    gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
include(":imagecompressor")
