import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "SharedLogic"      // iOS imports `import SharedLogic`
            isStatic = true
        }
    }
    android {
        namespace = "com.cte.examiner.sharedLogic"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)        // Ktor Android engine
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)         // Ktor iOS engine
        }
        sourceSets {
            commonMain.dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.coroutines)
            }
            androidMain.dependencies {
                implementation(libs.ktor.client.okhttp)    // Android HTTP engine
            }
            iosMain.dependencies {
                implementation(libs.ktor.client.darwin)    // iOS HTTP engine
            }
        }
    }
}