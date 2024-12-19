import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.collegegrad.newsworld"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.collegegrad.newsworld"
        minSdk = 24
        targetSdk = 34
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
            buildConfigField("String", "API_KEY", "\"${getApiKey()}\"")
        }
        debug{
            buildConfigField("String", "API_KEY", "\"${getApiKey()}\"")
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
        buildConfig = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.compose.material3:material3")

    implementation ("com.google.dagger:hilt-android:2.48")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")

    implementation ("io.coil-kt:coil-compose:2.5.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    // Retrofit for network requests
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-simplexml:2.9.0")

    // XML Parsing for RSS Feed
    implementation ("org.simpleframework:simple-xml:2.7.1")
}

fun getApiKey(): String {
    val localProperties = Properties()
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { localProperties.load(it) }
    }
    return localProperties.getProperty("NEWS_API_KEY", "default_api_key")
}
