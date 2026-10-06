import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    //firebase-services
    alias(libs.plugins.google.services)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)

    //firebase-services
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(libs.chottulink.android)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun localProperty(name: String): String = localProperties.getProperty(name).orEmpty().trim()

val chottuLinkApiKey = localProperty("CHOTTULINK_API_KEY")
val chottuLinkDomainRaw = localProperty("CHOTTULINK_DOMAIN")
val chottuLinkDomain = chottuLinkDomainRaw.ifBlank { "yourapp.chottu.link" }

android {
    namespace = "com.example.composemultiplatform"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.composemultiplatform"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "CHOTTULINK_API_KEY", "\"${chottuLinkApiKey.escapeForBuildConfig()}\"")
        buildConfigField("String", "CHOTTULINK_DOMAIN", "\"${chottuLinkDomain.escapeForBuildConfig()}\"")
        buildConfigField("boolean", "CHOTTULINK_CONFIGURED", "${chottuLinkApiKey.isNotBlank() && chottuLinkDomainRaw.isNotBlank()}")
        manifestPlaceholders["chottuLinkHost"] = chottuLinkDomain
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

private fun String.escapeForBuildConfig(): String =
    replace("\\", "\\\\").replace("\"", "\\\"")