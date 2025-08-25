import data.LibConf
import data.MavenConf
import org.jreleaser.model.Active

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.nuclominus.library)
    alias(libs.plugins.detekt.analyzer)
    id("org.jreleaser") version "1.19.0"
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

project.ext["signing.keyId"] = System.getenv("SIGN_KEY_ID")
project.ext["signing.secretKeyRingFile"] = System.getenv("SIGN_KEY")
project.ext["signing.password"] = System.getenv("SIGN_KEY_PASS")

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

version = LibConf.LIB_VERSION
description = MavenConf.DESCRIPTION


publishing {
    publications {
        create<MavenPublication>("release") {
            artifact(sourcesJar)

            this.groupId = MavenConf.GROUP_ID
            this.artifactId = MavenConf.ARTIFACT_ID

            pom {
                name.set(MavenConf.ARTIFACT_NAME)
                description.set(MavenConf.DESCRIPTION)
                url.set(MavenConf.URL)

                licenses {
                    license {
                        name.set(MavenConf.LICENSE_NAME)
                        url.set(MavenConf.LICENSE_URL)
                        description.set("repo")
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

                afterEvaluate {
                    from(components["release"])
                }
            }
        }
    }

    repositories {
        maven {
           setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    project {
        inceptionYear = "2023"
        author(MavenConf.DEVELOPER_NAME)
    }
    gitRootSearch = true
    signing {
        active = Active.ALWAYS
        armored = true
        verify = true
    }
    release {
        github {
            skipRelease = true
            skipTag = true
        }
    }
    deploy {
        maven {
            mavenCentral.create("sonatype") {
                active = Active.ALWAYS
                url = MavenConf.MAVEN_URL
                stagingRepository(layout.buildDirectory.dir("staging-deploy").get().toString())
                setAuthorization("Basic")
                applyMavenCentralRules = false // Wait for fix: https://github.com/kordamp/pomchecker/issues/21
                sign = true
                checksums = true
                sourceJar = true
                javadocJar = true
                retryDelay = 60
            }
        }
    }
}