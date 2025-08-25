package data

import org.gradle.api.JavaVersion

object AndroidAppConf {
    const val APP_VERSION: String = "1.0.3"
    const val VERSION_CODE: Int = 8
    const val NAMESPACE: String = "io.github.nuclominus.imforge.app"
    const val COMPILE_SDK: Int = 36
    const val MIN_SDK: Int = 26
    val javaVersion: JavaVersion = JavaVersion.VERSION_19
}

object LibConf {
    const val LIB_VERSION: String = "1.0.6"
    const val NAMESPACE: String = "io.github.nuclominus.imforge"
}

object MavenConf {
    const val GROUP_ID: String = "io.github.nuclominus"
    const val ARTIFACT_ID: String = "imforge"
    const val ARTIFACT_NAME: String = "ImForge"
    const val DESCRIPTION: String = "A bitmap optimizer with supporting popular formats"
    const val URL: String = "https://github.com/Nuclominus/ImForge"
    const val LICENSE_NAME: String = "The MIT License"
    const val LICENSE_URL: String = "https://github.com/Nuclominus/ImForge?tab=MIT-1-ov-file"
    const val DEVELOPER_ID: String = "Nuclominus"
    const val DEVELOPER_NAME: String = "Roman Kosko"
    const val DEVELOPER_EMAIL: String = "9DGRoman@gmail.com"
    const val SCM_URL: String = "https://github.com/Nuclominus/ImForge"
    const val MAVEN_URL: String = "https://central.sonatype.com/api/v1/publisher"
}


