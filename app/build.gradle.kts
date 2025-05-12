plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    alias(libs.plugins.junit5.android)
}

android {
    namespace = "me.francis.audioplayerwithequalizer"
    compileSdk = 35

    defaultConfig {
        applicationId = "me.francis.audioplayerwithequalizer"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        externalNativeBuild {

            cmake {
                /**
                 * adiciona flags de compilação para C. Está vazio, mas normalmente você poderia
                 * colocar algo como "-std=c++17" para definir a versão da linguagem C++, "-DDEBUG"
                 * para definições de pré-processador, entre outros.
                 */
                cFlags.add("")
            }

            ndk {
                /**
                 * /// opcional ///
                 * filtra quais ABIs (Application Binary Interfaces) serão compiladas.
                 * Apenas dispositivos com essas arquiteturas vão receber os binários nativos.
                 */
                abiFilters.addAll(listOf("armeabi-v7a", "x86", "x86_64", "arm64-v8a"))
            }
        }
    }

    externalNativeBuild {
        cmake {
            // caminho para o seu arquivo CMakeLists.txt
            path = file("src/main/cpp/CMakeLists.txt")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }

        unitTests {
            isIncludeAndroidResources = true
        }


        emulatorSnapshots {
            maxSnapshotsForTestFailures = 2
        }
    }

//    packagingOptions {
//        resources {
//            excludes += "META-INF/LICENSE.md"
//            // You might also need to exclude other META-INF files
//            // if similar conflicts arise with other files
//            // excludes += 'META-INF/NOTICE.md'
//            // excludes += 'META-INF/*.kotlin_module'
//        }
//    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE-notice.md",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.bundles.compose.icons)
    androidTestImplementation(libs.junit)
    androidTestImplementation (libs.junit.junit.jupiter.api)
    androidTestImplementation (libs.junit.jupiter.engine)
//    androidTestImplementation (libs.mockito.core)
    androidTestImplementation (libs.mockito.android)
    androidTestImplementation (libs.mockito.kotlin)
//    androidTestImplementation(libs.mockito.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("org.robolectric:robolectric:4.13")

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // nav controller
    implementation(libs.navigation)
    // modules
    implementation(project(":playbackmodule"))
    implementation(project(":notificationmodule"))
    implementation(project(":equalizermodule"))
}


