import data.LibConf
import data.MavenConf
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("io.nuclominus.android.library")
    alias(libs.plugins.detekt.analyzer)
    `maven-publish`
    signing
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

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                artifact(sourcesJar)

                this.groupId = MavenConf.GROUP_ID
                this.artifactId = MavenConf.ARTIFACT_ID
                this.version = LibConf.LIB_VERSION

                pom {
                    name.set(MavenConf.ARTIFACT_NAME)
                    description.set(MavenConf.DESCRIPTION)
                    url.set(MavenConf.URL)

                    licenses {
                        license {
                            name.set(MavenConf.LICENSE_NAME)
                        }
                    }

                    developers {
                        developer {
                            id.set(MavenConf.DEVELOPER_ID)
                            name.set(MavenConf.DEVELOPER_NAME)
                            email.set(MavenConf.DEVELOPER_EMAIL)
                        }
                    }

                    scm {
                        url.set(MavenConf.SCM_URL)
                    }
                }
            }
        }

        repositories {
            maven {
                name = MavenConf.MAVEN_NAME
                url = uri(MavenConf.MAVEN_URL)
                credentials {
                    username = localProps.getProperty(MavenConf.OSSRH_USERNAME)
                    password = localProps.getProperty(MavenConf.OSSRH_PASSWORD)
                }
            }
        }
    }

    signing {
        sign(publishing.publications)
    }
}