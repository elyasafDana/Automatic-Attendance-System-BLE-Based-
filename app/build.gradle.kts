plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.adplusscan"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.adplusscan"
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
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
// ספרייה להמרת הטקסט מהשרת לאובייקטים ב-Java
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.0")

// ספריית ה-Authentication (בשביל הרישום עם גוגל)
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.5.0")

// ספריית ה-HTTP - סופבייס צריכה אותה כדי לדבר עם האינטרנט
    implementation("io.ktor:ktor-client-android:2.3.5")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}