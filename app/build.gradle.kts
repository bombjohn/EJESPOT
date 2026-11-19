import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

// La clave de OpenRouter se lee de local.properties (ignorado por Git) y se
// expone como BuildConfig; así no queda embebida como literal en el código.
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
val openRouterApiKey = localProperties.getProperty("openrouter.api.key", "")

android {
    namespace = "com.example.ejespot"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.ejespot"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "OPENROUTER_API_KEY", "\"${openRouterApiKey}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            optimization {
                enable = false
            }
        }
    }

    // Se incluyen todos los idiomas en el APK. Sin esto, un App Bundle
    // separa los idiomas y el selector de la app no puede cambiar al que
    // no esté instalado (advertencia `AppBundleLocaleChanges`).
    bundle {
        language {
            enableSplit = false
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

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    // Carga de imágenes desde Firebase Storage (URLs https) en el feed y el detalle.
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.osmdroid.android)
    implementation(libs.androidx.data.store.preferences)
    implementation(libs.androidx.lifecycle.runtime.compose)
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}