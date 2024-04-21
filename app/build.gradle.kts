plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.CollectorsCorner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.CollectorsCorner"
        minSdk = 26
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Exclude resources using the resources block
    packaging {
        resources {
            excludes.add("kotlin/internal/internal.kotlin_builtins")
            excludes.add("kotlin/reflect/reflect.kotlin_builtins")
            excludes.add("kotlin/collections/collections.kotlin_builtins")
            excludes.add("kotlin/kotlin.kotlin_builtins")
            excludes.add("kotlin/coroutines/coroutines.kotlin_builtins")
            excludes.add("kotlin/annotation/annotation.kotlin_builtins")
            excludes.add("kotlin/ranges/ranges.kotlin_builtins")
        }
    }
}



dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("org.jetbrains.kotlin:kotlin-android-extensions:1.9.22")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // This is the line with the updated dependency for Kotlin that fixes the Kotlin issues, please do not remove
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.22") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }

}