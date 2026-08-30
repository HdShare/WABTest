import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val gitVersionCode = rootProject.extra["gitVersionCode"] as Int
val gitVersionSuffix = rootProject.extra["gitVersionSuffix"] as String

val ketStorePath: String? = System.getenv("KEY_STORE_PATH")

android {
    namespace = "io.github.wabtest"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.wabtest"
        minSdk = 28
        targetSdk = 37
        versionCode = gitVersionCode
        versionName = "1.2.1.$gitVersionSuffix"
        buildConfigField("String", "APP_NAME", "\"WABTest\"")
    }

    buildFeatures {
        buildConfig = true
    }

    androidResources {
        additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x78")
    }

    signingConfigs {
        create("ci") {
            if (!ketStorePath.isNullOrBlank()) {
                storeFile = file(ketStorePath)
                storePassword = System.getenv("KEY_STORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                enableV2Signing = true
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("ci")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources {
            excludes += "**"
        }
        dex {
            useLegacyPackaging = true
        }
    }

    applicationVariants.all {
        outputs.filterIsInstance<BaseVariantOutputImpl>()
            .forEach { output ->
                val projectName = rootProject.name
                output.outputFileName = "${projectName}-v$versionName.apk"
            }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs.addAll(
            listOf(
                "-Xno-call-assertions",
                "-Xno-param-assertions",
                "-Xno-receiver-assertions"
            )
        )
    }
}

dependencies {
    compileOnly(libs.annotation)
    compileOnly(libs.xposed.api)
    implementation(libs.yukihookapi.api) {
        exclude(group = "androidx.appcompat", module = "appcompat")
        exclude(group = "androidx.preference", module = "preference-ktx")
        exclude(group = "com.google.android.material", module = "material")
    }
    ksp(libs.yukihookapi.ksp.xposed)
    implementation(platform(libs.kavaref.bom))
    implementation(libs.kavaref.android)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    implementation(libs.kotlinx.serialization.json)
}
