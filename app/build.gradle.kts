plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrainsKotlinSerialization)

}

android {
    namespace = "com.example.ratonean2_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ratonean2_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/*.kotlin_module"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/*.txt"
            excludes += "META-INF/*.md"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    // Using bundles to clean up redundant lines
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.play.services)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.compose.testing)
    implementation(libs.bundles.test.libraries)
    testImplementation(libs.bundles.junit5)

    // Core testing dependencies (these are in the `testing` bundle)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))


    // Bundles for testing Ktor
    testImplementation(libs.bundles.ktor.testing)

    // Other dependencies
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("io.insert-koin:koin-core:3.5.6")
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-androidx-compose:3.5.6")
    implementation(libs.gids.signin)
    implementation(libs.gids.auth.api.phone)
    testImplementation(kotlin("test"))
}