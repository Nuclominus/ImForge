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

detekt {
    source.setFrom("src/main/kotlin")
    // preconfigure defaults
    buildUponDefaultConfig = true
    // activate all available (even unstable) rules
    allRules = false
    // point to your custom config defining rules to run, overwriting default behavior
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    // a way of suppressing issues before introducing detekt
    baseline = file("$projectDir/config/baseline.xml")
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

project.ext["signing.keyId"] = System.getenv("SIGN_KEY_ID")
project.ext["signing.secretKeyRingFile"] = System.getenv("SIGN_KEY")
project.ext["signing.password"] = System.getenv("SIGN_KEY_PASS")

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
                    username = System.getenv(MavenConf.OSSRH_USERNAME)
                    password = System.getenv(MavenConf.OSSRH_PASSWORD)
                }
            }
        }
    }

    signing {
        sign(publishing.publications)
    }
}