plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.smarttourism"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smarttourism"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //abiFilters.add("armeabi-v7a")
            //设置支持的SO库架构（开发者可以根据需要，选择一个或多个平台的so）
            abiFilters.addAll(arrayOf("armeabi", "armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    //根据不同的cpu架构，在app/build/outputs/apk/下生成对应的apk
    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "armeabi-v7a", "x86_64")
            isUniversalApk = true
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
        getByName("debug").setRoot("build-types/debug")
        getByName("release").setRoot("build-types/release")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.activity:activity:1.2.0")
    implementation("androidx.fragment:fragment:1.3.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.android.material:material:1.10.0")
    //Firebase Analytics SDK
    implementation("com.google.firebase:firebase-analytics:17.4.1")
    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}