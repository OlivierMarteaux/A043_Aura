plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
}

android {
  namespace = "com.aura"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.aura"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {

  //initial
  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.8.0")
  implementation("androidx.annotation:annotation:1.6.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

  //activity (viewModel())
  implementation("androidx.activity:activity-ktx:1.8.2")

  // lifecycle (lifecycleScope)
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

  // Retrofit+KotlinxSerializationJsonParsing
  implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.okhttp3:okhttp:4.11.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

  //DataStore
  implementation("androidx.datastore:datastore-preferences:1.0.0")


//  //Hilt
//  val hiltVersion = "2.44"
//  implementation("com.google.dagger:hilt-android:${hiltVersion}")
//  annotationProcessor("com.google.dagger:hilt-compiler:${hiltVersion}")
}