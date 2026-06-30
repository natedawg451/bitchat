plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.glyphdice"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.glyphdice"
        // Glyph Matrix SDK requires Android 14 (API 34) or newer.
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Proprietary Nothing SDK — download GlyphMatrixSDK.aar from
    // https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit
    // and place it in app/libs/. It is NOT included in this repo.
    implementation(files("libs/GlyphMatrixSDK.aar"))
}
