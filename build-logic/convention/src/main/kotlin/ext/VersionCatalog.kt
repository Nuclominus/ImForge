package ext

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType

/**
 * Get the version catalog for the project
 */
internal fun Project.versionCatalog(): VersionCatalog =
    extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Add a library to the dependencies
 */
internal fun DependencyHandlerScope.addLibrary(
    versionCatalog: VersionCatalog,
    alias: String,
    configuration: Configurations = Configurations.Implementation,
): Dependency? {
    return dependencies.add(
        configuration.value,
        versionCatalog.findLibrary(alias).get()
    )
}

/**
 * Add a platform to the dependencies
 */
internal fun DependencyHandlerScope.addPlatform(
    versionCatalog: VersionCatalog,
    alias: String,
    configuration: Configurations = Configurations.Implementation,
): Dependency? {
    return dependencies.add(
        configuration.value,
        platform(versionCatalog.findLibrary(alias).get())
    )
}

/**
 * Add a bundle to the dependencies
 */
internal fun DependencyHandlerScope.addBundle(
    versionCatalog: VersionCatalog,
    alias: String
): Dependency? {
    return dependencies.add(
        "implementation",
        versionCatalog.findBundle(alias).get()
    )
}

enum class Configurations(val value: String) {
    Implementation("implementation"),
    TestImplementation("androidTestImplementation"),
    DebugImplementation("debugImplementation"),
    Ksp("ksp"),
}