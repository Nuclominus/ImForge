import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("io.nuclominus.android.library")
    alias(libs.plugins.detekt.analyzer)
    `maven-publish`
    signing
}

android {
    namespace = "io.github.nuclominus.imforge"
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.exifinterface)
}


val sourcesJar by tasks.registering(Jar::class) {
    from(android.sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sourcesJar")
}

val localProps = Properties()
val localProperties = File(rootProject.rootDir, "local.properties")
if (localProperties.exists() && localProperties.isFile) {
    localProperties.inputStream().use { localProps.load(it) }
}

project.extra["signing.keyId"] = localProps.getProperty("signing.keyId")
project.extra["signing.secretKeyRingFile"] = localProps.getProperty("signing.secretKeyRingFile")
project.extra["signing.password"] = localProps.getProperty("signing.password")

afterEvaluate {

    val groupId by extra { "io.github.nuclominus" }
    val artifactId by extra { "imforge" }
    val version by extra { "1.0.2" }

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifact(sourcesJar)

                this.groupId = groupId
                this.artifactId = artifactId
                this.version = version

                pom {
                    name.set("ImForge")
                    description.set("A bitmap optimizer with supporting popular formats")
                    url.set("https://github.com/Nuclominus/ImForge")

                    licenses {
                        license {
                            name.set("The MIT License")
                        }
                    }

                    developers {
                        developer {
                            id.set("nuclominus")
                            name.set("Roman Kosko")
                            email.set("9DGRoman@gmail.com")
                        }
                    }

                    scm {
                        url.set("https://github.com/Nuclominus/ImForge")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "sonatypeStaging"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = localProps.getProperty("ossrhUsername")
                    password = localProps.getProperty("ossrhPassword")
                }
            }
        }
    }

    signing {
        sign(publishing.publications)
    }
}