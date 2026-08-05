import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val vueProjectDir = file("src/main/vue/wabtest")
val vueDistDir = file("$vueProjectDir/dist")
val npmCmd = if (System.getProperty("os.name").startsWith("Windows")) "npm.cmd" else "npm"

val ketStorePath: String? = System.getenv("KEY_STORE_PATH")

android {
    namespace = "me.hd.wabtest"
    compileSdk = 37

    sourceSets {
        named("main") {
            assets {
                srcDirs(vueDistDir.path)
            }
        }
    }

    defaultConfig {
        applicationId = "me.hd.wabtest"
        minSdk = 27
        targetSdk = 37
        versionCode = 26053001
        versionName = "1.0.10"
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

val npmInstallVueDeps = tasks.register("npmInstallVueDeps") {
    group = "wabtest"
    description = "Install dependencies for Vue settings page"
    inputs.files(file("$vueProjectDir/package.json"), file("$vueProjectDir/package-lock.json"))
    outputs.dir(file("$vueProjectDir/node_modules"))
    doLast {
        exec {
            workingDir = vueProjectDir
            commandLine(npmCmd, "install")
        }
    }
}

val buildVueSettingsPage = tasks.register("buildVueSettingsPage") {
    group = "wabtest"
    description = "Build Vue settings page into Android assets"
    dependsOn(npmInstallVueDeps)
    inputs.dir(file("$vueProjectDir/src"))
    inputs.file(file("$vueProjectDir/public/index.html"))
    inputs.file(file("$vueProjectDir/vue.config.js"))
    outputs.dir(vueDistDir)
    doLast {
        exec {
            workingDir = vueProjectDir
            commandLine(npmCmd, "run", "build")
        }
    }
}

tasks.preBuild {
    dependsOn(buildVueSettingsPage)
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
    implementation(libs.kotlinx.serialization.protobuf)
}
